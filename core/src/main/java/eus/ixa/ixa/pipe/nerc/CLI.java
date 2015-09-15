/*
 *  Copyright 2015 Rodrigo Agerri

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

package eus.ixa.ixa.pipe.nerc;

import ixa.kaflib.KAFDocument;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.TrainingParameters;

import org.jdom2.JDOMException;

import com.google.common.io.Files;

import eus.ixa.ixa.pipe.nerc.eval.CrossValidator;
import eus.ixa.ixa.pipe.nerc.eval.Evaluate;
import eus.ixa.ixa.pipe.nerc.train.FixedTrainer;
import eus.ixa.ixa.pipe.nerc.train.Flags;
import eus.ixa.ixa.pipe.nerc.train.InputOutputUtils;
import eus.ixa.ixa.pipe.nerc.train.Trainer;

/**
 * Main class of ixa-pipe-nerc, the ixa pipes (ixa2.si.ehu.es/ixa-pipes) sequence
 * labeler.
 * 
 * @author ragerri
 * @version 2015-02-26
 * 
 */
public class CLI {

  /**
   * Get dynamically the version of ixa-pipe-nerc by looking at the MANIFEST
   * file.
   */
  private final String version = CLI.class.getPackage()
      .getImplementationVersion();
  /**
   * Get the git commit of the ixa-pipe-nerc compiled by looking at the MANIFEST
   * file.
   */
  private final String commit = CLI.class.getPackage().getSpecificationVersion();
  /**
   * Name space of the arguments provided at the CLI.
   */
  private Namespace parsedArguments = null;
  /**
   * Argument parser instance.
   */
  private ArgumentParser argParser = ArgumentParsers.newArgumentParser(
      "ixa-pipe-nerc-" + version + ".jar").description(
      "ixa-pipe-nerc-" + version
          + " is a multilingual sequence labeler module developed by IXA NLP Group.\n");
  /**
   * Sub parser instance.
   */
  private Subparsers subParsers = argParser.addSubparsers().help(
      "sub-command help");
  /**
   * The parser that manages the NER tagging sub-command.
   */
  private Subparser annotateParser;
  /**
   * Parser to manage the Opinion Target Extraction sub-command.
   */
  private Subparser oteParser;
  /**
   * The parser that manages the training sub-command.
   */
  private Subparser trainParser;
  /**
   * The parser that manages the evaluation sub-command.
   */
  private Subparser evalParser;
  /**
   * The parser that manages the cross validation sub-command.
   */
  private Subparser crossValidateParser;

  /**
   * Construct a CLI object with the sub-parsers to manage the command
   * line parameters.
   */
  public CLI() {
    annotateParser = subParsers.addParser("tag").help("NER Tagging CLI");
    loadAnnotateParameters();
    oteParser = subParsers.addParser("ote").help("Opinion Target Extraction CLI");
    loadOteParameters();
    trainParser = subParsers.addParser("train").help("Training CLI");
    loadTrainingParameters();
    evalParser = subParsers.addParser("eval").help("Evaluation CLI");
    loadEvalParameters();
    crossValidateParser = subParsers.addParser("cross").help("Cross validation CLI");
    loadCrossValidateParameters();
    }

  /**
   * Main entry point of ixa-pipe-nerc.
   * 
   * @param args
   *          the arguments passed through the CLI
   * @throws IOException
   *           exception if input data not available
   * @throws JDOMException
   *           if problems with the xml formatting of NAF
   */
  public static void main(final String[] args) throws IOException,
      JDOMException {

    CLI cmdLine = new CLI();
    cmdLine.parseCLI(args);
  }

  /**
   * Parse the command interface parameters with the argParser.
   * 
   * @param args
   *          the arguments passed through the CLI
   * @throws IOException
   *           exception if problems with the incoming data
   * @throws JDOMException if xml format problems
   */
  public final void parseCLI(final String[] args) throws IOException, JDOMException {
    try {
      parsedArguments = argParser.parseArgs(args);
      System.err.println("CLI options: " + parsedArguments);
      if (args[0].equals("tag")) {
        annotate(System.in, System.out);
      } else if (args[0].equals("ote")) {
        extractOte(System.in, System.out);
      } else if (args[0].equals("eval")) {
        eval();
      } else if (args[0].equals("train")) {
        train();
      } else if (args[0].equals("cross")) {
        crossValidate();
      }
    } catch (ArgumentParserException e) {
      argParser.handleError(e);
      System.out.println("Run java -jar target/ixa-pipe-nerc-" + version
          + ".jar (tag|ote|train|eval|cross) -help for details");
      System.exit(1);
    }
  }

