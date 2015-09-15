/*
 * Copyright 2015 Rodrigo Agerri

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
package eus.ixa.ixa.pipe.nerc.eval;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

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
import eus.ixa.ixa.pipe.nerc.train.AbstractTrainer;
import eus.ixa.ixa.pipe.nerc.train.Flags;

/**
 * Evaluation class mostly using {@link TokenNameFinderEvaluator}.
 *
 * @author ragerri
 * @version 2015-02-24
 */
public class Evaluate {

  /**
   * The reference corpus to evaluate against.
   */
  private ObjectStream<NameSample> testSamples;
  /**
   * An instance of the probabilistic {@link NameFinderME}.
   */
  private NameFinderME nameFinder;
  /**
   * The models to use for every language. The keys of the hash are the
   * language codes, the values the models.
   */
  private static ConcurrentHashMap<String, TokenNameFinderModel> nercModels =
      new ConcurrentHashMap<String, TokenNameFinderModel>();
 
  /**
   * Construct an evaluator. It takes from the properties a model,
   * a testset and the format of the testset. Every other parameter
   * set in the training, e.g., beamsize, decoding, etc., is serialized
   * in the model.
   * @param props the properties parameter
   * @throws IOException the io exception
   */
  public Evaluate(final Properties props) throws IOException {
    
    String lang = props.getProperty("language");
    String clearFeatures = props.getProperty("clearFeatures");
    String model = props.getProperty("model");
    String testSet = props.getProperty("testset");
    String corpusFormat = props.getProperty("corpusFormat");
    String netypes = props.getProperty("types");
    
    testSamples = AbstractTrainer.getNameStream(testSet, clearFeatures, corpusFormat);
    if (netypes != Flags.DEFAULT_NE_TYPES) {
      String[] neTypes = netypes.split(",");
      testSamples = new NameSampleTypeFilter(neTypes, testSamples);
    }
    nercModels.putIfAbsent(lang, new TokenNameFinderModel(new FileInputStream(model)));
    nameFinder = new NameFinderME(nercModels.get(lang));
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
