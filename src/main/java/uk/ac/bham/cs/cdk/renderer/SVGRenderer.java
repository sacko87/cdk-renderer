/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.bham.cs.cdk.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.List;
import javax.vecmath.Point2d;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.AbstractRenderingElement;
import org.openscience.cdk.renderer.elements.AtomSymbolElement;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGLocatable;
import org.w3c.dom.svg.SVGRect;

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
     * @param generators
     */
    public SVGRenderer(RendererModel model, List<IGenerator> generators) {
       super(model, generators);
       // set defaults
       this.setColor(Color.black);
       this.setStroke(new BasicStroke(1));
    }
    
    @Override
    protected Point2d WH(Node element) {
        // create a temporary document and add the element
        Document d = SVGDOMImplementation.getDOMImplementation().createDocument(SVG_NS, "svg", null);
        Node n = d.importNode(element, true);
        d.getDocumentElement().appendChild(n);
        
        // setup a virtual browser if you will
        UserAgent ua = new UserAgentAdapter();
        DocumentLoader l = new DocumentLoader(ua);
        BridgeContext bc = new BridgeContext(ua, l);
        bc.setDynamicState(BridgeContext.DYNAMIC);
        
        // "render it"
        GVTBuilder b = new GVTBuilder();
        b.build(bc, d);
        
        // get the boundary of the result
        SVGRect bbox = ((SVGLocatable) n).getBBox();

        // return the points (x:width, y:height)
        return new Point2d(bbox.getWidth(), bbox.getHeight());
    }
    
    /**
     * 
     * @param svgElement
     * @param element 
     */
    private void setId(Node node, IRenderingElement element) {
        try {
            if(node instanceof Element &&
                    element instanceof AbstractRenderingElement) {
                // do some casting
                Element _node = (Element) node;
                AbstractRenderingElement renderingElement = (AbstractRenderingElement) element;
                
                // add some attributes
                _node.setAttribute("id", renderingElement.getRelatedChemicalObject().getID());
            }
        } catch(NullPointerException e) { }
    }
    
    @Override
    protected void setFill(Node element) {
        // i only play with elements
        if(element instanceof Element) {
            ((Element) element).setAttribute("fill", "rgb(" + this.getColor().getRed() + ", " + this.getColor().getGreen() + ", " + this.getColor().getBlue() + ")");
        }
    }

    @Override
    protected void setStroke(Node element) {
        // i only play with elements
        if(element instanceof Element) {
            ((Element) element).setAttribute("stroke", "rgb(" + this.getColor().getRed() + ", " + this.getColor().getGreen() + ", " + this.getColor().getBlue() + ")");
            ((Element) element).setAttribute("stroke-width", Double.toString((double) ((BasicStroke) this.getStroke()).getLineWidth()));
        }
    }
    
    @Override
    public Node render(IAtomContainer atomContainer, Double width, Double height) {
        // create an SVG DOM document
        Document doc = SVGDOMImplementation.getDOMImplementation().createDocument(SVG_NS, "svg", null);
        // set the height and width attributes
        doc.getDocumentElement().setAttribute("width", Double.toString(width));
        doc.getDocumentElement().setAttribute("height", Double.toString(height));
        
        // store this document for 
        // use within this render
        this.document = doc;
        
        Node result = super.render(atomContainer, width, height);
        if(result != null) {
            // add the resultant model to the document
            doc.getDocumentElement().appendChild(result);
        }

        return doc;
    }

    @Override
    protected Node render(LineElement element) {
        Point2d point;
        // create the line needed
        Element line = this.document.createElementNS(SVG_NS, "line");
        
        // transform the points and set the attributes
        point = this.XY(element.firstPointX, element.firstPointY);
        line.setAttribute("x1", Double.toString(point.x));
        line.setAttribute("y1", Double.toString(point.y));
        
        // transform the points and set the attributes
        point = this.XY(element.secondPointX, element.secondPointY);
        line.setAttribute("x2", Double.toString(point.x));
        line.setAttribute("y2", Double.toString(point.y));
        
        // styling
        this.setStroke(line);
        line.setAttribute("class", "bond");
        
        // if attached to IChemObject
        this.setId(line, element);
        
        return line;
    }

    @Override
    protected Node render(AtomSymbolElement element) {
        // create the required elements
        Element group = this.document.createElementNS(SVG_NS, "g");
        Element rect = this.document.createElementNS(SVG_NS, "rect");
        Element text = this.document.createElementNS(SVG_NS, "text");
        group.setAttribute("class", "atom");
        // if attached to IChemObject
        this.setId(group, element);
        
        // arrange them accordingly
        group.appendChild(rect);
        group.appendChild(text);
        
        // fill the background white
        this.setColor(Color.WHITE);
        this.setFill(rect);
        this.setStroke(rect);
        
        // set the text colour
        this.setColor(element.color);
        this.setFill(text);
        // set the text attributes
        text.setAttribute("font-family", this.DEFAULT_FONT.getFamily());
        text.setAttribute("font-size", Double.toString(this.DEFAULT_FONT.getSize2D() * this.getZoom()) + "px");
        
        Boolean showExplicitHydrogens =
                this.getModel().hasParameter(BasicAtomGenerator.ShowExplicitHydrogens.class)
                    ? this.getModel().get(BasicAtomGenerator.ShowExplicitHydrogens.class)
                    : this.getModel().getDefault(BasicAtomGenerator.ShowExplicitHydrogens.class);
        
        // create a fragement
        DocumentFragment df = this.document.createDocumentFragment();
        // create the core text for the atom
        Node elem = text.appendChild(this.document.createTextNode(element.text));
        // do we want to show explicitly the hydogen counts?
        // do we have any?
        if(showExplicitHydrogens && element.hydrogenCount > 0) {
            // room for the hydrogen
            DocumentFragment Hx = this.document.createDocumentFragment();
            // create the hydrogen atom
            Hx.appendChild(this.document.createTextNode("H"));
            // is there more than one?
            if(element.hydrogenCount > 1) {
                // create some space
                Element n = this.document.createElementNS(SVG_NS, "tspan");
                n.setAttribute("baseline-shift", "sub");
                // add the count to it
                n.appendChild(this.document.createTextNode(Integer.toString(element.hydrogenCount)));
                Hx.appendChild(n);
            }
            
            // before or after
            // add elements in order
            if(element.alignment == 2) {
                df.appendChild(elem);
                df.appendChild(Hx);
            } else {
                df.appendChild(Hx);
                df.appendChild(elem);
            }
        } else {
            // just add atom
            df.appendChild(elem);
        }
        
        text.appendChild(df);

        // transform the given (x,y) coordinates
        Point2d xy = this.XY(element.xCoord, element.yCoord);
        
        // get the width and height of the element
        Point2d b = this.WH(text);
        Double  w = b.x; // width
        Double  h = b.y; // hight
        
        // setup the background
        rect.setAttribute("x", Double.toString(xy.x - (w / 2) - ((this.DEFAULT_XPAD * this.getZoom()) / 2)));
        rect.setAttribute("y", Double.toString(xy.y - (-h / 2) - h - ((this.DEFAULT_YPAD * this.getZoom()) / 2)));
        rect.setAttribute("width", Double.toString(w + (this.DEFAULT_XPAD * this.getZoom())));
        rect.setAttribute("height", Double.toString(h + (this.DEFAULT_YPAD * this.getZoom())));
        
        // setup the text to be on top
        text.setAttribute("x", Double.toString(xy.x - (w / 2)));
        text.setAttribute("y", Double.toString(xy.y - (-h / 2)));
        
        return group;
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
