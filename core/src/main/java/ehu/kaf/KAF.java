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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Text;

public class KAF {

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
   * 
   * Generates timestamp in UTC atomic format.
   * 
   * @return timestamp in UTC timezone.
   */
  public String getTimestamp() {
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD'T'kk:mm:ssZ");
    String formattedDate = sdf.format(date);
    return formattedDate;
  }

  /*
   * Simple classes representing KafHeader, <wf>, <terms> and <entities> for
   * creating a document in KAF format.
   */

  class LinguisticProcessor {
    public String layer;
    public List<Element> lpElems;
    public String name;
    public String timestamp;
    public String version;

  }

  class WordForm {
    public String id;
    public String form;
    public String offset;
    public String length;
    public String sent;
    public String para;
  }

  class Term {
    public String id;
    public ArrayList<String> tokens;
    public String pos;
    public String lemma;
    public String type;
    public String spanString;
    public String morphofeat;
  }

  class Entity {
    public String id;
    public String type;
    public ArrayList<String> terms;
    public String neString;
  }

  /*
   * Class members
   */

  private String lang;
  ArrayList<LinguisticProcessor> lps;
  ArrayList<WordForm> wfs;
  ArrayList<Term> terms;
  ArrayList<Entity> entities;

  /*
   * Constructor: it initializes the class' member arrays
   */
  public KAF(String cmdOption) {
    this.lang = cmdOption;
    this.lps = new ArrayList<LinguisticProcessor>();
    this.wfs = new ArrayList<WordForm>();
    this.terms = new ArrayList<Term>();
    this.entities = new ArrayList<Entity>();
  }

  /*
   * The following methods are for adding linguisticProcessor, <wf>, <terms> and
   * <entities> to their respective object classes.
   */

  public void addlps(String layer, String name, String timestamp, String version) {
    LinguisticProcessor lp = new LinguisticProcessor();
    lp.layer = layer;
    lp.name = name;
    lp.timestamp = timestamp;
    lp.version = version;
    this.lps.add(lp);
  }

  public void addWf(String id, String sent, String offset, String tokLength, String para, String form) {
    WordForm wf = new WordForm();
    wf.id = id;
    wf.sent = sent;
    wf.offset = offset;
    wf.length = tokLength;
    wf.para = para;
    wf.form = form;
    this.wfs.add(wf);
  }

  public void addTerm(String id, String pos, String type, String lemma,
      ArrayList<String> tokenIds, String spanString, String posTag) {
    Term term = new Term();
    term.id = id;
    term.pos = pos; // kaf postag
    term.lemma = lemma;
    term.type = type;
    term.tokens = tokenIds;
    term.spanString = spanString;
    term.morphofeat = posTag; // penn treebank postag
    this.terms.add(term);
  }

  public void addEntity(String id, String type, ArrayList<String> termIds,
      String neString) {
    Entity entity = new Entity();
    entity.id = id;
    entity.type = type;
    entity.terms = termIds;
    entity.neString = neString;
    entities.add(entity);
  }

  /*
   * The following methods return the number of <wf>, <terms> or <entities>
   * elements in their respective object classes.
   */

  /**
   * Return number of <wf> elements
   * 
   * @return listwfs.size()
   */
  public int getNumWfs() {
    return this.wfs.size();
  }

  /**
   * Return the number of term elements
   * 
   * @return termlist.size()
   */
  public int getNumTerms() {
    return this.terms.size();
  }

  /**
   * Return the number of entity elements
   * 
   * @return entitieslist.size()
   */
  public int getNumEntities() {
    return this.entities.size();
  }

  /**
   * Create KAF document containing KafHeader, text, terms and entities
   * elements.
   * 
   * @return Document kafDocument
   */
  public Document createKAFDoc() {
    Document doc = null;
    try {

      Element root = new Element("KAF");
      root.setAttribute("lang", this.lang, Namespace.XML_NAMESPACE);
      root.setAttribute("version", "v1.opener");

      Element header = new Element("kafHeader");
      for (LinguisticProcessor lingProc : this.lps) {
        Element lingProcElem = new Element("linguisticProcessors");
        lingProcElem.setAttribute("layer", lingProc.layer);
        Element lpElem = new Element("lp");
        lpElem.setAttribute("name", lingProc.name);
        lpElem.setAttribute("timestamp", lingProc.timestamp);
        lpElem.setAttribute("version", lingProc.version);
        lingProcElem.addContent(lpElem);
        header.addContent(lingProcElem);
      }
      root.addContent(header);

      Element wfList = new Element("text");
      for (WordForm wf : this.wfs) {
        Element wfElem = new Element("wf");
        wfElem.setAttribute("wid", wf.id);
        wfElem.setAttribute("sent", wf.sent);
        wfElem.setAttribute("offset", wf.offset);
        wfElem.setAttribute("length", wf.length);
        wfElem.setAttribute("para", wf.para);
        Text wfFormTxtNode = new Text(wf.form);
        wfElem.addContent(wfFormTxtNode);
        wfList.addContent(wfElem);
      }
      root.addContent(wfList);

      Element termList = new Element("terms");
      for (Term term : this.terms) {
        Element termElem = new Element("term");
        termElem.setAttribute("tid", term.id);
        termElem.setAttribute("pos", term.pos);
        termElem.setAttribute("morphofeat", term.morphofeat);
        termElem.setAttribute("lemma", term.lemma);
        termElem.setAttribute("type", term.type);
        Element spanElem = new Element("span");
        Comment spanComment = new Comment(term.spanString);
        spanElem.addContent(spanComment);
        for (String tokenId : term.tokens) {
          Element targetElem = new Element("target");
          targetElem.setAttribute("id", tokenId);
          spanElem.addContent(targetElem);
        }
        termElem.addContent(spanElem);
        termList.addContent(termElem);
      }
      root.addContent(termList);

      Element entityList = new Element("entities");
      for (Entity entity : this.entities) {
        Element entityElem = new Element("entity");
        entityElem.setAttribute("eid", entity.id);
        entityElem.setAttribute("type", entity.type);
        Element referencesElem = new Element("references");
        Element spanElem = new Element("span");
        Comment spanNEString = new Comment(entity.neString);
        spanElem.addContent(spanNEString);
        for (String termId : entity.terms) {
          Element targetElem = new Element("target");
          targetElem.setAttribute("id", termId);
          spanElem.addContent(targetElem);
        }
        entityElem.addContent(referencesElem);
        referencesElem.addContent(spanElem);
        entityList.addContent(entityElem);
      }
      root.addContent(entityList);
      doc = new Document(root);

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return doc;
  }

}
