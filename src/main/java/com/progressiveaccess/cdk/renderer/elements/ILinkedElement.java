package com.progressiveaccess.cdk.renderer.elements;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.elements.IRenderingElement;

public interface ILinkedElement extends IRenderingElement{
  /**
   * 
   * @return
   */
  public IChemObject getChemicalObject();
  
  /**
   * 
   * @param chemicalObject
   */
  public void setChemicalObject(IChemObject chemicalObject);
}
