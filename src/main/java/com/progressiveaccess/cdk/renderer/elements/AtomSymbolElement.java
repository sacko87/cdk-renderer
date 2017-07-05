package com.progressiveaccess.cdk.renderer.elements;

import java.awt.Color;

import org.openscience.cdk.interfaces.IChemObject;

public class AtomSymbolElement extends org.openscience.cdk.renderer.elements.AtomSymbolElement
    implements ILinkedElement {
  private IChemObject chemicalObject;

  public AtomSymbolElement(double x, double y, String symbol, Integer formalCharge, Integer hydrogenCount,
      int alignment, Color color) {
    super(x, y, symbol, formalCharge, hydrogenCount, alignment, color);
  }

  @Override
  public IChemObject getChemicalObject() {
    return this.chemicalObject;
  }

  @Override
  public void setChemicalObject(IChemObject chemicalObject) {
    this.chemicalObject = chemicalObject;
  }
}
