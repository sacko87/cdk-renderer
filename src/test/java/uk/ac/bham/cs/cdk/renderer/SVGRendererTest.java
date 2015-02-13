package uk.ac.bham.cs.cdk.renderer;


import java.io.IOException;
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
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.w3c.dom.Document;
import uk.ac.bham.cs.cdk.renderer.generators.BasicAtomGenerator;
import uk.ac.bham.cs.cdk.renderer.generators.BasicBondGenerator;
import org.apache.commons.io.FilenameUtils;


/**
 *
 * @author John T. Saxon
 */
public class SVGRendererTest {
    /**
     * 
     */
    protected SVGRenderer renderer;
    
    /**
     * 
     */
    protected static final Double DEFAULT_WIDTH = 200.0;
    
    /**
     * 
     */
    protected static final Double DEFAULT_HEIGHT = 200.0;
    
    /**
     * 
     */
    public SVGRendererTest() {
        // create the render model (configuration)
        RendererModel rendererModel = new RendererModel();

        // create the "generators"
        List<IGenerator<IAtomContainer>> generators
            = new ArrayList<>();
        generators.add(new BasicSceneGenerator());
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());
        
        // setup the renderer
        this.renderer = new SVGRenderer(rendererModel, generators);
        // add my options
        this.renderer.getModel().set(BasicAtomGenerator.ShowExplicitHydrogens.class, true);
        this.renderer.getModel().set(BasicAtomGenerator.ShowEndCarbons.class, true);
    }
    
    /**
     * 
     */
    @Test
    public void testFactory() {
        // create a factory molecule
        IAtomContainer mole = MoleculeFactory.make123Triazole();
        
        // the original structure has no x,y coordinates (or so it seems).
        StructureDiagramGenerator sdg = new StructureDiagramGenerator(mole);

        try {
            // calculate the coordinates
            sdg.generateCoordinates();
        } catch (CDKException ex) {
            Logger.getLogger(SVGRendererTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }

        // refresh the molecule
        mole = sdg.getMolecule();
        if(mole != null) {
            // render the molecule
            Document doc = (Document) this.renderer.render(mole, SVGRendererTest.DEFAULT_WIDTH, SVGRendererTest.DEFAULT_HEIGHT);
            Assert.assertNotEquals("Unable to render the IAtomContainer.", null, doc);

            // write it to the file
            Boolean b = FileHandler.toFile(doc, /* fake path */ Paths.get("cml/triazole.svg"));
            Assert.assertEquals(Boolean.TRUE, b);
        }
    }
    
    /**
     * 
     * @throws IOException 
     */
    @Test
    public void testAll() throws IOException {
        EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        
        // walk through the /cml (with respect to the root of the project)
        Files.walkFileTree(Paths.get("cml"), opts, 2, new SimpleFileVisitor<Path>() {
            @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                // are we looking at a CML file?
                if(attrs.isRegularFile() && file.toString().toLowerCase().endsWith(".cml")) {
                    // read in the molecule from the CML
                    IAtomContainer mole = FileHandler.fromFile(file);
                    Assert.assertNotEquals("Failed to get an IAtomContainer instance.", null, mole);
                    if(mole != null) {
                        Document doc = null;
                        try {
                        // render the molecule
                            doc = (Document) SVGRendererTest.this.renderer.render(mole, SVGRendererTest.DEFAULT_WIDTH, SVGRendererTest.DEFAULT_HEIGHT);
                        } catch(IllegalArgumentException e) {
                            // to show which file threw the exception
                            throw new IllegalArgumentException(file.toString(), e);
                        }
                        Assert.assertNotEquals("Unable to render the IAtomContainer.", null, doc);
                        
                        // write it to the file
                        String svgFile = FilenameUtils.removeExtension(file.getFileName().toString()) + ".svg";
                        Boolean b = FileHandler.toFile(doc, file.resolveSibling(svgFile));
                        Assert.assertEquals(Boolean.TRUE, b);
                    }
                }
                
                // move on
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
}
