package es.ehu.si.ixa.pipe.nerc.train;

import java.io.IOException;
import java.util.List;

import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import opennlp.tools.util.featuregen.CachedFeatureGenerator;
import opennlp.tools.util.featuregen.SuffixFeatureGenerator;
import es.ehu.si.ixa.pipe.nerc.features.Prefix34FeatureGenerator;

/**
 * Training NER based on Apache OpenNLP Machine Learning API.
 * This class implements baseline shape features on top of the {@link DefaultNameFinderTrainer}
 * features. 
 * 
 * @author ragerri 2014/06/25
 * 
 */

public class BaselineNameFinderTrainer extends AbstractNameFinderTrainer {

  public BaselineNameFinderTrainer(String trainData, String testData,
      String lang, int beamsize, String corpusFormat, String netypes)
      throws IOException {
    super(trainData, testData, lang, beamsize, corpusFormat, netypes);
    features = createFeatureGenerator();
  }

  public BaselineNameFinderTrainer(int beamsize) {
    super(beamsize);
    features = createFeatureGenerator();
  }

  public AdaptiveFeatureGenerator createFeatureGenerator() {
    List<AdaptiveFeatureGenerator> featureList = DefaultNameFinderTrainer
        .createFeatureList();
    addToFeatureList(featureList);
    AdaptiveFeatureGenerator[] featuresArray = featureList
        .toArray(new AdaptiveFeatureGenerator[featureList.size()]);
    return new CachedFeatureGenerator(featuresArray);
  }

  /**
   * Adds the Baseline features to the feature list.
   * 
   * @param featureList
   *          the feature list containing the baseline features
   */
  public static void addToFeatureList(List<AdaptiveFeatureGenerator> featureList) {
    featureList.add(new Prefix34FeatureGenerator());
    featureList.add(new SuffixFeatureGenerator());

  }

}
