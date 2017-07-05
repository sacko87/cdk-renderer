package com.progressiveaccess.cdk.renderer.elements;

import java.awt.Color;

import org.openscience.cdk.interfaces.IChemObject;

public class TextElement extends org.openscience.cdk.renderer.elements.TextElement implements ILinkedElement {
  private IChemObject chemicalObject;

  public TextElement(double xCoord, double yCoord, String text, Color color) {
    super(xCoord, yCoord, text, color);
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
