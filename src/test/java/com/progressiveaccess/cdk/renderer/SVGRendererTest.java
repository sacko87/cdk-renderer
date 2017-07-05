
package com.progressiveaccess.cdk.renderer;


import com.progressiveaccess.cdk.renderer.Cli;
import com.progressiveaccess.cdk.renderer.FileHandler;
import com.progressiveaccess.cdk.renderer.SVGRenderer;
import com.progressiveaccess.cdk.renderer.generators.LinkedAtomGenerator;
import com.progressiveaccess.cdk.renderer.generators.LinkedBondGenerator;
import com.progressiveaccess.cdk.renderer.generators.LinkedSceneGenerator;

import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author John T. Saxon
 */
public class SVGRendererTest {
  /**
   *
   */
  protected SVGRenderer renderer;
  protected String outputDir = "target/test-output";

  /**
   *
   */
  public SVGRendererTest() {
    // create the render model (configuration)
    final RendererModel rendererModel = new RendererModel();

    // create the "generators"
    final List<IGenerator<IAtomContainer>> generators = new ArrayList<>();
    generators.add(new LinkedSceneGenerator());
    generators.add(new LinkedBondGenerator());
    generators.add(new LinkedAtomGenerator());

    // setup the renderer
    this.renderer = new SVGRenderer(rendererModel, generators);
    // add my options
    this.renderer.getModel().set(
        LinkedAtomGenerator.ShowExplicitHydrogens.class, true);
    this.renderer.getModel().set(LinkedAtomGenerator.ShowEndCarbons.class, true);
  }


  private void makeDirectory(final String prefix, final String dir) {
    final File outputDir = new File(prefix, dir);
    outputDir.mkdirs();
  }


  private void makeDirectory(final String dir) {
    this.makeDirectory(this.outputDir, dir);
  }


  private final String[] factoryMolecules = new String[] {
      "123Triazole",
      "124Triazole",
      "4x3CondensedRings",
      "Adenine",
      // "Alkane",
      "AlphaPinene",
      "Azulene",
      "Benzene",
      "BicycloRings",
      "Biphenyl",
      "BranchedAliphatic",
      "Cyclobutadiene",
      "Cyclobutane",
      "Cyclohexane",
      "Cyclohexene",
      "Cyclopentane",
      "Diamantane",
      "EthylCyclohexane",
      "EthylPropylPhenantren",
      "FusedRings",
      "Imidazole",
      "Indole",
      "Isothiazole",
      "Isoxazole",
      "MethylDecaline",
      "Oxadiazole",
      "Oxazole",
      "PhenylAmine",
      "PhenylEthylBenzene",
      "Piperidine",
      "PropylCycloPropane",
      "Pyrazole",
      "Pyridazine",
      "Pyridine",
      "PyridineOxide",
      "Pyrimidine",
      "Pyrrole",
      "PyrroleAnion",
      "Quinone",
      "SingleRing",
      "SpiroRings",
      "Steran",
      "Tetrahydropyran",
      "Tetrazole",
      "Thiadiazole",
      "Thiazole",
      "Triazine"
  };


  private Method createFactoryMethod(final String name) {
    Method method = null;
    try {
      method = Class.forName("org.openscience.cdk.templates.MoleculeFactory")
          .getMethod("make" + name);
    } catch (final ClassNotFoundException e) {
      System.out.println("Class Error " + e.getMessage());
      e.printStackTrace();
    } catch (final SecurityException e) {
      System.out.println("Security Error " + e.getMessage());
      e.printStackTrace();
    } catch (final NoSuchMethodException e) {
      System.out.println("Method Error " + e.getMessage());
      e.printStackTrace();
    }
    return method;
  }


  /**
   *
   * @throws IOException
   */
  @Test
  public void testDirectory() throws IOException {
    final String[] dummy = { "-d", "cml", "-o", this.outputDir + "/cml" };
    Cli.init(dummy);
    this.makeDirectory(this.outputDir, "cml");
    FileHandler.translateDirectory(Cli.getOptionValue("dir"), this.renderer);
  }


