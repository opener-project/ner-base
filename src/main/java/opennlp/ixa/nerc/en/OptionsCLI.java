package opennlp.ixa.nerc.en;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * CommandLineInterface options using commons.cli API.
 *
 * @author ragerri
 *
 */
public class OptionsCLI {

	/**
	 * It creates a set of both boolean and argument options for the CLI.
	 * @return all the created options
	 */
	public static Options createOptions() {

	    Options myOptions = new Options();

	    // these are boolean options. They do not need arguments to be passed
            Option stdin = new Option("stdin",false,"will read from stdin and print to stdout");
            Option plain = new Option("plain",false,"will output annotated text in Apache OpenNLP format; default output is KAF");
	    Option help = new Option("help", false, "will print usage to console");

	    myOptions.addOption(stdin);
	    myOptions.addOption(help);
	    myOptions.addOption(plain);

	    // Argument Options are built using OptionBuilder

	    // inputFile <file> option

	    OptionBuilder.withArgName("inputFile");
	    OptionBuilder.hasArg(true);
	    OptionBuilder
	        .withDescription("choose inputFile to annotate");
	    OptionBuilder.isRequired(false);
	    myOptions.addOption(OptionBuilder.create("i"));

	    // outputFile <file> option

	    OptionBuilder.withArgName("outputFile");
	    OptionBuilder.hasArg(true);
	    OptionBuilder
	        .withDescription("choose output file to save annotated text; otherwise an "
	            + "output file 'inputFile.xml' will be created. This option requires '-i'.");
	    OptionBuilder.isRequired(false);
	    myOptions.addOption(OptionBuilder.create("o"));

	    // recursively annotate any txt file option
	    OptionBuilder.withArgName("indir");
            OptionBuilder.hasArg(true);
            OptionBuilder
            .withDescription("choose input directory containing files to be annotated; " +
                    "the directory will be recursively walked and files will be " +
                    "annotated in KAF format.");
            OptionBuilder.isRequired(false);
            myOptions.addOption(OptionBuilder.create("indir"));

            return myOptions;
	  }

	  /**
	   * It creates a Help message using the HelpFormmater class from commons.cli API
	   * @param message
	   */
	  public static void printCliHelp(String message) {
	    System.out.println(message);
	    HelpFormatter formatter = new HelpFormatter();
	    System.out.println();
	    formatter.printHelp("java -jar target/nerc.en-1.0.jar",
	        createOptions());
	    System.out.println();
	    System.out.println("## Or you can also use execute it via Maven: ");
	    System.out.println();
	    formatter.printHelp("bin/nerc-en", createOptions());
	    System.exit(-1);
	  }


}
