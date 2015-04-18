//

package com.progressiveaccess.cdkRenderer;

import com.progressiveaccess.cdkRenderer.generators.BasicAtomGenerator;
import com.progressiveaccess.cdkRenderer.generators.BasicBondGenerator;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class App {

  public static void main(final String[] args) throws Exception {
    Cli.init(args);
    // create the render model (configuration)
    final RendererModel rendererModel = new RendererModel();

    // create the "generators"
    final List<IGenerator<IAtomContainer>> generators = new ArrayList<>();
    generators.add(new BasicSceneGenerator());
    generators.add(new BasicBondGenerator());
    generators.add(new BasicAtomGenerator());
    // add my options
    if (Cli.hasOption("j")) {
      final JessieRenderer renderer = new JessieRenderer(rendererModel,
          generators);
      for (final String file : Cli.getFiles()) {
        FileHandler.translateFile(file, renderer);
      }
      System.exit(0);
    }
    final SVGRenderer renderer = new SVGRenderer(rendererModel, generators);
    renderer.getModel().set(BasicAtomGenerator.ShowExplicitHydrogens.class,
        true);
    renderer.getModel().set(BasicAtomGenerator.ShowEndCarbons.class, true);
    if (Cli.hasOption("dir")) {
      FileHandler.translateDirectory(Cli.getOptionValue("dir"), renderer);
    }
    for (final String file : Cli.getFiles()) {
      FileHandler.translateFile(file, renderer);
    }
  }
}
