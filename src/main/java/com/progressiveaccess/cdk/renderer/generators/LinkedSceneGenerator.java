package com.progressiveaccess.cdk.renderer.generators;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;

import com.progressiveaccess.cdk.renderer.elements.ElementGroup;

public class LinkedSceneGenerator extends BasicSceneGenerator {
  @Override
  public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
    ElementGroup g = new ElementGroup();
    g.setChemicalObject(ac);
    return g;
  }
}
