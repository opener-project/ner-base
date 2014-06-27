package es.ehu.si.ixa.pipe.nerc.train;

import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.TokenNameFinderEvaluator;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;

/**
 * 
 * This interface defines the feature creation, the training method and the evaluation of the trained model.
 * 
 * @author ragerri
 *
 */
public interface NameFinderTrainer {
  
  /**
   * Generates the adaptive features to train Named Entity taggers.
   * @return the adaptive features
   */
  public AdaptiveFeatureGenerator createFeatureGenerator();
  
  /**
   * Generate {@link TokenNameFinderModel} models.
   * 
   * @param params the training parameters file
   * @return the model
   */
  public TokenNameFinderModel train(TrainingParameters params);
  
  public TokenNameFinderModel trainCrossEval(String trainData,
      String devData, TrainingParameters params, String[] evalRange);

  /**
   * Evaluate the model on a test set.
   * 
   * @param trainedModel the trained model
   * @param testSamples the test set
   * @return the results in terms of precision and recall
   */
  public TokenNameFinderEvaluator evaluate(TokenNameFinderModel trainedModel,
      ObjectStream<NameSample> testSamples); 


}
