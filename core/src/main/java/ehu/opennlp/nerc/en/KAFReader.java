package ehu.opennlp.nerc.en;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * 
 * KAF Reader using JDOM2. It reads KAFHeader, text, and terms elements.
 * 
 * @author ragerri
 * 
 */
public class KAFReader {
  private SAXBuilder builder;

  public KAFReader() {
    builder = new SAXBuilder();

  }

  /**
   * Read XML document using JDOM2 SAXBuilder and outputs the rootNode of the
   * XML document.
   * 
   * @param InputStream in
   * @return Element rootNode of the XML Document
   * @throws JDOMException
   * @throws IOException
   */
  public Element getRootNode(InputStream in) throws JDOMException, IOException {
    Document document = builder.build(in);
    Element rootNode = document.getRootElement();
    return rootNode;
  }

  /**
   * It reads the KAFHeader and its linguisticProcessors children.
   * 
   * @param Element
   *          rootNode
   * @return List<Element> linguisticProcessors
   */
  public List<Element> getKafHeader(Element rootNode) {
    Element kafHeaderElem = rootNode.getChild("kafHeader");
    List<Element> lpElem = kafHeaderElem.getChildren("linguisticProcessors");
    return lpElem;

  }

  /**
   * From the KAF rootNode it produces the list of <wf> elements
   * 
   * @param Element
   *          rootNode
   * @return List<Element> wfs
   * @throws JDOMException
   * @throws IOException
   */
  public List<Element> getWfs(Element rootNode) throws JDOMException,
      IOException {
    Element textNode = rootNode.getChild("text");
    List<Element> wfs = textNode.getChildren("wf");
    return wfs;
  }

  /**
   * From the KAF rootNode it produces the list of <term> elements (every
   * children of <terms>)
   * 
   * @param Element
   *          rootNode
   * @return List<Element> termList
   */
  public List<Element> getTerms(Element rootNode) {
    Element termsNode = rootNode.getChild("terms");
    List<Element> termList = termsNode.getChildren("term");
    return termList;
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

}