package uk.ac.bham.cs.cdk.renderer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.w3c.dom.Document;
import uk.ac.bham.cs.cdk.renderer.generators.BasicAtomGenerator;
import uk.ac.bham.cs.cdk.renderer.generators.BasicBondGenerator;

/**
 *
 * @author John T. Saxon
 */
public class BulkSVGRendererTest {
    /**
     * 
     */
    protected SVGRenderer renderer;
    
    /**
     * 
     */
    protected static final Double DEFAULT_WIDTH = 400.0;
    
    /**
     * 
     */
    protected static final Double DEFAULT_HEIGHT = 400.0;
    
    /**
     * 
     */
    public BulkSVGRendererTest() {
        // create the render model (configuration)
        RendererModel rendererModel = new RendererModel();

        // create the "generators"
        List<IGenerator> generators
            = new ArrayList<>();
        generators.add(new BasicSceneGenerator());
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());

        // setup the renderer
        this.renderer = new SVGRenderer(rendererModel, generators);
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
            Logger.getLogger(BulkSVGRendererTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }

        // refresh the molecule
        mole = sdg.getMolecule();
        if(mole != null) {
            // render the molecule
            Document doc = (Document) this.renderer.render(mole, BulkSVGRendererTest.DEFAULT_WIDTH, BulkSVGRendererTest.DEFAULT_HEIGHT);
            Assert.assertNotEquals("Unable to render the IAtomContainer.", null, doc);

            // write it to the file
            Boolean b = BulkSVGRendererTest.this.toFile(doc, /* fake path */ Paths.get("cml/triazole.cml"));
            Assert.assertEquals(Boolean.TRUE, b);
        }
    }
    
    /**
     * 
     * @throws IOException 
     */
    @Test
    public void testAll() throws IOException {
        EnumSet<FileVisitOption> opts = EnumSet.of(FOLLOW_LINKS);
        
        // walk through the /cml (with respect to the root of the project)
        Files.walkFileTree(Paths.get("cml"), opts, 2, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                // are we looking at a CML file?
                if(attrs.isRegularFile() && file.toString().toLowerCase().endsWith(".cml")) {
                    // read in the molecule from the CML
                    IAtomContainer mole = BulkSVGRendererTest.this.fromFile(file);
                    Assert.assertNotEquals("Failed to get an IAtomContainer instance.", null, mole);
                    if(mole != null) {
                        // render the molecule
                        Document doc = (Document) BulkSVGRendererTest.this.renderer.render(mole, BulkSVGRendererTest.DEFAULT_WIDTH, BulkSVGRendererTest.DEFAULT_HEIGHT);
                        Assert.assertNotEquals("Unable to render the IAtomContainer.", null, doc);
                        
                        // write it to the file
                        Boolean b = BulkSVGRendererTest.this.toFile(doc, file);
                        Assert.assertEquals(Boolean.TRUE, b);
                    }
                }
                
                // move on
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    /**
     * 
     * @param document
     * @param _path
     * @return 
     */
    protected Boolean toFile(Document document, Path _path) {
        // where shall we save it?
        Path path = _path.resolveSibling(FilenameUtils.removeExtension(_path.getFileName().toString()) + ".svg");
        
        // prepare the transformer
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File(path.toString()));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transformerFactory.newTransformer();
            // set the output configuration
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            // write the XML to the file
            transformer.transform(domSource, streamResult);
            
            return true;
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(BulkSVGRendererTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(BulkSVGRendererTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    /**
     * 
     * @param path
     * @return 
     */
    protected IAtomContainer fromFile(Path path) {
        IAtomContainer mole = null;
        try {
            // open the file
            InputStream input = new FileInputStream(path.toFile());
            // parse and read in the CML file
            IChemFile chemFile = (IChemFile) (((CMLReader) new CMLReader(input)).read(new ChemFile()));
            // set the IAtomContainer molecule
            mole = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        } catch (FileNotFoundException | CDKException | NullPointerException ex) {
            // lets assume this didn't work
            Logger.getLogger(BulkSVGRendererTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return mole;
    }
}
