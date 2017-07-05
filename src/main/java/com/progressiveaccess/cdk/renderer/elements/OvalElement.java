package com.progressiveaccess.cdk.renderer.elements;

import java.awt.Color;

import org.openscience.cdk.interfaces.IChemObject;

public class OvalElement extends org.openscience.cdk.renderer.elements.OvalElement implements ILinkedElement {
  private IChemObject chemicalObject;

  public OvalElement(double xCoord, double yCoord, Color color) {
    super(xCoord, yCoord, color);
  }

  public OvalElement(double xCoord, double yCoord, double radius, boolean fill, Color color) {
    super(xCoord, yCoord, radius, fill, color);
  }

  public OvalElement(double xCoord, double yCoord, double radius, Color color) {
    super(xCoord, yCoord, radius, color);
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
