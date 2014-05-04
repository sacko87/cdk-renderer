package uk.ac.bham.cs.cdk.renderer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.w3c.dom.Document;
import uk.ac.bham.cs.cdk.renderer.generators.BasicAtomGenerator;
import uk.ac.bham.cs.cdk.renderer.generators.BasicBondGenerator;

public class SVGRendererTest {
    @Test
    public void test123TriazoleRenderer() throws IOException, CDKException, TransformerConfigurationException, TransformerException {
        IAtomContainer mole = MoleculeFactory.make123Triazole();
        
        // pfft, no idea
        StructureDiagramGenerator sdg = new StructureDiagramGenerator(mole);
        sdg.setMolecule(mole); // seems redundant
        try {
            // calculate the coordinates
            sdg.generateCoordinates();
        } catch (CDKException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        // refresh the molecule
        mole = sdg.getMolecule();
        
        // create the "generators"
        List<IGenerator<IAtomContainer>> generators
                = new ArrayList<>();
        generators.add(new BasicSceneGenerator());
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());

        // we need a renderer model
        RendererModel rendererModel = new RendererModel();

        // register all generators to the model
        for (IGenerator<IAtomContainer> generator : generators) {
            rendererModel.registerParameters(generator);
        }

        // generate a renderable diagram of the molecule
        ElementGroup diagram = new ElementGroup();
        for (IGenerator<IAtomContainer> generator : generators) {
            diagram.add(generator.generate(mole, rendererModel));
        }
        
        SVGRenderer r = new SVGRenderer(rendererModel);
        Document d = (Document) r.render(diagram, mole, 200.0, 200.0);
        
        SVGRendererTest.toFile("triazole.svg", d);
    }
    
    @Test
    public void testFromFileRenderer() throws IOException, CDKException, TransformerConfigurationException, TransformerException {
        // generate a molecule
        IAtomContainer mole = null;
        
        InputStream i = this.getClass().getClassLoader().getResourceAsStream("example.cml");
        Assert.assertNotEquals(null, i);
        
        CMLReader reader = new CMLReader(i);
        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
        Assert.assertNotEquals(null, chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        
        IChemSequence chemSeq = chemFile.getChemSequence(0);
        Assert.assertEquals(1, chemSeq.getChemModelCount());
        
        IChemModel chemModel = chemSeq.getChemModel(0);
        Assert.assertNotEquals(null, chemModel.getMoleculeSet());
        
        IAtomContainerSet acs = chemModel.getMoleculeSet();
        Assert.assertEquals(1, acs.getAtomContainerCount());
        
        mole = acs.getAtomContainer(0);
        
        // create the "generators"
        List<IGenerator<IAtomContainer>> generators
                = new ArrayList<>();
        generators.add(new BasicSceneGenerator());
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());

        // we need a renderer model
        RendererModel rendererModel = new RendererModel();

        // register all generators to the model
        for (IGenerator<IAtomContainer> generator : generators) {
            rendererModel.registerParameters(generator);
        }

        // generate a renderable diagram of the molecule
        ElementGroup diagram = new ElementGroup();
        for (IGenerator<IAtomContainer> generator : generators) {
            diagram.add(generator.generate(mole, rendererModel));
        }
        
        SVGRenderer r = new SVGRenderer(rendererModel);
        Document d = (Document) r.render(diagram, mole, 200.0, 200.0);
        
        SVGRendererTest.toFile("example.svg", d);
    }
    
    private static void toFile(String name, Document document) throws TransformerConfigurationException, TransformerException {
        DOMSource ds = new DOMSource(document);
        StreamResult sr = new StreamResult(new File(name));
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        t.transform(ds, sr);
    }
}
