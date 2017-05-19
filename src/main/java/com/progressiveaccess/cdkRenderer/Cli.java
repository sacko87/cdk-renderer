//

package com.progressiveaccess.cdkRenderer;

/**
 *
 */

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Cli {

  private static CommandLine cl;

  private static List<String> files = new ArrayList<String>();

  protected Cli() {
  }

  public static void init(final String[] args) {
    Cli.parse(args);
  }

  private static void parse(final String[] args) {
    final Options options = new Options();
    // Basic Options
    options.addOption("help", false, "Print this message");
    options.addOption("d", "dir", true, "Directory to parse");
    options.addOption("j", "jessie", false, "JessieCode output");
    options.addOption("o", "output", true, "Output directory");
    options.addOption("e", "explicit", false, "Explicit Carbons");

    final CommandLineParser parser = new BasicParser();
    try {
      Cli.cl = parser.parse(options, args);
    } catch (final ParseException e) {
      usage(options, 1);
    }
    if (Cli.cl.hasOption("help")) {
      usage(options, 0);
    }

    for (int i = 0; i < Cli.cl.getArgList().size(); i++) {
      final String fileName = Cli.cl.getArgList().get(i).toString();
      final File f = new File(fileName);
      if (f.exists() && !f.isDirectory()) {
        Cli.files.add(fileName);
      } else {
        Cli.warning(fileName);
      }
    }

  }

  private static void usage(final Options options, final int exitValue) {

    final HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("enrich.sh", options);
    System.exit(exitValue);
  }

  private static void warning(final String fileName) {
    System.err.println("Warning: File "
        + fileName + " does not exist. Ignored!");
  }

  public static boolean hasOption(final String option) {
    return Cli.cl.hasOption(option);
  }

  public static String getOptionValue(final String option) {
    return Cli.cl.getOptionValue(option);
  }

  public static List<String> getFiles() {
    return Cli.files;
  }

}
