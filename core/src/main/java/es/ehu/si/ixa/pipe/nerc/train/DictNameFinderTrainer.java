package es.ehu.si.ixa.pipe.nerc.train;

import java.io.IOException;
import java.util.List;

import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import opennlp.tools.util.featuregen.CachedFeatureGenerator;
import es.ehu.si.ixa.pipe.nerc.dict.Dictionaries;
import es.ehu.si.ixa.pipe.nerc.dict.Dictionary;
import es.ehu.si.ixa.pipe.nerc.features.DictionaryFeatureGenerator;

/**
 * Training NER based on Apache OpenNLP Machine Learning API. This class
 * implements Gazetteer features.
 *
 * @author ragerri 2014/06/25
 *
 */

public class DictNameFinderTrainer extends AbstractNameFinderTrainer {

  /**
   * The {@link Dictionaries} contained in the given directory.
   */
  private static Dictionaries dictionaries;
  /**
   * A {@link Dictionary} object.
   */
  private static Dictionary dictionary;
  /**
   * The prefix to be used in the {@link DictionaryFeatureGenerator}.
   */
  private static String prefix;

  /**
   * Construct a DictionaryNameFinderTrainer.
   *
   * @param dictPath the path to the dictionaries to be used as features
   * @param trainData the training data
   * @param testData the test data for evaluation after training
   * @param lang the language
   * @param beamsize the beamsize for decoding it defaults to 3
   * @param corpusFormat the format is either opennlp or conll format
   * @param netypes filter by named entity classes to train specialized models
   * @throws IOException throws an exception
   */
  public DictNameFinderTrainer(final Dictionaries aDictionaries, final String trainData,
      final String testData, final String lang, final int beamsize, final String corpusFormat,
      final String netypes) throws IOException {
    super(trainData, testData, lang, beamsize, corpusFormat, netypes);

    if (dictionaries == null) {
      dictionaries = aDictionaries;  
    }
    features = createFeatureGenerator();

  }

  /**
   * Construct a DictionaryNameFinderTrainer for evaluation only.
   *
   * @param dictPath the path to the dictionaries
   * @param beamsize the beamsize for decoding; it defaults to 3
   */
  public DictNameFinderTrainer(final Dictionaries aDictionaries, final int beamsize) {
    super(beamsize);

    if (dictionaries == null) {
      dictionaries = aDictionaries;
    }
    features = createFeatureGenerator();

  }

  /*
   * Creates a feature list to which the dictionary features are added.
   *  (non-Javadoc)
   * @see es.ehu.si.ixa.pipe.nerc.train.NameFinderTrainer#createFeatureGenerator()
   */
  public final AdaptiveFeatureGenerator createFeatureGenerator() {
    List<AdaptiveFeatureGenerator> featureList = DefaultNameFinderTrainer.createFeatureList();
    BaselineNameFinderTrainer.addToFeatureList(featureList);
    addDictionariesToFeatureList(featureList);
    AdaptiveFeatureGenerator[] featuresArray = featureList
        .toArray(new AdaptiveFeatureGenerator[featureList.size()]);
    return new CachedFeatureGenerator(featuresArray);
  }

  /**
   * Adds the dictionary features to the feature list.
   *
   * @param featureList the feature list containing the dictionary features
   */
  private static void addDictionariesToFeatureList(
      List<AdaptiveFeatureGenerator> featureList) {
    for (int i = 0; i < dictionaries.getIgnoreCaseDictionaries().size(); i++) {
      prefix = dictionaries.getDictNames().get(i);
      dictionary = dictionaries.getIgnoreCaseDictionaries().get(i);
      featureList.add(new DictionaryFeatureGenerator(prefix, dictionary));
    }
  }

}
