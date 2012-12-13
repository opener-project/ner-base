package opennlp.ixa.nerc.en;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public class KAF {

  /*
   * Simple classes representing tokens, terms and entities for creating a
   * document in KAF format.
   */

  class Token {
    public String id;
    public String form;
  }

  class Term {
    public String id;
    public ArrayList<String> tokens;
  }

  class Entity {
    public String id;
    public String type;
    public ArrayList<String> terms;
  }

  /*
   * Class members
   */

  ArrayList<Token> tokens;
  ArrayList<Term> terms;
  ArrayList<Entity> entities;

  /*
   * Constructor: it initializes the class' member arrays
   */
  public KAF() {
    this.tokens = new ArrayList<Token>();
    this.terms = new ArrayList<Term>();
    this.entities = new ArrayList<Entity>();
  }

  /*
   * The following methods are for adding tokens, terms and entities to the
   * object.
   */

  public void addToken(String id, String form) {
    Token token = new Token();
    token.id = id;
    token.form = form;
    this.tokens.add(token);
  }

  public void addTerm(String id, ArrayList<String> tokenIds) {
    Term term = new Term();
    term.id = id;
    term.tokens = tokenIds;
    this.terms.add(term);
  }

  public void addEntity(String id, String type, ArrayList<String> termIds) {
    Entity entity = new Entity();
    entity.id = id;
    entity.type = type;
    entity.terms = termIds;
    entities.add(entity);
  }

  /*
   * The following methods return the number of tokens, terms or entities in the
   * object.
   */

  public int getNumTokens() {
    return this.tokens.size();
  }

  public int getNumTerms() {
    return this.terms.size();
  }

  public int getNumEntities() {
    return this.entities.size();
  }

  /*
   * This method returns all the data the object contains in KAF format as a
   * string.
   */
  public String toString() {
    String kafXml = "";
    try {
      DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dbFac.newDocumentBuilder();
      Document doc = docBuilder.newDocument();

      Element root = doc.createElement("KAF");
      root.setAttribute("xml:lang", "en");
      doc.appendChild(root);

      Element wfList = doc.createElement("text");
      for (Token token : this.tokens) {
        Element tokenElem = doc.createElement("wf");
        tokenElem.setAttribute("wid", token.id);
        Text tokenFormTxtNode = doc.createTextNode(token.form);
        tokenElem.appendChild(tokenFormTxtNode);
        wfList.appendChild(tokenElem);
      }
      root.appendChild(wfList);

      Element termList = doc.createElement("terms");
      for (Term term : this.terms) {
        Element termElem = doc.createElement("term");
        termElem.setAttribute("tid", term.id);
        Element spanElem = doc.createElement("span");
        for (String tokenId : term.tokens) {
          Element targetElem = doc.createElement("target");
          targetElem.setAttribute("id", tokenId);
          spanElem.appendChild(targetElem);
        }
        termElem.appendChild(spanElem);
        termList.appendChild(termElem);
      }
      root.appendChild(termList);

      Element entityList = doc.createElement("entities");
      for (Entity entity : this.entities) {
        Element entityElem = doc.createElement("entity");
        entityElem.setAttribute("eid", entity.id);
        entityElem.setAttribute("type", entity.type);
        Element referencesElem = doc.createElement("references");
        Element spanElem = doc.createElement("span");
        for (String termId : entity.terms) {
          Element targetElem = doc.createElement("target");
          targetElem.setAttribute("id", termId);
          spanElem.appendChild(targetElem);
        }
        entityElem.appendChild(referencesElem);
        referencesElem.appendChild(spanElem);
        entityList.appendChild(entityElem);
      }
      root.appendChild(entityList);

      ByteArrayOutputStream os = new ByteArrayOutputStream();
      DOMImplementationLS domImplLS = (DOMImplementationLS) doc
          .getImplementation();
      LSOutput lsOutput = domImplLS.createLSOutput();
      lsOutput.setByteStream(os);
      LSSerializer serializer = domImplLS.createLSSerializer();
      serializer.getDomConfig().setParameter("format-pretty-print", true);
      serializer.write(doc, lsOutput);
      kafXml = new String(os.toByteArray(), "UTF-8");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return kafXml;
  }

}
