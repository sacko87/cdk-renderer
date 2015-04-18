//

package com.progressiveaccess.cdkRenderer;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.AtomSymbolElement;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.WedgeLineElement;
import org.openscience.cdk.renderer.generators.IGenerator;

import java.util.List;

import javax.vecmath.Point2d;

/**
 * Renders output for Jessie. This is highly experimental and incomplete!
 *
 * Manual at: http://bin.sketchometry.org/ref
 */
public class JessieRenderer extends AbstractRenderer<String> {

  private Integer counter = 0;

  private String nodeId() {
    this.counter++;
    return 'p' + this.counter.toString();
  }

  /**
   *
   * @param model
   * @param generators
   */
  public JessieRenderer(final RendererModel model,
      final List<IGenerator<IAtomContainer>> generators) {
    super(model, generators);
  }

  @Override
  protected Point2d WH(final String element) {
    return new Point2d(0, 0);
  }

  @Override
  public String render(final IAtomContainer atomContainer) {
    final String result = super.render(atomContainer);
    return result;
  }

  @Override
  protected String render(final WedgeLineElement element) {
    System.out.println("WedgeLineElement: "
        + element.getRelatedChemicalObject());
    return "";
  }

  @Override
  protected String render(final LineElement element) {
    System.out.println("LineElement: " + element.getRelatedChemicalObject());
    String result = "";
    final Point2d start = this.XY(element.firstPointX, element.firstPointY);
    final Point2d end = this.XY(element.secondPointX, element.secondPointY);
    final String id1 = this.nodeId();
    final String id2 = this.nodeId();
    result += String.format("\npoint(%f, %f) << id: '%s', name: ''>>;",
        start.x, start.y, id1);
    result += String.format("\npoint(%f, %f) << id: '%s', name: ''>>;",
        end.x, end.y, id2);
    result += String.format("\nsegment(%s, %s);", id1, id2);
    return result;
  }

  @Override
  protected String render(final ElementGroup element) {
    System.out.println("ElementGroup: " + element.getRelatedChemicalObject());
    String result = "";
    for (final IRenderingElement e : element) {
      result += this.render(e);
    }
    return result;
  }

  @Override
  protected String render(final AtomSymbolElement element) {
    System.out.println("AtomSymbolElement: "
        + element.getRelatedChemicalObject());
    final Point2d xy = this.XY(element.xCoord, element.yCoord);
    final String id = element.getRelatedChemicalObject().getID();
    return String.format("\npoint(%f, %f) << id: '%s', name: '%s'>>;",
        xy.x, xy.y, id, element.text);
  }

  @Override
  protected void setFill(final String element) {
  }

  @Override
  protected void setStroke(final String element) {
  }

}
