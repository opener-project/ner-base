package opennlp.ixa.nerc.en;

import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

/**
 * Simple Named Entity Recognition module based on Apache OpenNLP.
 * 
 * English model trained by IXA NLP Group.
 * 
 * @author ragerri 2012/10/30
 * 
 */

public class NERC {

  private TokenNameFinderModel nercModel;
  private NameFinderME nercDetector;

  /**
   * It constructs an object NERC from the NERC class. First it loads a model,
   * then it initializes the nercModel and finally it creates a nercDetector
   * using such model.
   */
  public NERC() {

    InputStream trainedModel = getClass().getResourceAsStream("/en-500-0-testa-perceptron.bin");

    try {
      nercModel = new TokenNameFinderModel(trainedModel);

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (trainedModel != null) {
        try {
          trainedModel.close();
        } catch (IOException e) {
        }
      }
    }

    nercDetector = new NameFinderME(nercModel);
  }

  /**
   * This method receives as an input an array of Apache OpenNLP tokenized text
   * and calls the NameFinderME.find(tokens) to recognize and classify Named
   * Entities.
   * 
   * From Apache OpenNLP documentation: "After every document clearAdaptiveData
   * must be called to clear the adaptive data in the feature generators. Not
   * calling clearAdaptiveData can lead to a sharp drop in the detection rate
   * after a few documents."
   * 
   * @param tokens
   *          an array of tokenized text
   * @return an array of OpenNLP Spans of annotated text
   */
  public Span[] nercAnnotate(String[] tokens) {
    Span[] annotatedText = nercDetector.find(tokens);
    nercDetector.clearAdaptiveData();
    return annotatedText;

  }

}
