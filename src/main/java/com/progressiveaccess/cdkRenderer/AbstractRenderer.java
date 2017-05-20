/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates and open the template
 * in the editor.
 */

package com.progressiveaccess.cdkRenderer;

// i'm being nice
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.BoundsCalculator;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.AtomSymbolElement;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.WedgeLineElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.ZoomFactor;
import org.openscience.cdk.renderer.generators.IGenerator;

import org.openscience.cdk.renderer.elements.MarkedElement;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.vecmath.Point2d;
import org.openscience.cdk.renderer.elements.OvalElement;

/**
 *
 *
 * @author sacko
 * @param <T>
 */
public abstract class AbstractRenderer<T> {
  /**
   *
   */
  protected final double DEFAULT_SCALE = 30;

  /**
   *
   */
  protected final double DEFAULT_XPAD = 2;

  /**
   *
   */
  protected final double DEFAULT_YPAD = 2;

  /**
   *
   */
  protected final Font DEFAULT_FONT = new Font("serif", Font.PLAIN, 15);

  /**
   *
   */
  private Color color;

  /**
   *
   */
  private BasicStroke stroke;

  /**
   *
   */
  protected AffineTransform transform;

  /**
   *
   */
  private final RendererModel model;

  /**
   *
   */
  private Point2d modelCentre = new Point2d(0, 0);

  /**
   *
   */
  private Point2d drawingCentre = new Point2d(100, 100);

  private Rectangle2D boundBox;

  /**
   *
   */
  private final List<IGenerator<IAtomContainer>> generators;

  /**
   *
   * @param model
   */
  protected AbstractRenderer(final RendererModel model,
      final List<IGenerator<IAtomContainer>> generators) {
    this.model = model;
    this.generators = generators;
    for (final IGenerator<IAtomContainer> generator : this.generators) {
      this.model.registerParameters(generator);
    }

    this.updateTransformer();
  }

  /**
   *
   * @return
   */
  public Color getColor() {
    return this.color;
  }

  /**
   *
   * @param color
   */
  public void setColor(final Color color) {
    this.color = color;
  }

  /**
   *
   * @return
   */
  public Point2d getModelCentre() {
    return this.modelCentre;
  }

  /**
   *
   * @param modelCentre
   */
  public void setModelCentre(final Point2d modelCentre) {
    this.modelCentre = modelCentre;
    this.updateTransformer();
  }

  /**
   *
   * @return
   */
  public Point2d getDrawingCentre() {
    return this.drawingCentre;
  }

  /**
   *
   * @param drawingCentre
   */
  public void setDrawingCentre(final Point2d drawingCentre) {
    this.drawingCentre = drawingCentre;
    this.updateTransformer();
  }

  /**
   *
   * @return
   */
  public final Stroke getStroke() {
    return this.stroke;
  }

  /**
   *
   * @param stroke
   */
  public final void setStroke(final Stroke stroke) {
    if (stroke instanceof BasicStroke) {
      this.stroke = (BasicStroke) stroke;
    }
  }

  /**
   *
   * @return
   */
  public final Double getScale() {
    return this.getModel().getParameter(Scale.class).getValue();
  }

  /**
   *
   * @param atomContainer
   */
  public final void setScale(final IAtomContainer atomContainer) {
    this.getModel()
    .getParameter(Scale.class)
    .setValue(
        this.calculateScaleForBondLength(GeometryTools
            .getBondLengthAverage(atomContainer)));
    this.updateTransformer();
  }

  /**
   *
   * @return
   */
  public final Double getZoom() {
    return this.getModel().getParameter(ZoomFactor.class).getValue();
  }

  /**
   *
   * @param value
   */
  public final void setZoom(final Double value) {
    this.getModel().getParameter(ZoomFactor.class).setValue(value);
    this.updateTransformer();
  }

  /**
   *
   * @return
   */
  public RendererModel getModel() {
    return this.model;
  }

  /**
   *
   * @return
   */
  public List<IGenerator<IAtomContainer>> getGenerators() {
    return this.generators;
  }

  /**
   *
   * @param x
   * @param y
   * @return
   */
  protected Point2d XY(final Double x, final Double y) {
    final double[] i = new double[] {
        x, y
    };
    this.transform.transform(i, 0, i, 0, 1);
    return new Point2d(i);
  }

