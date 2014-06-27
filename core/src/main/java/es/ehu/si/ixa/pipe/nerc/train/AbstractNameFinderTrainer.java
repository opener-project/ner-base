package es.ehu.si.ixa.pipe.nerc.train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.NameSampleTypeFilter;
import opennlp.tools.namefind.TokenNameFinderEvaluator;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;

import org.apache.commons.io.FileUtils;

/**
 * Abstract class for common training functionalities. Every other trainer
 * class needs to extend this class.
 *
 * @author ragerri
 * @version 2014-04-17
 *
 */
public abstract class AbstractNameFinderTrainer implements NameFinderTrainer {

  protected String lang;
  /**
   * String holding the training data.
   */
  protected String trainData;
  /**
   * String holding the testData.
   */
  protected String testData;
  /**
   * ObjectStream of the training data.
   */
  protected ObjectStream<NameSample> trainSamples;
  /**
   * ObjectStream of the test data.
   */
  protected ObjectStream<NameSample> testSamples;
  /**
   * beamsize value needs to be established in any class extending this one.
   */
  protected int beamSize;
  /**
   * features needs to be implemented by any class extending this one.
   */
  protected AdaptiveFeatureGenerator features;

  /**
   * Constructs a trainer with training and test data, and with options
   * for language, beamsize for decoding, and corpus format (conll or opennlp).
   * 
   * @param trainData
   * @param testData
   * @param lang
   * @param beamsize
   * @param corpusFormat
   * @throws IOException
   */
  public AbstractNameFinderTrainer(String trainData, String testData, String lang, int beamsize, String corpusFormat, String netypes) throws IOException {

    this.lang = lang;
    this.trainData = trainData;
    this.testData = testData;
    trainSamples = getNameStream(trainData, lang, corpusFormat);
    testSamples = getNameStream(testData, lang, corpusFormat);
    this.beamSize = beamsize;
    if (netypes.length() != 0) {
      String[] neTypes = netypes.split(",");
      trainSamples = new NameSampleTypeFilter(neTypes, trainSamples);
      testSamples = new NameSampleTypeFilter(neTypes, testSamples);
    }
  }

  /**
   * Constructs a trainer with only beamsize as option.
   * This is used for tagging time with the appropriate features.
   * @param beamsize
   */
  public AbstractNameFinderTrainer(final int beamsize) {
    this.beamSize = beamsize;
  }

  /* (non-Javadoc)
   * @see es.ehu.si.ixa.pipe.nerc.train.NameFinderTrainer#train(opennlp.tools.util.TrainingParameters)
   */
  public TokenNameFinderModel train(TrainingParameters params) {
    if (features == null) {
      throw new IllegalStateException(
      "Classes derived from AbstractNameFinderTrainer must create and fill the AdaptiveFeatureGenerator features!");
    }
    Map<String, Object> resources = null;
    TokenNameFinderModel trainedModel = null;
    TokenNameFinderEvaluator nerEvaluator = null;
    try {
      trainedModel = NameFinderME.train(lang, null,
          trainSamples, params, features, resources);
      nerEvaluator = evaluate(trainedModel, testSamples);
    } catch (IOException e) {
      System.err.println("IO error while loading traing and test sets!");
      e.printStackTrace();
      System.exit(1);
    }
    System.out.println("Final Result: \n" + nerEvaluator.getFMeasure());
    return trainedModel;
  }

  /* (non-Javadoc)
   * @see es.ehu.si.ixa.pipe.nerc.train.NameFinderTrainer#trainCrossEval(java.lang.String, java.lang.String, opennlp.tools.util.TrainingParameters, java.lang.String[])
   */
  public TokenNameFinderModel trainCrossEval(String trainData,
      final String devData, final TrainingParameters params, final String[] evalRange) {

    // get best parameters from cross evaluation
    List<Integer> bestParams = null;
    try {
      bestParams = crossEval(trainData, devData, params, evalRange);
    } catch (IOException e) {
      System.err.println("IO error while loading training and test sets!");
      e.printStackTrace();
      System.exit(1);
    }
    TrainingParameters crossEvalParams = new TrainingParameters();
    crossEvalParams.put(TrainingParameters.ALGORITHM_PARAM, params.algorithm());
    crossEvalParams.put(TrainingParameters.ITERATIONS_PARAM,
        Integer.toString(bestParams.get(0)));
    crossEvalParams.put(TrainingParameters.CUTOFF_PARAM,
        Integer.toString(bestParams.get(1)));

    // use best parameters to train model
    TokenNameFinderModel trainedModel = train(crossEvalParams);
    return trainedModel;
  }

