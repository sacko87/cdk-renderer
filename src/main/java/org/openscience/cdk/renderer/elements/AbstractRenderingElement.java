/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates and open the template
 * in the editor.
 */

package org.openscience.cdk.renderer.elements;

import org.openscience.cdk.interfaces.IChemObject;

/**
 *
 * @author sacko
 */
public abstract class AbstractRenderingElement implements IRenderingElement {
  /**
   *
   */
  private IChemObject relatedChemicalObject;

  /**
   *
   * @return
   */
  public IChemObject getRelatedChemicalObject() {
    return this.relatedChemicalObject;
  }

  /**
   *
   * @param chemicalObject
   */
  public void setRelatedChemicalObject(final IChemObject chemicalObject) {
    this.relatedChemicalObject = chemicalObject;
  }
}
