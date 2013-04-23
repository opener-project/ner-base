/*
 * Copyright 2013 Rodrigo Agerri

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package ehu.kaf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import opennlp.tools.util.Span;

import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.JDOMException;

public class KAFUtils {
  
  public String getWfOffset(List<Element> wfs, int origWfCounter) {
    String offset=null;
    if (wfs.get(origWfCounter).getAttributeValue("offset") == null) { 
      offset = "";
    }
    else {
      offset = wfs.get(origWfCounter).getAttributeValue("offset");
    }
    return offset;
  }
  
  public String getWfLength(List<Element> wfs, int origWfCounter) {
    String tokLength=null;
    if (wfs.get(origWfCounter).getAttributeValue("length") == null) { 
      tokLength = "";
    }
    else {
      tokLength = wfs.get(origWfCounter).getAttributeValue("length");
    }
    return tokLength;
  }
  
  public String getWfPara(List<Element> wfs, int origWfCounter) {
    String para=null;
    if (wfs.get(origWfCounter).getAttributeValue("para") == null) { 
      para = "";
    }
    else {
      para = wfs.get(origWfCounter).getAttributeValue("para");
    }
    return para;
  }
  
  public String getWfXpath(List<Element> wfs, int origWfCounter) {
    String page=null;
    if (wfs.get(origWfCounter).getAttributeValue("page") == null) { 
      page = "";
    }
    else {
      page = wfs.get(origWfCounter).getAttributeValue("page");
    }
    return page;
  }
  
  public String getWfPage(List<Element> wfs, int origWfCounter) {
    String xpath=null;
    if (wfs.get(origWfCounter).getAttributeValue("xpath") == null) { 
      xpath = "";
    }
    else {
      xpath = wfs.get(origWfCounter).getAttributeValue("xpath");
    }
    return xpath;
  }
  
  /**
   * From the list of <wf> elements, get the sentence Ids in a SortedSet.
   * 
   * @param List
   *          <Element> wfs
   * @return SortedSet sentIds
   */
  
  public SortedSet<Integer> getNumSents(List<Element> wfs) {
    SortedSet<Integer> sentIds = new TreeSet<Integer>();
    for (int i = 0; i < wfs.size(); i++) {
      sentIds.add(Integer.parseInt(wfs.get(i).getAttributeValue("sent")));
    }
    return sentIds;
  }

  /**
   * From the list of <wf> elements, get Map <sentId,tokens>. The tokens
   * ArrayList is empty and should be populated by the
   * getSentsFromWfs(LinkedHashMap, wfs) function.
   * 
   * @param List
   *          <Element> wfs
   * @return LinkedHashMap<String,List<String>> sentId, tokens of each sentence
   */
  public LinkedHashMap<String, List<String>> getSentencesMap(List<Element> wfs) {
    LinkedHashMap<String, List<String>> sentTokensMap = new LinkedHashMap<String, List<String>>();
    SortedSet<Integer> sentIds = getNumSents(wfs);
    int sentId = 1;
    for (int i = 0; i < sentIds.size(); i++) {
      sentTokensMap.put(Integer.toString(sentId), new ArrayList<String>());
      sentId++;
    }
    return sentTokensMap;

  }

  /**
   * 
   * Gets a Map <sentId,tokens> and the list of <wf> elements and populates the
   * Map<sentId,tokens> with the sentenceId and the tokens for each sentence.
   * The output of this function is used to annotate NEs per tokenized sentence.
   * 
   * @param LinkedHashMap
   *          <String,List<String>> sentTokensMap
   * @param List
   *          <Element> wfs
   * @return LinkedHashMap<String,List<String>> sentTokensMap populated with
   *         sentIds and tokens per sentence
   * @throws JDOMException
   * @throws IOException
   */
  public LinkedHashMap<String, List<String>> getSentsFromWfs(
      LinkedHashMap<String, List<String>> sentTokensMap, List<Element> wfs)
      throws JDOMException, IOException {

    List<String> tokens = new ArrayList<String>();
    for (int j = 0; j < wfs.size(); j++) {
      tokens = sentTokensMap.get(wfs.get(j).getAttributeValue("sent"));
      tokens.add(wfs.get(j).getText());
    }
    return sentTokensMap;
  }

  /**
   * Set the term type attribute based on the pos value
   * 
   * @param kaf postag
   * @return type
   */
  public String setTermType(String postag) {
    if (postag.startsWith("N") || postag.startsWith("V")
        || postag.startsWith("G") || postag.startsWith("A")) {
      return "open";
    } else {
      return "close";
    }
  }
  
  public String getTermMorphofeat(List<Element> termList, int realTermCounter) { 
	  String morphFeat = null;
	  if (termList.get(realTermCounter).getAttributeValue("morphofeat") == null) { 
	      morphFeat = "";
	    }
	    else {
	      morphFeat = termList.get(realTermCounter).getAttributeValue("morphofeat");
	    }
	    return morphFeat;
  }

  public String getTermType(List<Element> termList, int realTermCounter) { 
	  String termType = null;
	  if (termList.get(realTermCounter).getAttributeValue("type") == null) { 
	      termType = "";
	    }
	    else {
	      termType = termList.get(realTermCounter).getAttributeValue("type");
	    }
	    return termType;
  }
  
  public String getTermLemma(List<Element> termList, int realTermCounter) { 
	  String lemma = null;
	  if (termList.get(realTermCounter).getAttributeValue("lemma") == null) { 
	      lemma = "";
	    }
	    else {
	      lemma = termList.get(realTermCounter).getAttributeValue("lemma");
	    }
	    return lemma;
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
  public Comment getTermSpanComment(Element termSpan) {
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
  public String getStringFromSpan(Span reducedSpan, String[] tokens) {
    StringBuilder sb = new StringBuilder();
    for (int si = reducedSpan.getStart(); si < reducedSpan.getEnd(); si++) {
      sb.append(tokens[si]).append(" ");
    }
    String neString = sb.toString();
    return neString;
  }
  

}
