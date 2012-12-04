package opennlp.ixa.nerc.en;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import opennlp.tools.namefind.NameSample;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FilenameUtils;

/**
 * Simple NERC module using Apache OpenNLP.
 * 
 * @author ragerri
 * @version 1.0
 * 
 */

public class CLI {

  /**
   * 
   * The args array is processed via a CommandLineParser from
   * org.apache.commons.cli API. It provides CLI facilities using the options
   * created in OptionsCLI.createOptions().
   * 
   * 
   * @param args
   * @throws IOException
   */

  public static void main(String[] args) throws IOException {

    Path infile = null;
    Path outfile = null;
    Path indir = null;

    CommandLineParser parser = new PosixParser();
    CommandLine cmd = null;

    try {
      cmd = parser.parse(OptionsCLI.createOptions(), args);
    } catch (org.apache.commons.cli.ParseException exc) {
      // something bad happened so output help message
      System.out.println();
      OptionsCLI
          .printCliHelp("Error in parsing arguments. " + exc.getMessage());
    }

    // processing command line options

    if (cmd.hasOption("help")) {
      System.out.println();
      OptionsCLI.printCliHelp("Please use one of the following options: ");
    }

    if (cmd.hasOption("plain") == false && cmd.hasOption("i")
        && cmd.getOptionValue("o") == null) {
      infile = Paths.get(cmd.getOptionValue("i"));
      outfile = Paths
          .get(FilenameUtils.removeExtension(cmd.getOptionValue("i")) + ".xml");
      Annotate.nerc2kaf(infile, outfile);
      System.out.println();
      System.out.println("Wrote KAF formatted annotation to " + outfile);
    }

    if (cmd.hasOption("plain") && cmd.hasOption("i")
        && cmd.getOptionValue("o") == null) {
      infile = Paths.get(cmd.getOptionValue("i"));
      outfile = Paths
          .get(FilenameUtils.removeExtension(cmd.getOptionValue("i")) + ".nerc");
      Annotate.nercFiles(infile, outfile);
      System.out.println();
      System.out.println("Wrote Apache OpenNLP formatted annotation to "
          + outfile);
    }

    if (cmd.hasOption("plain") == false && cmd.hasOption("o")
        && cmd.getOptionValue("i") != null) {
      infile = Paths.get(cmd.getOptionValue("i"));
      outfile = Paths.get(cmd.getOptionValue("o"));
      Annotate.nerc2kaf(infile, outfile);
      System.out.println();
      System.out.println("Wrote KAF formatted annotation to " + outfile);
    }

    if (cmd.hasOption("plain") && cmd.hasOption("o")
        && cmd.getOptionValue("i") != null) {
      infile = Paths.get(cmd.getOptionValue("i"));
      outfile = Paths.get(cmd.getOptionValue("o"));
      Annotate.nercFiles(infile, outfile);
      System.out.println();
      System.out.println("Wrote Apache OpenNLP formatted annotation to "
          + outfile);
    }

    if (cmd.hasOption("o") && cmd.getOptionValue("i") == null) {
      System.out.println();
      OptionsCLI.printCliHelp("Please use one of the following options.");
    }

    if (cmd.hasOption("indir")) {
      indir = Paths.get(cmd.getOptionValue("indir"));
      DirRecursiveWalk fileVisitor = new DirRecursiveWalk();
      Files.walkFileTree(indir, fileVisitor);
    }

    if (cmd.hasOption("plain") == false && cmd.hasOption("stdin")) {
      BufferedReader stdInReader = new BufferedReader(new InputStreamReader(
          System.in));
      BufferedWriter w = new BufferedWriter(new OutputStreamWriter(System.out,
          "UTF-8"));
      String line = null;
      KAF kaf = new KAF();
      while ((line = stdInReader.readLine()) != null) {
        Annotate.annotateNERC(line, kaf);
        w.write(kaf.toString());
        w.flush();
      }
      w.close();
    }

    if (cmd.hasOption("plain") && cmd.hasOption("stdin")) {
      BufferedReader stdInReader = new BufferedReader(new InputStreamReader(
          System.in));
      BufferedWriter w = new BufferedWriter(new OutputStreamWriter(System.out,
          "UTF-8"));
      String line = null;
      while ((line = stdInReader.readLine()) != null) {
        List<NameSample> stdInNames = Annotate.annotateNERC(line);
        for (NameSample name : stdInNames) {
          w.write(name.toString());
          w.flush();
        }
      }
      w.close();
    }

    if (args.length == 0) {
      OptionsCLI.printCliHelp("Please use one of the following options.");
    }
  }
}
