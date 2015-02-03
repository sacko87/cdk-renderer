
//
package uk.ac.bham.cs.cdk.renderer;

/**
 *
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public final class Cli {

    private static CommandLine cl;

    private static List<String> files = new ArrayList<String>();

    protected Cli() {
    }

    public static void init(String[] args) {
	Cli.parse(args);
    }

    private static void parse( String[] args ) {
        Options options = new Options();
	// Basic Options
        options.addOption("help", false, "Print this message");
        options.addOption("d", "dir", true, "Directory to parse");
        
        CommandLineParser parser = new BasicParser();
        try {
            Cli.cl = parser.parse(options, args);
        }
        catch (ParseException e) {
            usage(options, 1);
        }
        if (Cli.cl.hasOption("help")) {
            usage(options, 0);
        }

	for (int i = 0; i < Cli.cl.getArgList().size(); i++) {
	    String fileName = Cli.cl.getArgList().get(i).toString();
	    File f = new File(fileName);
	    if (f.exists() && !f.isDirectory()) {
		Cli.files.add(fileName);
	    } else {
		Cli.warning(fileName);
	    }
	}

    }

    private static void usage(Options options, int exitValue) {

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("enrich.sh", options);
        System.exit(exitValue);
    }

    private static void warning(String fileName) {
        System.err.println("Warning: File " 
                           + fileName + " does not exist. Ignored!");
    }


    public static boolean hasOption(String option) {
        return Cli.cl.hasOption(option);
    }


    public static String getOptionValue(String option) {
        return Cli.cl.getOptionValue(option);
    }

    public static List<String> getFiles() {
        return Cli.files;
    }    

}
