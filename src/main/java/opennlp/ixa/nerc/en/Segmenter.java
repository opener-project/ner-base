package opennlp.ixa.nerc.en;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    Path modelIn = Paths.get("models/en-sent.bin");

    FileInputStream trainedModel = null;
    try {
      trainedModel = new FileInputStream(modelIn.toString());
    } catch (FileNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    // System.out.format("Sentence Model used: %s%n",modelIn.toString());
    // System.out.println();

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
