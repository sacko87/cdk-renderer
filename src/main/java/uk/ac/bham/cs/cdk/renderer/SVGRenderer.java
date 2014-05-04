/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.bham.cs.cdk.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import javax.vecmath.Point2d;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.AbstractRenderingElement;
import org.openscience.cdk.renderer.elements.AtomSymbolElement;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author John T. Saxon
 */
public class SVGRenderer extends AbstractRenderer<Node> {
    /**
     * 
     */
    protected Document document = null;
    
    /**
     * 
     */
    public static final String SVG_NS = "http://www.w3.org/2000/svg";
    
    /**
     * 
     * @param model
     */
    public SVGRenderer(RendererModel model) {
       super(model);
       // set defaults
       this.setColor(Color.black);
       this.setStroke(new BasicStroke(1));
    }
    
    /**
     * 
     * @param svgElement
     * @param element 
     */
    private void setId(Node svgElement, IRenderingElement element) {
        try {
            if(svgElement instanceof Element &&
                    element instanceof AbstractRenderingElement) {
                ((Element) svgElement).setAttribute("id", ((AbstractRenderingElement) element).getRelatedChemicalObject().getID());
            }
        } catch(NullPointerException e) { }
    }
    
    @Override
    protected void setFill(Node element) {
        if(element instanceof Element) {
            ((Element) element).setAttribute("fill", "rgb(" + this.getColor().getRed() + ", " + this.getColor().getGreen() + ", " + this.getColor().getBlue() + ")");
        }
    }

    @Override
    protected void setStroke(Node element) {
        if(element instanceof Element) {
            ((Element) element).setAttribute("stroke", "rgb(" + this.getColor().getRed() + ", " + this.getColor().getGreen() + ", " + this.getColor().getBlue() + ")");
            ((Element) element).setAttribute("stroke-width", Double.toString((double) ((BasicStroke) this.getStroke()).getLineWidth()));
        }
    }
    
    @Override
    public Node render(IRenderingElement element, IAtomContainer atomContainer, Double width, Double height) {
        this.document = SVGDOMImplementation.getDOMImplementation().createDocument(SVG_NS, "svg", null);
        this.document.getDocumentElement().setAttribute("width", Double.toString(width));
        this.document.getDocumentElement().setAttribute("height", Double.toString(height));
        this.document.getDocumentElement().appendChild(super.render(element, atomContainer, width, height));
        
        return document;
    }

    @Override
    protected Node render(LineElement element) {
        Point2d point;
        Element line = this.document.createElementNS(SVG_NS, "line");
        
        point = this.XY(element.firstPointX, element.firstPointY);
        line.setAttribute("x1", Double.toString(point.x));
        line.setAttribute("y1", Double.toString(point.y));
        
        point = this.XY(element.secondPointX, element.secondPointY);
        line.setAttribute("x2", Double.toString(point.x));
        line.setAttribute("y2", Double.toString(point.y));
        
        this.setStroke(line);
        
        this.setId(line, element);
        line.setAttribute("class", "bond");
        
        return line;
    }

    @Override
    protected Node render(AtomSymbolElement element) {
        return null;
    }

    @Override
    protected Node render(ElementGroup element) {
        Element group = this.document.createElementNS(SVG_NS, "g");
        
        // create a group element
        // set the ID
        this.setId(group, element);
        // render the bonds
        for(IRenderingElement e: element) {
            Node x = this.render(e);
            if(x != null) {
                group.appendChild(x);
            }
        }
        
        // return the group
        return group.hasChildNodes() ? group : null;
    }
}
