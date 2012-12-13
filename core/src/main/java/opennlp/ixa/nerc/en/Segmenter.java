package opennlp.ixa.nerc.en;

import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

/**
 * English Sentence Segmentation using Apache OpenNLP Model provided by
 * http://opennlp.sourceforge.net/models-1.5/
 * 
 * @author ragerri
 * 
 */
public class Segmenter {

  private SentenceModel segModel;
  private SentenceDetector sentDetector;

  /**
   * The Segmenter constructor loads an Apache OpenNLP sentence segmentation
   * model, it initializes and sentenceDetector using such a model.
   */
  public Segmenter() {

   InputStream trainedModel = getClass().getResourceAsStream("/en-sent.bin");
   
    try {
      segModel = new SentenceModel(trainedModel);

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

    sentDetector = new SentenceDetectorME(segModel);

  }

  /**
   * @param line
   *          a string
   * @return an array of segmented sentences
   */
  public String[] segmentSentence(String line) {
    String sentences[] = sentDetector.sentDetect(line);
    return sentences;

  }

}
