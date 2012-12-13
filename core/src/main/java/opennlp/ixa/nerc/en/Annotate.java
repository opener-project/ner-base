/**
 * 
 */
package opennlp.ixa.nerc.en;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.Span;

/**
 * @author ragerri
 * 
 */
public class Annotate {

  static Segmenter sentDetector = new Segmenter();
  static TokTokenizer toker = new TokTokenizer();
  static NERC nameFinder = new NERC();

  /**
   * This method uses the Apache OpenNLP Sentence Detector, Tokenizer and
   * NameFinder to find and classify Named Entities in running text.
   * 
   * For every line of text the method receives, it creates an array of
   * segmented sentences, an array of Tokens from the segmented sentences and a
   * list of names which corresponds to the original string with NERC
   * annotations.
   * 
   * @param line
   *          a string
   * @return
   * @return a List of NameSample items, consisting of the annotated document
   * @throws IOException
   */

  public static List<NameSample> annotateNERC(String line) {

    List<NameSample> annText = new ArrayList<NameSample>();
    String sentences[] = sentDetector.segmentSentence(line);

    for (String sent : sentences) {
      String tokens[] = toker.toker(sent);
      Span nameSpans[] = nameFinder.nercAnnotate(tokens);
      Span reducedSpans[] = NameFinderME.dropOverlappingSpans(nameSpans);
      NameSample nameSample = new NameSample(tokens, reducedSpans, true);
      annText.add(nameSample);
    }
    return annText;
  }

  /**
   * This method uses the Apache OpenNLP Sentence Detector, Tokenizer and
   * NameFinder to find and classify Named Entities in running text.
   * 
   * For every line of text the method receives, it creates an array of
   * segmented sentences, an array of Tokens from the segmented sentences and a
   * list of names which corresponds to the original string with NERC
   * annotations.
   * 
   * From that information, it fills the kaf object received with all the data
   * it needs: tokens, terms and entities.
   * 
   * @param line
   *          of string
   * @param KAF
   *          object. This object is used to take the output data and convert it
   *          to KAF, returning an XML document in a string.
   */
  public static void annotateNERC(String line, KAF kaf) throws IOException {
    int i, j;
    String sentences[] = sentDetector.segmentSentence(line);

    for (String sent : sentences) {
      String tokens[] = toker.toker(sent);
      Span nameSpans[] = nameFinder.nercAnnotate(tokens);
      Span reducedSpans[] = NameFinderME.dropOverlappingSpans(nameSpans);

      // Add tokens in the sentence to kaf object
      int numTokensInKaf = kaf.getNumTokens();
      int nextTokenInd = numTokensInKaf + 1;
      for (i = 0; i < tokens.length; i++) {
        String id = "w" + Integer.toString(nextTokenInd++);
        String tokenStr = tokens[i];
        kaf.addToken(id, tokenStr);
      }

      // Add terms and entities to kaf object
      int numTermsInKaf = kaf.getNumTerms();
      int nextTermInd = numTermsInKaf + 1;
      int numEntitiesInKaf = kaf.getNumEntities();
      // int nextEntityInd = numEntitiesInKaf+1;
      for (i = 0; i < reducedSpans.length; i++) {
        ArrayList<String> namesTerms = new ArrayList<String>();
        // Term
        for (j = reducedSpans[i].getStart(); j < reducedSpans[i].getEnd(); j++) {
          String termId = "t" + Integer.toString(nextTermInd++);
          ArrayList<String> tokenIds = new ArrayList<String>();
          tokenIds.add("w" + Integer.toString(numTokensInKaf + 1 + j));
          kaf.addTerm(termId, tokenIds);
          // Collect all terms in a list. We can add them to the
          // entity's term list later.
          namesTerms.add(termId);
        }
        // Entity
        String entityId = "e" + Integer.toString(numEntitiesInKaf + 1 + i);
        String type = reducedSpans[i].getType();
        kaf.addEntity(entityId, type, namesTerms);
      }
    }

  }

  /**
   * 
   * This method creates a BufferedReader to store the text of an input file and
   * a BufferedWriter to write it to an output file. The method calls to
   * CLI.annotatedNERC(string) method to obtain the NameSample List which is
   * written via the NameSample.toString().
   * 
   * @param infile
   *          Path from which to open a file into a BufferedReader
   * @param outfile
   *          Path to which save a file from a BufferedWriter
   * @throws IOException
   */

  public static void nercFiles(Path infile, Path outfile) throws IOException {

    Charset charset = Charset.forName("UTF-8");
    BufferedReader breader = Files.newBufferedReader(infile, charset);
    BufferedWriter bwriter = Files.newBufferedWriter(outfile, charset); // save
    String line = null;
    while ((line = breader.readLine()) != null) {
      List<NameSample> names = Annotate.annotateNERC(line);
      for (NameSample name : names) {
        bwriter.write(name.toString());// printing every name (sentence
        // segmented annotated text) to
        // a file
      }
    }
    bwriter.close();
  }

  public static void nerc2kaf(Path infile, Path outfile) throws IOException {

    Charset charset = Charset.forName("UTF-8");
    BufferedReader breader = Files.newBufferedReader(infile, charset);
    BufferedWriter bwriter = Files.newBufferedWriter(outfile, charset); // save
    String line = null;
    KAF kaf = new KAF();
    while ((line = breader.readLine()) != null) {
      Annotate.annotateNERC(line, kaf);
    }
    bwriter.write(kaf.toString());
    bwriter.close();
  }

}
