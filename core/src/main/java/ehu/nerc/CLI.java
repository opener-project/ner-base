/*
 * Copyright 2013 Rodrigo Agerri

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package ehu.nerc;

import ixa.kaflib.KAFDocument;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import org.jdom2.JDOMException;

/**
 * IXA-OpenNLP NERC using Apache OpenNLP.
 * 
 * @author ragerri
 * @version 1.0
 * 
 */

public class CLI {

  /**
   * 
   * BufferedReader (from standard input) and BufferedWriter are opened. The
   * module takes KAF and reads the header, the text, terms elements and uses
   * Annotate class to annotate Named Entities and to add the entities element
   * to the KAF read from standard input. Finally, the modified KAF document is
   * passed via standard output.
   * 
   * @param args
   * @throws IOException
   * @throws JDOMException
   */
  public static void main(String[] args) throws IOException, JDOMException {

    Namespace parsedArguments = null;

    // create Argument Parser
    ArgumentParser parser = ArgumentParsers
        .newArgumentParser("ehu-nerc-1.0.jar")
        .description(
            "ehu-nerc-1.0 is a multilingual NERC module developed by IXA NLP Group " +
            "based on Apache OpenNLP.\n");

    // specify language
    parser
        .addArgument("-l", "--lang")
        .choices("de","en","es","it","nl")
        .required(true)
        .help(
            "It is REQUIRED to choose a language to perform annotation with ixa-pipe-nerc");
    
    parser.addArgument("-t","--timestamp").action(Arguments.storeTrue()).help("flag to make timestamp static for continous " +
            "integration testing");
    /*
     * Parse the command line arguments
     */

    // catch errors and print help
    try {
      parsedArguments = parser.parseArgs(args);
    } catch (ArgumentParserException e) {
      parser.handleError(e);
      System.out
          .println("Run java -jar target/ehu-nerc-1.0.jar -help for details");
      System.exit(1);
    }

    /*
     * Load language parameters and construct annotators, read
     * and write kaf
     */

    String lang = parsedArguments.getString("lang");
    Annotate annotator = new Annotate(lang);
    BufferedReader breader = null;
    BufferedWriter bwriter = null;
    try {
      breader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
      bwriter = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
      
      // read KAF document from inputstream
      KAFDocument kaf = KAFDocument.createFromStream(breader);
      
      if (parsedArguments.getBoolean("timestamp") == true) {
          kaf.addLinguisticProcessor("terms","ehu-nerc-"+lang,"now", "1.0");
        }
        else {
          kaf.addLinguisticProcessor("terms", "ehu-nerc-"+lang, "1.0");
        }
      
      // annotate Named Entities to KAF
      annotator.annotateNEsToKAF(kaf);
      bwriter.write(kaf.toString());
      bwriter.close();
      breader.close();
      }
      catch (IOException e) {
      e.printStackTrace();
    }

  }
}
