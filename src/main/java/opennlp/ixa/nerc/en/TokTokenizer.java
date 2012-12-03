package opennlp.ixa.nerc.en;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * A simple tokenizer based on Apache OpenNLP.
 * 
 * Model provided by http://opennlp.sourceforge.net/models-1.5/
 * 
 * @author ragerri
 * 
 */
public class TokTokenizer {
  private TokenizerModel tokModel;
  private TokenizerME tokDetector;

  /**
   * This constructor loads a tokenization model, it initializes and creates a
   * tokDetector using such a model.
   */
  public TokTokenizer() {

    Path modelIn = Paths.get("models/en-token.bin");

    FileInputStream trainedModel = null;
    try {
      trainedModel = new FileInputStream(modelIn.toString());
    } catch (FileNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    // System.out.format("Tokenizer Model used: %s%n",modelIn.toString());
    // System.out.println();

    try {
      tokModel = new TokenizerModel(trainedModel);

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

    tokDetector = new TokenizerME(tokModel);

  }

  /**
   * @param sentence
   * @return an array of tokenized tokens
   */
  public String[] toker(String sentence) {
    String tokens[] = tokDetector.tokenize(sentence);
    return tokens;

  }
}
