
package com.progressiveaccess.cdkRenderer.generators;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.AbstractRenderingElement;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.OvalElement;

public class RingGenerator extends
org.openscience.cdk.renderer.generators.RingGenerator {
  /**
   * Generate rendering element(s) for the current bond, including ring elements
   * if this bond is part of a ring.
   *
   * @param currentBond
   *          the bond to use when generating elements
   * @param model
   *          the renderer model
   * @return one or more rendering elements
   */
  @Override
  public IRenderingElement generateRingElements(final IBond bond,
                                                final IRing ring,
                                                final RendererModel model) {
    IRenderingElement result = super.generateRingElements(bond, ring, model);
    for (IRenderingElement element: (ElementGroup)result) {
      if (element instanceof OvalElement) {
        ((AbstractRenderingElement) element).setRelatedChemicalObject(ring);
      }
    }
    return result;
  }

}
