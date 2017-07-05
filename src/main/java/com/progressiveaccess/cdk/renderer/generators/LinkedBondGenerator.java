package com.progressiveaccess.cdk.renderer.generators;

import java.awt.Color;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.MarkedElement;
import org.openscience.cdk.renderer.elements.WedgeLineElement.Direction;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.ShowExplicitHydrogens;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;
import org.openscience.cdk.tools.manipulator.AtomContainerComparatorBy2DCenter;

import com.progressiveaccess.cdk.renderer.elements.ElementGroup;
import com.progressiveaccess.cdk.renderer.elements.ILinkedElement;
import com.progressiveaccess.cdk.renderer.elements.LineElement;
import com.progressiveaccess.cdk.renderer.elements.WedgeLineElement;

public class LinkedBondGenerator extends BasicBondGenerator {
  private int IDEAL_RINGSIZE = 6;
  private double MIN_RINGSIZE_FACTOR = 2.5;

  @Override
  public IRenderingElement generate(IAtomContainer container, RendererModel model) {
    ElementGroup group = new ElementGroup();
    this.ringSet = this.getRingSet(container);

    // Sort the ringSet consistently to ensure consistent rendering.
    // If this is omitted, the bonds may 'tremble'.
    ringSet.sortAtomContainers(new AtomContainerComparatorBy2DCenter());

    for (IBond bond : container.bonds()) {
      IRenderingElement result = this.generate(bond, model);
      if (result instanceof ILinkedElement) {
        ((ILinkedElement) result).setChemicalObject(bond);
      }
      group.add(MarkedElement.markupBond(result, bond));
    }
    return group;
  }

  @Override
  public IRenderingElement generateBondElement(IBond bond, IBond.Order type, RendererModel model) {
    // More than 2 atoms per bond not supported by this module
    if (bond.getAtomCount() > 2)
      return null;

    ILinkedElement result = null;

    // is object right? if not replace with a good one
    Point2d point1 = bond.getBegin().getPoint2d();
    Point2d point2 = bond.getEnd().getPoint2d();
    Color color = this.getColorForBond(bond, model);
    double bondWidth = this.getWidthForBond(bond, model);
    double bondDistance = (Double) model.get(BondDistance.class) / model.getParameter(Scale.class).getValue();
    if (type == IBond.Order.SINGLE) {
      result = new LineElement(point1.x, point1.y, point2.x, point2.y, bondWidth, color);
    } else {
      ElementGroup group = new ElementGroup();
      switch (type) {
      case DOUBLE:
        createLines(point1, point2, bondWidth, bondDistance, color, group);
        break;
      case TRIPLE:
        createLines(point1, point2, bondWidth, bondDistance * 2, color, group);
        group.add(new LineElement(point1.x, point1.y, point2.x, point2.y, bondWidth, color));
        break;
      case QUADRUPLE:
        createLines(point1, point2, bondWidth, bondDistance, color, group);
        createLines(point1, point2, bondWidth, bondDistance * 4, color, group);
      default:
        break;
      }
      result = group;
    }

    return result;
  }

  @Override
  public IRenderingElement generateRingElements(IBond bond, IRing ring, RendererModel model) {
    if (isSingle(bond) && isStereoBond(bond)) {
      return generateStereoElement(bond, model);
    } else if (isDouble(bond)) {
      ElementGroup pair = new ElementGroup();
      pair.add(generateBondElement(bond, IBond.Order.SINGLE, model));
      pair.add(generateInnerElement(bond, ring, model));
      return pair;
    } else {
      return generateBondElement(bond, model);
    }
  }

