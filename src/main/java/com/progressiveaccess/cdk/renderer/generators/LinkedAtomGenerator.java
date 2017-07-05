
package com.progressiveaccess.cdk.renderer.generators;

import javax.vecmath.Point2d;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.MarkedElement;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;

import com.progressiveaccess.cdk.renderer.Cli;
import com.progressiveaccess.cdk.renderer.elements.AtomSymbolElement;
import com.progressiveaccess.cdk.renderer.elements.ElementGroup;
import com.progressiveaccess.cdk.renderer.elements.ILinkedElement;
import com.progressiveaccess.cdk.renderer.elements.RectangleElement;
import com.progressiveaccess.cdk.renderer.elements.OvalElement;

public class LinkedAtomGenerator extends BasicAtomGenerator {
  @Override
  public AtomSymbolElement generateElement(IAtom atom, int alignment, RendererModel model) {
    String text;
    if (atom instanceof IPseudoAtom) {
      text = ((IPseudoAtom) atom).getLabel();
    } else {
      text = atom.getSymbol();
    }
    return new AtomSymbolElement(atom.getPoint2d().x, atom.getPoint2d().y, text, atom.getFormalCharge(),
        atom.getImplicitHydrogenCount(), alignment, getAtomColor(atom, model));
  }

  @Override
  public IRenderingElement generate(final IAtomContainer atomContainer, final IAtom atom, final RendererModel model) {
    IRenderingElement result;
    final Boolean showExplicitHydrogens = model.hasParameter(BasicAtomGenerator.ShowExplicitHydrogens.class)
        ? model.get(BasicAtomGenerator.ShowExplicitHydrogens.class)
        : model.getDefault(BasicAtomGenerator.ShowExplicitHydrogens.class);
    if (showExplicitHydrogens && !this.canDraw(atom, atomContainer, model)) {
      result = null;
    } else if (Cli.hasOption("exclude_rings") && atom.getSymbol().equals("C") && atom.isAromatic()) {
      result = null;
    } else if (model.get(CompactAtom.class)) {
      result = this.generateCompactElement(atom, model);
    } else {
      int alignment;
      if (atom.getSymbol().equals("C")) {
        alignment = GeometryTools.getBestAlignmentForLabel(atomContainer, atom);
      } else {
        alignment = GeometryTools.getBestAlignmentForLabelXY(atomContainer, atom);
      }

      result = this.generateElement(atom, alignment, model);
    }
    
    if(result instanceof ILinkedElement) {
      ((ILinkedElement) result).setChemicalObject(atom);
    }

    return result;
  }

  @Override
  public IRenderingElement generate(IAtomContainer container, RendererModel model) {
    ElementGroup elementGroup = new ElementGroup();
    for (IAtom atom : container.atoms()) {
      elementGroup.add(MarkedElement.markupAtom(this.generate(container, atom, model), atom));
    }
    return elementGroup;
  }

  @Override
  public IRenderingElement generateCompactElement(IAtom atom, RendererModel model) {
    Point2d point = atom.getPoint2d();
    double radius = (Double) model.get(AtomRadius.class) / model.getParameter(Scale.class).getValue();
    double distance = 2 * radius;
    if (model.get(CompactShape.class) == Shape.SQUARE) {
      return new RectangleElement(point.x - radius, point.y - radius, distance, distance, true,
          getAtomColor(atom, model));
    } else {
      return new OvalElement(point.x, point.y, radius, true, getAtomColor(atom, model));
    }
  }
}
