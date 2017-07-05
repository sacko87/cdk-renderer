package com.progressiveaccess.cdk.renderer.elements;

import java.awt.Color;

import org.openscience.cdk.interfaces.IChemObject;

public class LineElement extends org.openscience.cdk.renderer.elements.LineElement implements ILinkedElement {
  private IChemObject chemicalObject;

  public LineElement(double firstPointX, double firstPointY, double secondPointX, double secondPointY, double width,
      Color color) {
    super(firstPointX, firstPointY, secondPointX, secondPointY, width, color);
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
