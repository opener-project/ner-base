package ehu.opennlp.nerc.en;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedHashMap;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * EHU-OpenNLP NERC using Apache OpenNLP.
 * 
 * @author ragerri
 * @version 1.0
 * 
 */

public class CLI {

  /**
   * 
   * BufferedReader (from standard input) and BufferedWriter are opened. The module 
   * takes KAF and reads the header, the text, terms elements and uses Annotate class to 
   * annotate Named Entities and to add the entities element to the KAF read from standard
   * input. Finally, the modified KAF document is passed via standard output. 
   * 
   * @param args
   * @throws IOException
   * @throws JDOMException
   */
  public static void main(String[] args) throws IOException, JDOMException {

    KAFReader kafReader = new KAFReader();
    Annotate annotator = new Annotate();
    StringBuilder sb = new StringBuilder();
    BufferedReader breader = null;
    BufferedWriter bwriter = null;
    KAF kaf = new KAF();
    try {
      breader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
      bwriter = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
      String line;
      while ((line = breader.readLine()) != null) {
        sb.append(line);
      }

      // read KAF
      InputStream kafIn = new ByteArrayInputStream(sb.toString().getBytes(
          "UTF-8"));
      Element rootNode = kafReader.getRootNode(kafIn);
      List<Element> lingProc = kafReader.getKafHeader(rootNode);
      List<Element> wfs = kafReader.getWfs(rootNode);
      List<Element> termList = kafReader.getTerms(rootNode);
      LinkedHashMap<String, List<String>> sentencesMap = kafReader
          .getSentencesMap(wfs);
      LinkedHashMap<String, List<String>> sentences = kafReader
          .getSentsFromWfs(sentencesMap, wfs);

      // add already contained header plus this module linguistic
      // processor
      annotator.addKafHeader(lingProc, kaf);
      kaf.addlps("entities", "ehu-opennlp-nerc-en", kaf.getTimestamp(), "1.0");

      // annotate NEs to KAF
      annotator.annotateNEsToKAF(sentences, termList, kaf);

      XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
      xout.output(kaf.createKAFDoc(), bwriter);
      bwriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
