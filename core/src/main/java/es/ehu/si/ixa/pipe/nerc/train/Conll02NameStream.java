package es.ehu.si.ixa.pipe.nerc.train;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.StringUtil;

/**
 * Parser for the dutch and spanish ner training files of the CONLL 2002 shared task.
 * <p>
 * The dutch data has a -DOCSTART- tag to mark article boundaries,
 * adaptive data in the feature generators will be cleared before every article.<br>
 * The spanish data does not contain article boundaries,
 * adaptive data will be cleared for every sentence.
 * <p>
 * The data contains four named entity types: Person, Organization, Location and Misc.<br>
 * <p>
 * Data can be found on this web site:<br>
 * http://www.cnts.ua.ac.be/conll2002/ner/
 * <p>
 * <b>Note:</b> Do not use this class, internal use only!
 */
public class Conll02NameStream implements ObjectStream<NameSample>{
  
  public static final String DOCSTART = "-DOCSTART-";
	
  private final String lang;
  private final ObjectStream<String> lineStream;
 
  
  public Conll02NameStream(String lang, ObjectStream<String> lineStream) {
    this.lang = lang;
    this.lineStream = lineStream;
  }
  
  /**
   * @param lang
   * @param in an Input Stream to read data.
   * @throws IOException 
   */
  public Conll02NameStream(String lang, InputStream in) {
    
    this.lang = lang;
    try {
      this.lineStream = new PlainTextByLineStream(in, "UTF-8");
      System.setOut(new PrintStream(System.out, true, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      // UTF-8 is available on all JVMs, will never happen
      throw new IllegalStateException(e);
    }
  }
  
  static final Span extract(int begin, int end, String beginTag) throws InvalidFormatException {
    
    String type = beginTag.substring(2);
    return new Span(begin, end, type);
  }

  
  public NameSample read() throws IOException {

    List<String> sentence = new ArrayList<String>();
    List<String> tags = new ArrayList<String>();
    
    boolean isClearAdaptiveData = false;
    
    // Empty line indicates end of sentence
    
    String line;
    while ((line = lineStream.read()) != null && !StringUtil.isEmpty(line)) {
      
      if (lang.equalsIgnoreCase("nl") && line.startsWith(DOCSTART)) {
        isClearAdaptiveData = true;
        continue;
      }
      
      String fields[] = line.split(" ");
      
      if (fields.length == 3) {
        sentence.add(fields[0]);
        tags.add(fields[2]);
      }
      else {
        throw new IOException("Expected three fields per line in training data, got " +
            fields.length + " for line '" + line + "'!");
      }
    }
    
    // Always clear adaptive data for spanish
    if (lang.equalsIgnoreCase("es"))
      isClearAdaptiveData = true;
    
    if (sentence.size() > 0) {
      
      // convert name tags into spans
      List<Span> names = new ArrayList<Span>();
      
      int beginIndex = -1;
      int endIndex = -1;
      for (int i = 0; i < tags.size(); i++) {
        
        String tag = tags.get(i);
        
        if (tag.startsWith("B-")) {
          
          if (beginIndex != -1) {
            names.add(extract(beginIndex, endIndex, tags.get(beginIndex)));
            beginIndex = -1;
            endIndex = -1;
          }
          
          beginIndex = i;
          endIndex = i +1;
        }
        else if (tag.startsWith("I-")) {
          endIndex++;
        }
        else if (tag.equals("O")) {
          if (beginIndex != -1) {
            names.add(extract(beginIndex, endIndex, tags.get(beginIndex)));
            beginIndex = -1;
            endIndex = -1;
          }
        }
        else {
          throw new IOException("Invalid tag: " + tag);
        }
      }
      
      // if one span remains, create it here
      if (beginIndex != -1)
        names.add(extract(beginIndex, endIndex, tags.get(beginIndex)));
      
      return new NameSample(sentence.toArray(new String[sentence.size()]), names.toArray(new Span[names.size()]), isClearAdaptiveData);
    }
    else if (line != null) {
      // Just filter out empty events, if two lines in a row are empty
      return read();
    }
    else {
      // source stream is not returning anymore lines
      return null;
    }
  }

  public void reset() throws IOException, UnsupportedOperationException {
    lineStream.reset();
  }

  public void close() throws IOException {
    lineStream.close();
  }
}