  /**
   * Main method to do Named Entity tagging.
   * 
   * @param inputStream
   *          the input stream containing the content to tag
   * @param outputStream
   *          the output stream providing the named entities
   * @throws IOException
   *           exception if problems in input or output streams
   * @throws JDOMException if xml formatting problems
   */
  public final void annotate(final InputStream inputStream,
      final OutputStream outputStream) throws IOException, JDOMException {

    BufferedReader breader = new BufferedReader(new InputStreamReader(
        inputStream, "UTF-8"));
    BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(
        outputStream, "UTF-8"));
    // read KAF document from inputstream
    KAFDocument kaf = KAFDocument.createFromStream(breader);
    // load parameters into a properties
    String model = parsedArguments.getString("model");
    String outputFormat = parsedArguments.getString("outputFormat");
    String lexer = parsedArguments.getString("lexer");
    String dictTag = parsedArguments.getString("dictTag");
    String dictPath = parsedArguments.getString("dictPath");
    String clearFeatures = parsedArguments.getString("clearFeatures");
    // language parameter
    String lang = null;
    if (parsedArguments.getString("language") != null) {
      lang = parsedArguments.getString("language");
      if (!kaf.getLang().equalsIgnoreCase(lang)) {
        System.err
            .println("Language parameter in NAF and CLI do not match!!");
        System.exit(1);
      }
    } else {
      lang = kaf.getLang();
    }
    Properties properties = setAnnotateProperties(model, lang, lexer, dictTag, dictPath, clearFeatures);
    KAFDocument.LinguisticProcessor newLp = kaf.addLinguisticProcessor(
        "entities", "ixa-pipe-nerc-" + Files.getNameWithoutExtension(model), version + "-" + commit);
    newLp.setBeginTimestamp();
    Annotate annotator = new Annotate(properties);
    annotator.annotateNEs(kaf);
    newLp.setEndTimestamp();
    String kafToString = null;
    if (outputFormat.equalsIgnoreCase("conll03")) {
      kafToString = annotator.annotateNEsToCoNLL2003(kaf);
    } else if (outputFormat.equalsIgnoreCase("conll02")) {
      kafToString = annotator.annotateNEsToCoNLL2002(kaf);
    } else if (outputFormat.equalsIgnoreCase("opennlp")) {
      kafToString = annotator.annotateNEsToOpenNLP(kaf);
    } else {
      kafToString = annotator.annotateNEsToKAF(kaf);
    }
    bwriter.write(kafToString);
    bwriter.close();
    breader.close();
  }
  
  /**
   * Main method to do Opinion Target Extraction (OTE).
   * 
   * @param inputStream
   *          the input stream containing the content to tag
   * @param outputStream
   *          the output stream providing the opinion targets
   * @throws IOException
   *           exception if problems in input or output streams
   * @throws JDOMException if xml formatting problems
   */
  public final void extractOte(final InputStream inputStream,
      final OutputStream outputStream) throws IOException, JDOMException {

    BufferedReader breader = new BufferedReader(new InputStreamReader(
        inputStream, "UTF-8"));
    BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(
        outputStream, "UTF-8"));
    // read KAF document from inputstream
    KAFDocument kaf = KAFDocument.createFromStream(breader);
    // load parameters into a properties
    String model = parsedArguments.getString("model");
    String outputFormat = parsedArguments.getString("outputFormat");
    String clearFeatures = parsedArguments.getString("clearFeatures");
    // language parameter
    String lang = null;
    if (parsedArguments.getString("language") != null) {
      lang = parsedArguments.getString("language");
      if (!kaf.getLang().equalsIgnoreCase(lang)) {
        System.err
            .println("Language parameter in NAF and CLI do not match!!");
        System.exit(1);
      }
    } else {
      lang = kaf.getLang();
    }
    Properties properties = setOteProperties(model, lang, clearFeatures);
    KAFDocument.LinguisticProcessor newLp = kaf.addLinguisticProcessor(
        "opinions", "ixa-pipe-nerc-" + Files.getNameWithoutExtension(model), version + "-" + commit);
    newLp.setBeginTimestamp();
    OpinionTargetExtractor oteExtractor = new OpinionTargetExtractor(properties);
    oteExtractor.extractOpinionTargets(kaf);
    newLp.setEndTimestamp();
    String kafToString = null;
    if (outputFormat.equalsIgnoreCase("opennlp")) {
      kafToString = oteExtractor.annotateOTEsToOpenNLP(kaf);
    } else {
      kafToString = oteExtractor.annotateOTEsToKAF(kaf);
    }
    bwriter.write(kafToString);
    bwriter.close();
    breader.close();
  }

  /**
   * Main access to the train functionalities.
   * 
   * @throws IOException
   *           input output exception if problems with corpora
   */
  public final void train() throws IOException {

    // load training parameters file
    String paramFile = parsedArguments.getString("params");
    TrainingParameters params = InputOutputUtils
        .loadTrainingParameters(paramFile);
    String outModel = null;
    if (params.getSettings().get("OutputModel") == null || params.getSettings().get("OutputModel").length() == 0) {
      outModel = Files.getNameWithoutExtension(paramFile) + ".bin";
      params.put("OutputModel", outModel);
    }
    else {
      outModel = Flags.getModel(params);
    }
    Trainer nercTrainer = new FixedTrainer(params);
    TokenNameFinderModel trainedModel = nercTrainer.train(params);
    CmdLineUtil.writeModel("ixa-pipe-nerc", new File(outModel), trainedModel);
  }

  /**
   * Main evaluation entry point.
   * 
   * @throws IOException
   *           throws exception if test set not available
   */
  public final void eval() throws IOException {

    String lang = parsedArguments.getString("language");
    String model = parsedArguments.getString("model");
    String testset = parsedArguments.getString("testset");
    String corpusFormat = parsedArguments.getString("corpusFormat");
    String netypes = parsedArguments.getString("types");
    String clearFeatures = parsedArguments.getString("clearFeatures");
    Properties props = setEvalProperties(lang, model, testset, corpusFormat, netypes, clearFeatures);
    
      Evaluate evaluator = new Evaluate(props);
      if (parsedArguments.getString("evalReport") != null) {
        if (parsedArguments.getString("evalReport").equalsIgnoreCase("brief")) {
          evaluator.evaluate();
        } else if (parsedArguments.getString("evalReport").equalsIgnoreCase(
            "error")) {
          evaluator.evalError();
        } else if (parsedArguments.getString("evalReport").equalsIgnoreCase(
            "detailed")) {
          evaluator.detailEvaluate();
        }
      } else {
        evaluator.detailEvaluate();
      }
  }
  
  /**
   * Main access to the cross validation.
   * 
   * @throws IOException
   *           input output exception if problems with corpora
   */
  public final void crossValidate() throws IOException {

    String paramFile = parsedArguments.getString("params");
    TrainingParameters params = InputOutputUtils
        .loadTrainingParameters(paramFile);
    CrossValidator crossValidator = new CrossValidator(params);
    crossValidator.crossValidate(params);
  }

  /**
   * Create the available parameters for NER tagging.
   */
  private void loadAnnotateParameters() {
    
    annotateParser.addArgument("-m", "--model")
        .required(true)
        .help("Pass the model to do the tagging as a parameter.\n");
    annotateParser.addArgument("--clearFeatures")
        .required(false)
        .choices("yes", "no", "docstart")
        .setDefault(Flags.DEFAULT_FEATURE_FLAG)
        .help("Reset the adaptive features every sentence; defaults to 'no'; if -DOCSTART- marks" +
        		" are present, choose 'docstart'.\n");
    annotateParser.addArgument("-l","--language")
        .required(false)
        .choices("de", "en", "es", "eu", "it", "nl")
        .help("Choose language; it defaults to the language value in incoming NAF file.\n");
    annotateParser.addArgument("-o","--outputFormat")
        .required(false)
        .choices("conll03", "conll02", "naf", "opennlp")
        .setDefault(Flags.DEFAULT_OUTPUT_FORMAT)
        .help("Choose output format; it defaults to NAF.\n");
    annotateParser.addArgument("--lexer")
        .choices("numeric")
        .setDefault(Flags.DEFAULT_LEXER)
        .required(false)
        .help("Use lexer rules for NERC tagging; it defaults to false.\n");
    annotateParser.addArgument("--dictTag")
        .required(false)
        .choices("tag", "post")
        .setDefault(Flags.DEFAULT_DICT_OPTION)
        .help("Choose to directly tag entities by dictionary look-up; if the 'tag' option is chosen, " +
        		"only tags entities found in the dictionary; if 'post' option is chosen, it will " +
        		"post-process the results of the statistical model.\n");
    annotateParser.addArgument("--dictPath")
        .required(false)
        .setDefault(Flags.DEFAULT_DICT_PATH)
        .help("Provide the path to the dictionaries for direct dictionary tagging; it ONLY WORKS if --dictTag " +
        		"option is activated.\n");
  }
  
  /**
   * Create the available parameters for Opinion Target Extraction.
   */
  private void loadOteParameters() {
    
    oteParser.addArgument("-m", "--model")
        .required(true)
        .help("Pass the model to do the tagging as a parameter.\n");
    oteParser.addArgument("--clearFeatures")
        .required(false)
        .choices("yes", "no", "docstart")
        .setDefault(Flags.DEFAULT_FEATURE_FLAG)
        .help("Reset the adaptive features every sentence; defaults to 'no'; if -DOCSTART- marks" +
                " are present, choose 'docstart'.\n");
    oteParser.addArgument("-l","--language")
        .required(false)
        .choices("en")
        .help("Choose language; it defaults to the language value in incoming NAF file.\n");
    oteParser.addArgument("-o","--outputFormat")
        .required(false)
        .choices("naf", "opennlp")
        .setDefault(Flags.DEFAULT_OUTPUT_FORMAT)
        .help("Choose output format; it defaults to NAF.\n");
  }

  /**
   * Create the main parameters available for training sequence labeling models.
   */
  private void loadTrainingParameters() {
    trainParser.addArgument("-p", "--params").required(true)
        .help("Load the training parameters file\n");
  }

  /**
   * Create the parameters available for evaluation.
   */
  private void loadEvalParameters() {
    evalParser.addArgument("-l", "--language")
        .required(true)
        .choices("de", "en", "es", "eu", "it", "nl")
        .help("Choose language.\n");
    evalParser.addArgument("-m", "--model")
        .required(false)
        .setDefault(Flags.DEFAULT_EVALUATE_MODEL)
        .help("Pass the model to evaluate as a parameter.\n");
    evalParser.addArgument("-t", "--testset")
        .required(true)
        .help("The test or reference corpus.\n");
    evalParser.addArgument("--clearFeatures")
        .required(false)
        .choices("yes", "no", "docstart")
        .setDefault(Flags.DEFAULT_FEATURE_FLAG)
        .help("Reset the adaptive features; defaults to 'no'.\n");
    evalParser.addArgument("-f","--corpusFormat")
        .required(false)
        .choices("conll02", "conll03", "opennlp")
        .setDefault(Flags.DEFAULT_EVAL_FORMAT)
        .help("Choose format of reference corpus; it defaults to conll02 format.\n");
    evalParser.addArgument("--evalReport")
        .required(false)
        .choices("brief", "detailed", "error")
        .help("Choose level of detail of evaluation report; it defaults to detailed evaluation.\n");
    evalParser.addArgument("--types")
        .required(false)
        .setDefault(Flags.DEFAULT_NE_TYPES)
        .help("Choose which Sequence types used for evaluation; the argument must be a comma separated" +
        		" string; e.g., 'person,organization'.\n");
            
  }
  
  /**
   * Create the main parameters available for training NERC models.
   */
  private void loadCrossValidateParameters() {
    crossValidateParser.addArgument("-p", "--params").required(true)
        .help("Load the Cross validation parameters file\n");
  }

  /**
   * Set a Properties object with the CLI parameters for NER annotation.
   * @param model the model parameter
   * @param language language parameter
   * @param lexer rule based parameter
   * @param dictTag directly tag from a dictionary
   * @param dictPath directory to the dictionaries
   * @return the properties object
   */
  private Properties setAnnotateProperties(String model, String language, String lexer, String dictTag, String dictPath, String clearFeatures) {
    Properties annotateProperties = new Properties();
    annotateProperties.setProperty("model", model);
    annotateProperties.setProperty("language", language);
    annotateProperties.setProperty("ruleBasedOption", lexer);
    annotateProperties.setProperty("dictTag", dictTag);
    annotateProperties.setProperty("dictPath", dictPath);
    annotateProperties.setProperty("clearFeatures", clearFeatures);
    return annotateProperties;
  }
  
  /**
   * Set a Properties object with the CLI parameters for Opinion Target Extraction.
   * @param model the model parameter
   * @param language language parameter
   * @param lexer rule based parameter
   * @param dictTag directly tag from a dictionary
   * @param dictPath directory to the dictionaries
   * @return the properties object
   */
  private Properties setOteProperties(String model, String language, String clearFeatures) {
    Properties oteProperties = new Properties();
    oteProperties.setProperty("model", model);
    oteProperties.setProperty("language", language);
    oteProperties.setProperty("clearFeatures", clearFeatures);
    return oteProperties;
  }
  
  /**
   * Set a Properties object with the CLI parameters for evaluation.
   * @param model the model parameter
   * @param testset the reference set
   * @param corpusFormat the format of the testset
   * @param netypes the ne types to use in the evaluation
   * @return the properties object
   */
  private Properties setEvalProperties(String language, String model, String testset, String corpusFormat, String netypes, String clearFeatures) {
    Properties evalProperties = new Properties();
    evalProperties.setProperty("language", language);
    evalProperties.setProperty("model", model);
    evalProperties.setProperty("testset", testset);
    evalProperties.setProperty("corpusFormat", corpusFormat);
    evalProperties.setProperty("types", netypes);
    evalProperties.setProperty("clearFeatures", clearFeatures);
    return evalProperties;
  }

}
