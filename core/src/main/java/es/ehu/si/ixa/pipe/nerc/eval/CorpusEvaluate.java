package es.ehu.si.ixa.pipe.nerc.eval;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleTypeFilter;
import opennlp.tools.namefind.TokenNameFinderEvaluator;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.eval.FMeasure;
import es.ehu.si.ixa.pipe.nerc.train.AbstractNameFinderTrainer;

/**
 * Evaluation class mostly using {@link TokenNameFinderEvaluator}.
 *
 * @author ragerri
 * @version 2014-04-04
 */
public class CorpusEvaluate {

  /**
   * The reference corpus to evaluate against.
   */
  private ObjectStream<NameSample> referenceSamples;
  /**
   * The reference corpus to evaluate against.
   */
  private ObjectStream<NameSample> predictionSamples;
  /**
   * The FMeasure implementation.
   */
  private FMeasure fmeasure = new FMeasure();
  /**
   * Construct an evaluator.
   *
   * @param predictionData the reference data to evaluate against
   * @param model the model to be evaluated
   * @param features the features
   * @param lang the language
   * @param beamsize the beam size for decoding
   * @param corpusFormat the format of the testData corpus
   * @throws IOException if input data not available
   */
  public CorpusEvaluate(final String referenceData, final String predictionData, final String lang,
      final String corpusFormat, String netypes) throws IOException {

    referenceSamples = AbstractNameFinderTrainer.getNameStream(referenceData, lang, corpusFormat);
    predictionSamples = AbstractNameFinderTrainer.getNameStream(predictionData, lang, corpusFormat);
    if (netypes != null) {
      String[] neTypes = netypes.split(",");
      referenceSamples = new NameSampleTypeFilter(neTypes, referenceSamples);
      predictionSamples = new NameSampleTypeFilter(neTypes, predictionSamples);
    }
  }
  
  public List<NameSample> readSamplesToList(ObjectStream<NameSample> samples) throws IOException {
    NameSample sample;
    List<NameSample> nameSampleList = new ArrayList<NameSample>();
    while ((sample = samples.read()) != null) {
      nameSampleList.add(sample);
    }
    return nameSampleList;
  }

  /**
   * Evaluate and print precision, recall and F measure.
   * @throws IOException if test corpus not loaded
   */
  public final void evaluate() throws IOException {
    List<NameSample> refList = readSamplesToList(referenceSamples);
    List<NameSample> predList = readSamplesToList(predictionSamples);
    for (int i = 0; i < refList.size(); ++i) {
      fmeasure.updateScores(refList.get(i).getNames(), predList.get(i).getNames());
    }
    System.out.println(fmeasure.toString());
  }
  

}
