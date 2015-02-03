
//
package uk.ac.bham.cs.cdk.renderer;

import java.util.ArrayList;
import java.util.List;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import uk.ac.bham.cs.cdk.renderer.generators.BasicAtomGenerator;
import uk.ac.bham.cs.cdk.renderer.generators.BasicBondGenerator;

/**
 *
 */

public class App {

    public static void main(String[] args) throws Exception {
	Cli.init(args);
        // create the render model (configuration)
        RendererModel rendererModel = new RendererModel();

        // create the "generators"
        List<IGenerator<IAtomContainer>> generators
            = new ArrayList<>();
        generators.add(new BasicSceneGenerator());
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());
	for (String file : Cli.getFiles()) {
	    FileHandler.translateFile(file, new SVGRenderer(rendererModel, generators));
        }
    }
}


