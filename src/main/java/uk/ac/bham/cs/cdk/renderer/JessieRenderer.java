
//
package uk.ac.bham.cs.cdk.renderer;

import java.util.List;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.elements.AtomSymbolElement;
import javax.vecmath.Point2d;
import org.openscience.cdk.renderer.elements.WedgeLineElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;

/**
 * Renders output for Jessie. This is highly experimental and incomplete!
 *
 * Manual at:
 * http://bin.sketchometry.org/ref
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
  public JessieRenderer(RendererModel model,
        List<IGenerator<IAtomContainer>> generators) {
    super(model, generators);
  }


  @Override
  protected Point2d WH(String element) {
    return new Point2d(0, 0);
  }


  @Override
    public String render(IAtomContainer atomContainer) {
    String result = super.render(atomContainer);
    return result;
  }


  @Override
  protected String render(WedgeLineElement element) {
    System.out.println("WedgeLineElement: " + element.getRelatedChemicalObject());
    return "";
  }


  @Override
  protected String render(LineElement element) {
    System.out.println("LineElement: " + element.getRelatedChemicalObject());
    String result = "";
    Point2d start = this.XY(element.firstPointX, element.firstPointY);
    Point2d end = this.XY(element.secondPointX, element.secondPointY);
    String id1 = nodeId();
    String id2 = nodeId();
    result += String.format("\npoint(%f, %f) << id: '%s', name: ''>>;",
                            start.x, start.y, id1);
    result += String.format("\npoint(%f, %f) << id: '%s', name: ''>>;",
                            end.x, end.y, id2);
    result += String.format("\nsegment(%s, %s);", id1, id2);
    return result;
  }


  @Override
  protected String render(ElementGroup element) {
    System.out.println("ElementGroup: " + element.getRelatedChemicalObject());
    String result = "";
    for (IRenderingElement e : element) {
      result += this.render(e);
    }
    return result;
  }


  @Override
  protected String render(AtomSymbolElement element) {
    System.out.println("AtomSymbolElement: " + element.getRelatedChemicalObject());
    Point2d xy = this.XY(element.xCoord, element.yCoord);
    String id = element.getRelatedChemicalObject().getID();
    return String.format("\npoint(%f, %f) << id: '%s', name: '%s'>>;",
                         xy.x, xy.y, id, element.text);
  }
  

  @Override
  protected void setFill(String element) {
  }


  @Override
  protected void setStroke(String element) {
  }

}