  protected List<Integer> crossEval(String trainData, String devData,
      TrainingParameters params, String[] evalRange) throws IOException {

    System.err.println("Cross Evaluation:");
    // lists to store best parameters
    List<List<Integer>> allParams = new ArrayList<List<Integer>>();
    List<Integer> finalParams = new ArrayList<Integer>();
    Map<String, Object> resources = null;

    // F:<iterations,cutoff> Map
    Map<List<Integer>, Double> results = new LinkedHashMap<List<Integer>, Double>();
    // maximum iterations and cutoff
    Integer cutoffParam = Integer.valueOf(params.getSettings().get(
        TrainingParameters.CUTOFF_PARAM));
    List<Integer> cutoffList = new ArrayList<Integer>(Collections.nCopies(
        cutoffParam, 0));
    Integer iterParam = Integer.valueOf(params.getSettings().get(
        TrainingParameters.ITERATIONS_PARAM));
    List<Integer> iterList = new ArrayList<Integer>(Collections.nCopies(
        iterParam, 0));

    for (int c = 0; c < cutoffList.size() + 1; c++) {
      int start = Integer.valueOf(evalRange[0]);
      int iterRange = Integer.valueOf(evalRange[1]);
      for (int i = start + 10; i < iterList.size() + 10; i += iterRange) {
        // reading data for training and test
        ObjectStream<String> trainStream = InputOutputUtils.readInputData(trainData);
        ObjectStream<String> devStream = InputOutputUtils.readInputData(devData);
        ObjectStream<NameSample> trainSamples = new Conll03NameStream(lang, trainStream);
        ObjectStream<NameSample> devSamples = new Conll03NameStream(lang, devStream);

        // dynamic creation of parameters
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(i));
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(c));
        System.out.println("Trying with " + i + " iterations...");

        // training model
        TokenNameFinderModel trainedModel = NameFinderME.train(lang, null,
            trainSamples, params, features, resources);
        // evaluate model
        TokenNameFinderEvaluator nerEvaluator = this.evaluate(trainedModel,
            devSamples);
        double result = nerEvaluator.getFMeasure().getFMeasure();
        double precision = nerEvaluator.getFMeasure().getPrecisionScore();
        double recall = nerEvaluator.getFMeasure().getRecallScore();
        StringBuilder sb = new StringBuilder();
        sb.append("Iterations: ").append(i).append(" cutoff: ").append(c)
            .append(" ").append("PRF: ").append(precision).append(" ")
            .append(recall).append(" ").append(result).append("\n");
        FileUtils.write(new File("ner-results.txt"), sb.toString(), true);
        List<Integer> bestParams = new ArrayList<Integer>();
        bestParams.add(i);
        bestParams.add(c);
        results.put(bestParams, result);
        System.out.println();
        System.out.println("Iterations: " + i + " cutoff: " + c);
        System.out.println(nerEvaluator.getFMeasure());
      }
    }
    // print F1 results by iteration
    System.out.println();
    InputOutputUtils.printIterationResults(results);
    InputOutputUtils.getBestIterations(results, allParams);
    finalParams = allParams.get(0);
    System.out.println("Final Params " + finalParams.get(0) + " "
        + finalParams.get(1));
    return finalParams;
  }

  public TokenNameFinderEvaluator evaluate(TokenNameFinderModel trainedModel,
      ObjectStream<NameSample> testSamples) {
    NameFinderME nerTagger = new NameFinderME(trainedModel, features, beamSize);
    TokenNameFinderEvaluator nerEvaluator = new TokenNameFinderEvaluator(
        nerTagger);
    try {
      nerEvaluator.evaluate(testSamples);
    } catch (IOException e) {
      System.err.println("IO error while loading test set for evaluation!");
      e.printStackTrace();
      System.exit(1);
    }
    return nerEvaluator;
  }
  
  public static ObjectStream<NameSample> getNameStream(String trainData, String lang, String corpusFormat) throws IOException {
	  ObjectStream<NameSample> samples = null;
	  if (corpusFormat.equalsIgnoreCase("conll")) {
		  ObjectStream<String> nameStream = InputOutputUtils.readInputData(trainData);
	      samples = new Conll03NameStream(lang, nameStream);
	  }
	  else if (corpusFormat.equalsIgnoreCase("conll") && (lang.equalsIgnoreCase("es") || lang.equalsIgnoreCase("nl"))) {
	    ObjectStream<String> nameStream = InputOutputUtils.readInputData(trainData);
		samples = new Conll02NameStream(lang, nameStream);
	  }
	  else if (corpusFormat.equalsIgnoreCase("opennlp")){
	    ObjectStream<String> nameStream = InputOutputUtils.readInputData(trainData);
		samples = new NameSampleDataStream(nameStream);	
	  }
	  else {
	    System.err.println("Test set corpus format not valid!!");
	    System.exit(1);
	  }
	  return samples;
  }


}
