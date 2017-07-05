package com.progressiveaccess.cdk.renderer.elements;

import java.awt.Color;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.elements.LineElement;


public class WedgeLineElement extends org.openscience.cdk.renderer.elements.WedgeLineElement implements ILinkedElement {
  private IChemObject chemicalObject;

  public WedgeLineElement(double x1, double y1, double x2, double y2, double width, TYPE type, Direction direction,
      Color color) {
    super(x1, y1, x2, y2, width, type, direction, color);
  }

  public WedgeLineElement(LineElement element, TYPE type, Direction direction, Color color) {
    super(element, type, direction, color);
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
