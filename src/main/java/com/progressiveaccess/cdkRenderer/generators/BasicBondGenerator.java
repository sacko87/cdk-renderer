
package com.progressiveaccess.cdkRenderer.generators;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.AbstractRenderingElement;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

public class BasicBondGenerator extends
org.openscience.cdk.renderer.generators.BasicBondGenerator {
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
  public IRenderingElement generate(final IBond currentBond,
      final RendererModel model) {
    final IRing ring = RingSetManipulator.getHeaviestRing(this.ringSet,
        currentBond);
    IRenderingElement result;
    if (ring != null) {
      result = this.generateRingElements(currentBond, ring, model);
    } else {
      result = this.generateBond(currentBond, model);
    }

    if (result instanceof AbstractRenderingElement) {
      ((AbstractRenderingElement) result).setRelatedChemicalObject(currentBond);
    }

    return result;
  }
}