  /**
   *
   */
  @Test
  public void testFactory() {
    final String[] dummy = {};
    Cli.init(dummy);
    this.makeDirectory("all");
    for (final String name : this.factoryMolecules) {
      System.out.println("Generating: " + name);
      // create a factory molecule
      final Method method = this.createFactoryMethod(name);
      IAtomContainer mole = null;
      try {
        mole = (IAtomContainer) method.invoke(null);
      } catch (final IllegalAccessException e) {
        System.out.println("Access Error " + e.getMessage());
        e.printStackTrace();
      } catch (final IllegalArgumentException e) {
        System.out.println("Argument Error " + e.getMessage());
        e.printStackTrace();
      } catch (final InvocationTargetException e) {
        System.out.println("Invocation Error " + e.getMessage());
        e.printStackTrace();
      }
      // the original structure has no x,y coordinates (or so it seems).
      final StructureDiagramGenerator sdg = new StructureDiagramGenerator(mole);
      try {
        // calculate the coordinates
        sdg.generateCoordinates();
      } catch (final CDKException ex) {
        Logger.getLogger(SVGRendererTest.class.getName()).log(Level.SEVERE,
            null, ex);
        Assert.fail();
      }

      final Path output = Paths.get(this.outputDir + "/all/" + name + ".svg");
      // Translate to CML.
      final Class<?> params[] = new Class<?>[] { Path.class,
          IAtomContainer.class };
      Method buildCML = null;
      try {
        buildCML = Class.forName(
            "com.progressiveaccess.cdk.renderer.FileHandler").
            getDeclaredMethod("buildCML", params);
      } catch (final ClassNotFoundException e) {
        System.out.println("Class Error " + e.getMessage());
        e.printStackTrace();
      } catch (final SecurityException e) {
        System.out.println("Security Error " + e.getMessage());
        e.printStackTrace();
      } catch (final NoSuchMethodException e) {
        System.out.println("Method Error " + e.getMessage());
        e.printStackTrace();
      }
      buildCML.setAccessible(true);
      try {
        buildCML.invoke(null, output, sdg.getMolecule());
      } catch (final IllegalAccessException e) {
        System.out.println("Access Error " + e.getMessage());
        e.printStackTrace();
      } catch (final IllegalArgumentException e) {
        System.out.println("Argument Error " + e.getMessage());
        e.printStackTrace();
      } catch (final InvocationTargetException e) {
        System.out.println("Invocation Error " + e.getMessage());
        e.printStackTrace();
      }
      // the original structure has no x,y coordinates (or so it seems).
      // StructureDiagramGenerator sdg = new StructureDiagramGenerator(mole);

      // refresh the molecule
      mole = sdg.getMolecule();
      if (mole != null) {
        // render the molecule
        final Document doc = (Document) this.renderer.render(mole);
        Assert.assertNotEquals("Unable to render the IAtomContainer.", null,
            doc);
        // write it to the file
        final Boolean b = FileHandler.toFile(doc, output);
        Assert.assertEquals(Boolean.TRUE, b);
      }
    }
  }

  /**
   *
   * @throws IOException
   */
  @Test
  public void testAll() throws IOException {
    final EnumSet<FileVisitOption> opts = EnumSet
        .of(FileVisitOption.FOLLOW_LINKS);

    this.makeDirectory("/cml");
    // walk through the /cml (with respect to the root of the project)
    Files.walkFileTree(Paths.get("cml"), opts, 2,
        new SimpleFileVisitor<Path>() {
          @Override
          public FileVisitResult visitFile(final Path file,
              final BasicFileAttributes attrs) {
            // are we looking at a CML file?
            if (attrs.isRegularFile()
                && file.toString().toLowerCase().endsWith(".cml")) {
              // read in the molecule from the CML
              final IAtomContainer mole = FileHandler.fromFile(file);
              Assert.assertNotEquals(
                  "Failed to get an IAtomContainer instance.", null, mole);
              if (mole != null) {
                Document doc = null;
                try {
                  // render the molecule
                  doc = (Document) SVGRendererTest.this.renderer.render(mole);
                } catch (final IllegalArgumentException e) {
                  // to show which file threw the exception
                  throw new IllegalArgumentException(file.toString(), e);
                }
                Assert.assertNotEquals("Unable to render the IAtomContainer.",
                    null, doc);

                // write it to the file
                final String svgFile = FilenameUtils.removeExtension(file
                    .getFileName().toString()) + ".svg";
                final Boolean b = FileHandler.toFile(doc,
                    Paths.get(SVGRendererTest.this.outputDir +
                        "/cml/", svgFile));
                Assert.assertEquals(Boolean.TRUE, b);
              }
            }

            // move on
            return FileVisitResult.CONTINUE;
          }
    });
  }

}
