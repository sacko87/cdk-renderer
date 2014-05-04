package uk.ac.bham.cs.cdk.renderer.generators;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.AbstractRenderingElement;
import org.openscience.cdk.renderer.elements.IRenderingElement;

public class BasicAtomGenerator extends org.openscience.cdk.renderer.generators.BasicAtomGenerator {
    @Override
    public IRenderingElement generate(
            IAtomContainer atomContainer, IAtom atom, RendererModel model) {
        IRenderingElement result;
        
        if (!canDraw(atom, atomContainer, model)) {
            result = null;
	} else if ((boolean) model.get(CompactAtom.class)) {
            result = this.generateCompactElement(atom, model);
        } else {
            int alignment;
            if (atom.getSymbol().equals("C")) {
                alignment = 
                  GeometryTools.getBestAlignmentForLabel(atomContainer, atom);
            } else {
                alignment = 
                  GeometryTools.getBestAlignmentForLabelXY(atomContainer, atom);
            }

            result = generateElement(atom, alignment, model);
        }
        
        if(result instanceof AbstractRenderingElement) {
            ((AbstractRenderingElement) result).setRelatedChemicalObject(atom);
        }
        
        return result;
    }
}