  /**
   *
   * @param element
   * @return
   */
  protected abstract Point2d WH(T element);

  /**
   *
   */
  protected final void updateTransformer() {
    this.transform = new AffineTransform();
    this.transform.translate(this.drawingCentre.x, this.drawingCentre.y);
    this.transform.scale(1, -1);
    this.transform.scale(this.getScale(), this.getScale());
    this.transform.scale(this.getZoom(), this.getZoom());
    this.transform.translate(-this.modelCentre.x, -this.modelCentre.y);
  }

  /**
   * Given a bond length for a model, calculate the scale that will transform
   * this length to the on screen bond length in RendererModel.
   *
   * @param bondLenght
   *          the average bond length of the model
   * @return the scale necessary to transform this to a screen bond
   */
  public double calculateScaleForBondLength(final Double bondLenght) {
    if (Double.isNaN(bondLenght) || bondLenght == 0) {
      return this.DEFAULT_SCALE;
    } else {
      return this.getModel().getParameter(
          BasicSceneGenerator.BondLength.class).getValue() / bondLenght;
    }
  }

  /**
   *
   * @param atomContainer
   * @param width
   * @param height
   * @return
   */
  public T render(final IAtomContainer atomContainer) {
    this.boundBox = BoundsCalculator.calculateBounds(atomContainer);
    this.setScale(atomContainer);
    this.setDrawingCentre(new Point2d(this.getWidth() / 2, this.getHeight() / 2));
    final ElementGroup diagram = new ElementGroup();
    for (final IGenerator<IAtomContainer> generator : this.getGenerators()) {
      diagram.add(generator.generate(atomContainer, this.getModel()));
    }
    return this.render(diagram, atomContainer);
  }

  /**
   *
   * @param element
   * @param atomContainer
   * @return
   */
  protected T render(final IRenderingElement element,
      final IAtomContainer atomContainer) {
    final Rectangle2D boundBox = BoundsCalculator
        .calculateBounds(atomContainer);
    this.setModelCentre(new Point2d(boundBox.getCenterX(), boundBox
        .getCenterY()));

    return this.render(element);
  }

  /**
   *
   * @param element
   * @return
   */
  protected T render(IRenderingElement element) {
    // save current colours/stroke
    final Color pColor = this.getColor();
    final Stroke pStroke = this.getStroke();
    T result;
    // generate the result
    if (element instanceof MarkedElement) {
      element = ((MarkedElement) element).element();
    }
    if (element instanceof WedgeLineElement) {
      result = this.render((WedgeLineElement) element);
    } else if (element instanceof LineElement) {
      result = this.render((LineElement) element);
    } else if (element instanceof ElementGroup) {
      result = this.render((ElementGroup) element);
    } else if (element instanceof AtomSymbolElement) {
      result = this.render((AtomSymbolElement) element);
    } else if (element instanceof OvalElement) {
      result = this.render((OvalElement) element);
    } else {
      throw new UnsupportedOperationException(
          "The rendering of " + element.getClass().getCanonicalName()
          + " is not supported.");
    }
    // restore the colours/strokes
    this.setColor(pColor);
    this.setStroke(pStroke);
    return result;
  }

  /**
   *
   * @param element
   * @return
   */
  protected abstract T render(WedgeLineElement element);

  /**
   *
   * @param element
   * @return
   */
  protected T render(MarkedElement element) {
    return null;
  };

  /**
   *
   * @param element
   * @return
   */
  protected abstract T render(LineElement element);

  /**
   *
   * @param element
   * @return
   */
  protected abstract T render(ElementGroup element);

  /**
   *
   * @param element
   * @return
   */
  protected abstract T render(AtomSymbolElement element);

  /**
   *
   * @param element
   * @return
   */
  protected abstract T render(OvalElement element);

  /**
   *
   * @param element
   */
  protected abstract void setFill(T element);

  /**
   *
   * @param element
   */
  protected abstract void setStroke(T element);

  protected final Double getWidth() {
    return this.boundBox.getWidth() * this.getScale() + 50;
  }

  protected final Double getHeight() {
    return this.boundBox.getHeight() * this.getScale() + 50;
  }

}
