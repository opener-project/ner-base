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
import java.io.InputStream;
import java.util.List;

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
   * @param InputStream
   *          in
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
   * child of <terms>)
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

  
}