  private IRenderingElement generateStereoElement(IBond bond, RendererModel model) {

    IBond.Stereo stereo = bond.getStereo();
    WedgeLineElement.TYPE type = WedgeLineElement.TYPE.WEDGED;
    Direction dir = Direction.toSecond;
    if (stereo == IBond.Stereo.DOWN || stereo == IBond.Stereo.DOWN_INVERTED)
      type = WedgeLineElement.TYPE.DASHED;
    if (stereo == IBond.Stereo.UP_OR_DOWN || stereo == IBond.Stereo.UP_OR_DOWN_INVERTED)
      type = WedgeLineElement.TYPE.INDIFF;
    if (stereo == IBond.Stereo.DOWN_INVERTED || stereo == IBond.Stereo.UP_INVERTED
        || stereo == IBond.Stereo.UP_OR_DOWN_INVERTED)
      dir = Direction.toFirst;

    IRenderingElement base = generateBondElement(bond, IBond.Order.SINGLE, model);
    return new WedgeLineElement((LineElement) base, type, dir, getColorForBond(bond, model));
  }

  private void createLines(Point2d point1, Point2d point2, double width, double dist, Color color, ElementGroup group) {
    double[] out = generateDistanceData(point1, point2, dist);
    LineElement l1 = new LineElement(out[0], out[1], out[4], out[5], width, color);
    LineElement l2 = new LineElement(out[2], out[3], out[6], out[7], width, color);
    group.add(l1);
    group.add(l2);
  }

  private double[] generateDistanceData(Point2d point1, Point2d point2, double dist) {
    Vector2d normal = new Vector2d();
    normal.sub(point2, point1);
    normal = new Vector2d(-normal.y, normal.x);
    normal.normalize();
    normal.scale(dist);

    Point2d line1p1 = new Point2d();
    Point2d line1p2 = new Point2d();
    line1p1.add(point1, normal);
    line1p2.add(point2, normal);

    normal.negate();
    Point2d line2p1 = new Point2d();
    Point2d line2p2 = new Point2d();
    line2p1.add(point1, normal);
    line2p2.add(point2, normal);

    return new double[] { line1p1.x, line1p1.y, line2p1.x, line2p1.y, line1p2.x, line1p2.y, line2p2.x, line2p2.y };
  }

  public LineElement generateInnerElement(IBond bond, IRing ring, RendererModel model) {
    Point2d center = GeometryUtil.get2DCenter(ring);
    Point2d a = bond.getBegin().getPoint2d();
    Point2d b = bond.getEnd().getPoint2d();

    // the proportion to move in towards the ring center
    double distanceFactor = model.getParameter(TowardsRingCenterProportion.class).getValue();
    double ringDistance = distanceFactor * IDEAL_RINGSIZE / ring.getAtomCount();
    if (ringDistance < distanceFactor / MIN_RINGSIZE_FACTOR)
      ringDistance = distanceFactor / MIN_RINGSIZE_FACTOR;

    Point2d w = new Point2d();
    w.interpolate(a, center, ringDistance);
    Point2d u = new Point2d();
    u.interpolate(b, center, ringDistance);

    double alpha = 0.2;
    Point2d ww = new Point2d();
    ww.interpolate(w, u, alpha);
    Point2d uu = new Point2d();
    uu.interpolate(u, w, alpha);

    double width = getWidthForBond(bond, model);
    Color color = getColorForBond(bond, model);

    return new LineElement(u.x, u.y, w.x, w.y, width, color);
  }

  private boolean isDouble(IBond bond) {
    return bond.getOrder() == IBond.Order.DOUBLE;
  }

  private boolean isSingle(IBond bond) {
    return bond.getOrder() == IBond.Order.SINGLE;
  }

  private boolean isStereoBond(IBond bond) {
    return bond.getStereo() != IBond.Stereo.NONE && bond.getStereo() != (IBond.Stereo) CDKConstants.UNSET
        && bond.getStereo() != IBond.Stereo.E_Z_BY_COORDINATES;
  }

  @Override
  public IRenderingElement generateBond(IBond bond, RendererModel model) {
    final Boolean showExplicitHydrogens = model.hasParameter(BasicAtomGenerator.ShowExplicitHydrogens.class)
        ? model.get(BasicAtomGenerator.ShowExplicitHydrogens.class)
        : model.getDefault(BasicAtomGenerator.ShowExplicitHydrogens.class);

    if (!showExplicitHydrogens && bindsHydrogen(bond)) {
      return null;
    }

    if (isStereoBond(bond)) {
      return this.generateStereoElement(bond, model);
    } else {
      return this.generateBondElement(bond, model);
    }
  }
}
