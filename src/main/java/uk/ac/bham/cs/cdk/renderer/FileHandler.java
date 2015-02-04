/**
 * @file   FileHandling.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Mon Feb  2 21:56:21 2015
 * 
 * @brief  Basic file handling for the CML to SVG renderer.
 * 
 * 
 */

//
package uk.ac.bham.cs.cdk.renderer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.CMLReader;
import org.w3c.dom.Document;
import java.nio.file.Path;
import java.util.EnumSet;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;


/**
 *
 */

public class FileHandler {

    protected static final double DEFAULT_WIDTH = 400.;
    protected static final double DEFAULT_HEIGHT = 400.;

    /**
     * 
     * @param document
     * @param path
     * @return 
     */
    public static Boolean toFile(Document document, Path path) {
        // where shall we save it?
        String filename = path.getFileName().toString();
        path = path.resolveSibling(FilenameUtils.removeExtension(filename) + ".svg");
        
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
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return false;
    }
    

    /**
     * 
     * @param path
     * @return 
     */
    public static IAtomContainer fromFile(Path path) {
        IAtomContainer mole = null;
        try {
            // open the file
            InputStream input = new FileInputStream(path.toFile());
            // parse and read in the CML file
            IChemFile chemFile = (IChemFile) (new CMLReader(input)).read(new ChemFile());
            // set the IAtomContainer molecule
            mole = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        } catch (FileNotFoundException | CDKException | NullPointerException ex) {
            // lets assume this didn't work
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return mole;
    }


    public static void translateFile(String fileName, SVGRenderer renderer) {
        translateFile(Paths.get(fileName), renderer);
    }


    public static void translateFile(Path file, SVGRenderer renderer) {
        if(file.toString().toLowerCase().endsWith(".cml")) {
            // read in the molecule from the CML
            IAtomContainer mole = FileHandler.fromFile(file);
            if (mole != null) {
                Document doc = null;
                // render the molecule
                doc = (Document)renderer.render(mole,
                                                FileHandler.DEFAULT_WIDTH,
                                                FileHandler.DEFAULT_HEIGHT);
                // write it to // TODO: he file
                FileHandler.toFile(doc, file);
            }
        }
    }


    // TODO (sorge) Refactor the visitor to a separate class extended SimpleFileVisitor.
    public static void translateDirectory(String pathName, SVGRenderer renderer) {
        EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        try {
            Files.walkFileTree(Paths.get(pathName), opts, 2, new SimpleFileVisitor<Path>() {
                    @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        // are we looking at a CML file?
                        if(attrs.isRegularFile()) {
                            FileHandler.translateFile(file, renderer);
                        }
                        // move on
                        return FileVisitResult.CONTINUE;
                    }
                });
        } catch(IOException e) {
            // to show which file threw the exception
            Logger.getLogger("Incorrect file " + pathName).log(Level.SEVERE, null, e);
        }
        
    } 
}
