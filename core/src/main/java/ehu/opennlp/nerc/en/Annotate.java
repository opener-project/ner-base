package ehu.opennlp.nerc.en;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;

import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.Element;

/**
 * @author ragerri
 * 
 */
public class Annotate {

  private NERC nameFinder;

  public Annotate(String cmdOption) {
    Models modelRetriever = new Models();
    InputStream nerModel = modelRetriever.getNERModel(cmdOption);
    nameFinder = new NERC(nerModel);
  }

  /**
   * It reads the linguisticProcessor elements and adds them to the KAF
   * document.
   * 
   * @param lingProc
   * @param kaf
   */
  public void addKafHeader(List<Element> lingProc, KAF kaf) {
    String layer = null;
    for (int i = 0; i < lingProc.size(); i++) {
      layer = lingProc.get(i).getAttributeValue("layer");
      List<Element> lps = lingProc.get(i).getChildren("lp");
      for (Element lp : lps) {
        kaf.addlps(layer, lp.getAttributeValue("name"),
            lp.getAttributeValue("timestamp"), lp.getAttributeValue("version"));
      }
    }
  }

  /**
   * From a termSpan Element in KAF returns the comment inside the Span Element.
   * This function is used to add the string corresponding to a term span as a
   * commment in the Span element of the <terms> layer.
   * 
   * @param Element
   *          termSpan
   * @return Comment spanComment
   */
  private Comment getTermSpanComment(Element termSpan) {
    Comment spanComment = null;
    List<Content> spanContent = termSpan.getContent();
    for (Object elem : spanContent) {
      if (elem instanceof Comment) {
        spanComment = (Comment) elem;
      }
    }
    return spanComment;
  }

  /**
   * 
   * It takes a NE span indexes and the tokens in a sentence and produces the
   * string to which the NE span corresponds to. This function is used to get
   * the NE textual representation from a Span. The NE string will then be added
   * to the span element in the <entities> layer of the KAF document.
   * 
   * @param Span
   *          reducedSpan
   * @param String
   *          [] tokens
   * @return named entity string
   */
  private String getStringFromSpan(Span reducedSpan, String[] tokens) {
    StringBuilder sb = new StringBuilder();
    for (int si = reducedSpan.getStart(); si < reducedSpan.getEnd(); si++) {
      sb.append(tokens[si]).append(" ");
    }
    String neString = sb.toString();
    return neString;
  }

  /**
   * This method uses the Apache OpenNLP to tag Named Entities.
   * 
   * It gets a Map<SentenceId, tokens> from the input KAF document and iterates
   * over the tokens of each sentence to annotate Named Entities.
   * 
   * It also reads <wf>, <terms> elements from the input KAF document and fills
   * the KAF object with those elements plus the annotated Named Entities.
   * 
   * @param LinkedHashMap
   *          <String,List<String>
   * @param List
   *          <Element> termList
   * @param KAF
   *          object. This object is used to take the output data and convert it
   *          to KAF.
   * 
   * @return JDOM KAF document containing <wf>,<terms> and <entities> elements.
   */

  public void annotateNEsToKAF(
      LinkedHashMap<String, List<String>> sentTokensMap,
      List<Element> termList, KAF kaf) throws IOException {

    for (Map.Entry<String, List<String>> sentence : sentTokensMap.entrySet()) {
      String sid = sentence.getKey();
      String[] tokens = sentence.getValue().toArray(
          new String[sentence.getValue().size()]);

      Span nameSpans[] = nameFinder.nercAnnotate(tokens);
      Span reducedSpans[] = NameFinderME.dropOverlappingSpans(nameSpans);

      // Add tokens in the sentence to kaf object
      int numTokensInKaf = kaf.getNumWfs();
      int nextTokenInd = numTokensInKaf + 1;
      for (int i = 0; i < tokens.length; i++) {
        String id = "w" + Integer.toString(nextTokenInd++);
        String tokenStr = tokens[i];
        kaf.addWf(id, sid, tokenStr);
      }

      // Read, link with tokens and add unmodified terms to KAF object
      int noTerms = kaf.getNumTerms();
      for (int v = 0; v < tokens.length; v++) {
        int realTermCounter = v + noTerms;
        // get termId ArrayList from index of token in current sentence + number
        // of Terms in KAF so far
        String termId = termList.get(realTermCounter).getAttributeValue("tid");

        // get span from term corresponding to token index in current sentence
        Element spanElem = termList.get(realTermCounter).getChild("span");
        Element targetElem = spanElem.getChild("target");
        // get comment in Span Element
        Comment spanComment = getTermSpanComment(spanElem);

        // get wIds of target element from index of token in current sentence +
        // number of wfs in KAF so far
        String wordId = targetElem.getAttributeValue("id");
        ArrayList<String> tokenIds = new ArrayList<String>();
        tokenIds.add(wordId);

        // get posId, lemma and type from term corresponding to token index in
        // current sentence
        String posId = termList.get(realTermCounter).getAttributeValue("pos");
        String termLemma = termList.get(realTermCounter).getAttributeValue(
            "lemma");
        String termType = termList.get(realTermCounter).getAttributeValue(
            "type");
        String morphFeatValue = termList.get(realTermCounter).getAttributeValue(
            "morphofeat");
        String morphFeat;
        if (morphFeatValue == null) { 
          morphFeat = ""; 
        }
        else {
          morphFeat = morphFeatValue;
        }
        kaf.addTerm(termId, posId, termType, termLemma, tokenIds,
            spanComment.getValue(), morphFeat);
      }

      // loop over the span of the NE
      int noEntities = kaf.getNumEntities();
      for (int j = 0; j < reducedSpans.length; j++) {
        ArrayList<String> neTerms = new ArrayList<String>();
        // loop to obtain indexes of NEs in reducedSpans for each sentence
        for (int k = reducedSpans[j].getStart(); k < reducedSpans[j].getEnd(); k++) {
          // We get the offset by summing up the total number of terms up until
          // now + their index in the sentence
          String termsId = "t" + (noTerms + (k + 1));
          neTerms.add(termsId);
        }
        String neId = "e" + Integer.toString(noEntities + 1 + j);
        String type = reducedSpans[j].getType();
        String neString = getStringFromSpan(reducedSpans[j], tokens);
        kaf.addEntity(neId, type, neTerms, neString);
      }
    }
  }

}
