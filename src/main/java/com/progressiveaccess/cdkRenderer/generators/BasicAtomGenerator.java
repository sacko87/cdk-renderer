
package com.progressiveaccess.cdkRenderer.generators;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.AbstractRenderingElement;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import com.progressiveaccess.cdkRenderer.Cli;

public class BasicAtomGenerator extends
org.openscience.cdk.renderer.generators.BasicAtomGenerator {
  @Override
  public IRenderingElement generate(
      final IAtomContainer atomContainer, final IAtom atom,
      final RendererModel model) {
    IRenderingElement result;
    if (!Cli.hasOption("explicit")
        && !this.canDraw(atom, atomContainer, model)) {
      result = null;
    } else if (Cli.hasOption("exclude_rings") &&
               atom.getSymbol().equals("C") &&
               atom.isAromatic()) {
      result = null;
    } else if (model.get(CompactAtom.class)) {
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

      result = this.generateElement(atom, alignment, model);
    }

    if (result instanceof AbstractRenderingElement) {
      ((AbstractRenderingElement) result).setRelatedChemicalObject(atom);
    }

    return result;
  }
}
