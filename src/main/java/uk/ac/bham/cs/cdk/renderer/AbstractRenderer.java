/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.bham.cs.cdk.renderer;

// i'm being nice
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.vecmath.Point2d;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.BoundsCalculator;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.AtomSymbolElement;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.ZoomFactor;

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

    /**
     * 
     * @param model 
     */
    protected AbstractRenderer(RendererModel model) {
        this.model = model;
        this.updateTransformer();
    }

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
     * 
     * @return 
     */
    public Color getColor() {
        return color;
    }

    /**
     * 
     * @param color 
     */
    public void setColor(Color color) {
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
    public void setModelCentre(Point2d modelCentre) {
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
    public void setDrawingCentre(Point2d drawingCentre) {
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
    public final void setStroke(Stroke stroke) {
        if(stroke instanceof BasicStroke) {
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
    public final void setScale(IAtomContainer atomContainer) {
        this.getModel().getParameter(Scale.class).setValue(this.calculateScaleForBondLength(GeometryTools.getBondLengthAverage(atomContainer)));
        this.updateTransformer();
    }
    
    /**
     * Given a bond length for a model, calculate the scale that will transform
     * this length to the on screen bond length in RendererModel.
     *
     * @param bondLenght the average bond length of the model
     * @return the scale necessary to transform this to a screen bond
     */
    public double calculateScaleForBondLength(Double bondLenght) {
        if (Double.isNaN(bondLenght) || bondLenght == 0) {
            return DEFAULT_SCALE;
        } else {
            return getModel().getParameter(
                    BasicSceneGenerator.BondLength.class).getValue() / bondLenght;
        }
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
    public final void setZoom(Double value) {
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
     * @param x
     * @param y
     * @return 
     */
    protected Point2d XY(Double x, Double y) {
        double[] i = new double[] {
            x, y
        };
        
        this.transform.transform(i, 0, i, 0, 1);
        
        return new Point2d(i);
    }
    
    /**
     * 
     * @param element
     * @param atomContainer
     * @param width
     * @param height
     * @return 
     */
    public T render(IRenderingElement element, IAtomContainer atomContainer, Double width, Double height) {
        this.setDrawingCentre(new Point2d(width / 2, height / 2));
        
        return this.render(element, atomContainer);
    }
    
    /**
     * 
     * @param element
     * @param atomContainer
     * @return 
     */
    protected T render(IRenderingElement element, IAtomContainer atomContainer) {
        this.setScale(atomContainer);
        
        Rectangle2D boundBox = BoundsCalculator.calculateBounds(atomContainer);
        this.setModelCentre(new Point2d(boundBox.getCenterX(), boundBox.getCenterY()));
        
        return this.render(element);
    }

    /**
     * 
     * @param element 
     * @return  
     */
    protected T render(IRenderingElement element) {
        if(element instanceof LineElement) {
            return this.render((LineElement) element);
        } else if(element instanceof ElementGroup) {
            return this.render((ElementGroup) element);
        } else if(element instanceof AtomSymbolElement) {
            return this.render((AtomSymbolElement) element);
        } else {
            throw new UnsupportedOperationException(
                    "The rendering of " + element.getClass().getCanonicalName()
                            + " is not supported.");
        }
    }
    
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
     */
    protected abstract void setFill(T element);

    /**
     * 
     * @param element 
     */
    protected abstract void setStroke(T element);
}
