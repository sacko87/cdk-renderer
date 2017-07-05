package com.progressiveaccess.cdk.renderer.elements;

import org.openscience.cdk.interfaces.IChemObject;

public class ElementGroup extends org.openscience.cdk.renderer.elements.ElementGroup implements ILinkedElement {
  private IChemObject chemicalObject;

  @Override
  public IChemObject getChemicalObject() {
    return this.chemicalObject;
  }

  @Override
  public void setChemicalObject(IChemObject chemicalObject) {
    this.chemicalObject = chemicalObject;
  }
}
