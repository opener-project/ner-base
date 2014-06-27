package es.ehu.si.ixa.pipe.nerc.eval;

import es.ehu.si.ixa.pipe.nerc.StatisticalNameFinder;
import es.ehu.si.ixa.pipe.nerc.dict.Dictionaries;
import es.ehu.si.ixa.pipe.nerc.train.AbstractNameFinderTrainer;
import es.ehu.si.ixa.pipe.nerc.train.DictNameFinderTrainer;
import es.ehu.si.ixa.pipe.nerc.train.NameFinderTrainer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import opennlp.tools.cmdline.namefind.NameEvaluationErrorListener;
import opennlp.tools.cmdline.namefind.TokenNameFinderDetailedFMeasureListener;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleTypeFilter;
import opennlp.tools.namefind.TokenNameFinderEvaluationMonitor;
import opennlp.tools.namefind.TokenNameFinderEvaluator;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.eval.EvaluationMonitor;

/**
 * Evaluation class mostly using {@link TokenNameFinderEvaluator}.
 *
 * @author ragerri
 * @version 2013-04-04
 */
public class Evaluate {

  /**
   * The reference corpus to evaluate against.
   */
  private ObjectStream<NameSample> testSamples;
  /**
   * Static instance of {@link TokenNameFinderModel}.
   */
  private static TokenNameFinderModel nercModel;
  /**
   * The name finder trainer to use for appropriate features.
   */
  private NameFinderTrainer nameFinderTrainer;
  /**
   * An instance of the probabilistic {@link NameFinderME}.
   */
  private NameFinderME nameFinder;

  /**
   * Construct an evaluator.
   *
   * @param testData the reference data to evaluate against
   * @param model the model to be evaluated
   * @param features the features
   * @param lang the language
   * @param beamsize the beam size for decoding
   * @param corpusFormat the format of the testData corpus
   * @throws IOException if input data not available
   */
  public Evaluate(final Dictionaries dictionaries, final String testData, final String model, final String features, final String lang,
      final int beamsize, final String corpusFormat, String netypes) throws IOException {

    testSamples = AbstractNameFinderTrainer.getNameStream(testData, lang, corpusFormat);
    if (netypes != null) {
      String[] neTypes = netypes.split(",");
      testSamples = new NameSampleTypeFilter(neTypes, testSamples);
    }
    InputStream trainedModelInputStream = null;
    try {
      if (nercModel == null) {
        trainedModelInputStream = new FileInputStream(model);
        nercModel = new TokenNameFinderModel(trainedModelInputStream);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (trainedModelInputStream != null) {
        try {
          trainedModelInputStream.close();
        } catch (IOException e) {
          System.err.println("Could not load model!");
        }
      }
    }
   
    if (features.equalsIgnoreCase("dict")) {
      nameFinderTrainer = new DictNameFinderTrainer(dictionaries, beamsize);
    }
    else {
      StatisticalNameFinder statFinder = new StatisticalNameFinder(lang, model, features);
      nameFinderTrainer = statFinder.getNameFinderTrainer(features, beamsize);
    }
    nameFinder = new NameFinderME(nercModel, nameFinderTrainer.createFeatureGenerator(), beamsize);
  }

  /**
   * Evaluate and print precision, recall and F measure.
   * @throws IOException if test corpus not loaded
   */
  public final void evaluate() throws IOException {
    TokenNameFinderEvaluator evaluator = new TokenNameFinderEvaluator(nameFinder);
    evaluator.evaluate(testSamples);
    System.out.println(evaluator.getFMeasure());
  }
  /**
   * Evaluate and print the precision, recall and F measure per
   * named entity class.
   *
   * @throws IOException if test corpus not loaded
   */
  public final void detailEvaluate() throws IOException {
    List<EvaluationMonitor<NameSample>> listeners = new LinkedList<EvaluationMonitor<NameSample>>();
    TokenNameFinderDetailedFMeasureListener detailedFListener = new TokenNameFinderDetailedFMeasureListener();
    listeners.add(detailedFListener);
    TokenNameFinderEvaluator evaluator = new TokenNameFinderEvaluator(nameFinder,
        listeners.toArray(new TokenNameFinderEvaluationMonitor[listeners.size()]));
    evaluator.evaluate(testSamples);
    System.out.println(detailedFListener.toString());
  }
  /**
   * Evaluate and print every error.
   * @throws IOException if test corpus not loaded
   */
  public final void evalError() throws IOException {
    List<EvaluationMonitor<NameSample>> listeners = new LinkedList<EvaluationMonitor<NameSample>>();
    listeners.add(new NameEvaluationErrorListener());
    TokenNameFinderEvaluator evaluator = new TokenNameFinderEvaluator(nameFinder,
        listeners.toArray(new TokenNameFinderEvaluationMonitor[listeners.size()]));
    evaluator.evaluate(testSamples);
    System.out.println(evaluator.getFMeasure());
  }

}
