//

package com.progressiveaccess.cdk.renderer;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.generators.IGenerator;

import com.progressiveaccess.cdk.renderer.generators.LinkedAtomGenerator;
import com.progressiveaccess.cdk.renderer.generators.LinkedSceneGenerator;

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
    generators.add(new LinkedSceneGenerator());
    //generators.add(new LinkedBondGenerator());
    generators.add(new LinkedAtomGenerator());
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
    renderer.getModel().set(LinkedAtomGenerator.ShowExplicitHydrogens.class,
        true);
    renderer.getModel().set(LinkedAtomGenerator.ShowEndCarbons.class, true);
    if (Cli.hasOption("dir")) {
      FileHandler.translateDirectory(Cli.getOptionValue("dir"), renderer);
    }
    for (final String file : Cli.getFiles()) {
      FileHandler.translateFile(file, renderer);
    }
  }
}
