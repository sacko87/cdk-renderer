/**
 * @file FileHandling.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date Mon Feb 2 21:56:21 2015
 *
 * @brief Basic file handling for the CML to SVG renderer.
 *
 *
 */

//

package com.progressiveaccess.cdk.renderer;

import nu.xom.ParsingException;

import org.apache.commons.io.FilenameUtils;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.w3c.dom.Document;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 */

public class FileHandler {

  /**
   *
   * @param document
   *          The output document.
   * @param path
   *          The output path.
   * @return
   */
  public static Boolean toFile(final Document document, final Path path) {
    //
    // prepare the transformer
    final DOMSource domSource = new DOMSource(document);
    final StreamResult streamResult = new StreamResult(
        new File(path.toString()));
    final TransformerFactory transformerFactory = TransformerFactory
        .newInstance();
    try {
      final Transformer transformer = transformerFactory.newTransformer();
      // set the output configuration
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
      transformer.setOutputProperty(
          "{http://xml.apache.org/xslt}indent-amount", "2");
      // write the XML to the file
      transformer.transform(domSource, streamResult);

      return true;
    } catch (final TransformerConfigurationException ex) {
      Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
    } catch (final TransformerException ex) {
      Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  /**
   *
   * @param path
   * @return
   */
  public static IAtomContainer fromFile(final Path path) {
    IAtomContainer mole = null;
    try {
      final InputStream file = new BufferedInputStream(new FileInputStream(
          path.toFile()));
      final ISimpleChemObjectReader reader = new ReaderFactory()
      .createReader(file);
      IChemFile cFile = null;
      cFile = reader.read(SilentChemObjectBuilder.getInstance().
          newInstance(IChemFile.class));
      reader.close();
      mole = ChemFileManipulator.getAllAtomContainers(cFile).get(0);
      if (!path.toString().toLowerCase().endsWith(".cml")) {
        FileHandler.buildCML(path, mole);
      }
    } catch (IOException | CDKException | NullPointerException
        | ParsingException ex) {
      Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
    }

    return mole;
  }

  public static void translateFile(final String fileName,
      final SVGRenderer renderer) {
    translateFile(Paths.get(fileName), renderer);
  }

  public static void translateFile(final String fileName,
      final JessieRenderer renderer) {
    final IAtomContainer mole = FileHandler.fromFile(Paths.get(fileName));
    if (mole != null) {
      System.out.println(renderer.render(mole));
    }
  }

  public static void translateFile(final Path file, final SVGRenderer renderer) {
    final IAtomContainer mole = FileHandler.fromFile(file);
    if (mole != null) {
      Document doc = null;
      doc = (Document) renderer.render(mole);
      final Path newFile = FileHandler.rewritePath(file, "svg");
      FileHandler.toFile(doc, newFile);
    }
  }

  // TODO (sorge) Refactor the visitor to a separate class extended
  // SimpleFileVisitor.
  public static void translateDirectory(final String pathName,
      final SVGRenderer renderer) {
    final EnumSet<FileVisitOption> opts = EnumSet
        .of(FileVisitOption.FOLLOW_LINKS);
    try {
      Files.walkFileTree(Paths.get(pathName), opts, 2,
          new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(final Path file,
            final BasicFileAttributes attrs) {
          // are we looking at a CML file?
          if (attrs.isRegularFile()) {
            FileHandler.translateFile(file, renderer);
          }
          // move on
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (final IOException e) {
      // to show which file threw the exception
      Logger.getLogger("Incorrect file " + pathName).log(Level.SEVERE, null, e);
    }

  }

  private static Path rewritePath(final Path path, final String extension) {
    final String filename = path.getFileName().toString();
    final String newFile = FilenameUtils.removeExtension(filename) + "."
        + extension;
    final Path newPath = Cli.hasOption("output") ? Paths.get(
        Cli.getOptionValue("output"), newFile) :
          path.resolveSibling(newFile);
        return newPath;
  }

  /**
   * Build the CML XOM element. Makes sure that we have object ids if the input
   * file is not a CML file. Writes the corresponding CML file.
   *
   * @throws IOException
   *           Problems with PrintWriter
   * @throws CDKException
   *           Problems with CMLWriter
   * @throws ParsingException
   *           Problems with building CML XOM.
   */
  private static void buildCML(final Path path, final IAtomContainer mol)
      throws IOException, CDKException, ParsingException {
    path.getFileName().toString();
    final OutputStream outFile = new BufferedOutputStream
        (new FileOutputStream
            (FilenameUtils.removeExtension(path.toString()) + ".cml"));
    final PrintWriter output = new PrintWriter(outFile);
    final CMLWriter cmlwriter = new CMLWriter(output);
    cmlwriter.write(mol);
    cmlwriter.close();
    output.flush();
    output.close();
  }

}
