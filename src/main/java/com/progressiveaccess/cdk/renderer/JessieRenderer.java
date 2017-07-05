//

package com.progressiveaccess.cdk.renderer;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.MarkedElement;
import org.openscience.cdk.renderer.generators.IGenerator;

import com.progressiveaccess.cdk.renderer.elements.AtomSymbolElement;
import com.progressiveaccess.cdk.renderer.elements.ElementGroup;
import com.progressiveaccess.cdk.renderer.elements.LineElement;
import com.progressiveaccess.cdk.renderer.elements.OvalElement;
import com.progressiveaccess.cdk.renderer.elements.WedgeLineElement;

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
        + element.getChemicalObject());
    return "";
  }

  @Override
  protected String render(final LineElement element) {
    System.out.println("LineElement: " + element.getChemicalObject());
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
    System.out.println("ElementGroup: " + element.getChemicalObject());
    String result = "";
    for (final IRenderingElement e : element) {
      result += this.render(e);
    }
    return result;
  }

  @Override
  protected String render(final AtomSymbolElement element) {
    System.out.println("AtomSymbolElement: "
        + element.getChemicalObject());
    final Point2d xy = this.XY(element.xCoord, element.yCoord);
    final String id = element.getChemicalObject().getID();
    return String.format("\npoint(%f, %f) << id: '%s', name: '%s'>>;",
        xy.x, xy.y, id, element.text);
  }

  @Override
  protected String render(final OvalElement element) {
    // TODO
    return null;
  }
  
  @Override
  protected void setFill(final String element) {
  }

  @Override
  protected void setStroke(final String element) {
  }

  @Override
  protected String render(MarkedElement element) {
    // TODO Auto-generated method stub
    return null;
  }

}
