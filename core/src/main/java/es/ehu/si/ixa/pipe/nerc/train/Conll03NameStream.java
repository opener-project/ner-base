package es.ehu.si.ixa.pipe.nerc.train;

/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

import es.ehu.si.ixa.pipe.nerc.train.Conll02NameStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.StringUtil;

/**
 * An import stream which can parse the CONLL03 data.
 */
public class Conll03NameStream implements ObjectStream<NameSample>{

  
  private final String lang;
  private final ObjectStream<String> lineStream;

  /**
   *
   * @param lang
   * @param lineStream
   * @param types
   */
  public Conll03NameStream(String lang, ObjectStream<String> lineStream) {
    this.lang = lang;
    this.lineStream = lineStream;
  }

  /**
   *
   * @param lang
   * @param in
   * @param types
   */
  public Conll03NameStream(String lang, InputStream in) {

    this.lang = lang;
    try {
      this.lineStream = new PlainTextByLineStream(in, "UTF-8");
      System.setOut(new PrintStream(System.out, true, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      // UTF-8 is available on all JVMs, will never happen
      throw new IllegalStateException(e);
    }
  }

  public NameSample read() throws IOException {

    List<String> sentence = new ArrayList<String>();
    List<String> tags = new ArrayList<String>();

    boolean isClearAdaptiveData = false;

    // Empty line indicates end of sentence

    String line;
    while ((line = lineStream.read()) != null && !StringUtil.isEmpty(line)) {

      if (line.startsWith(Conll02NameStream.DOCSTART)) {
        isClearAdaptiveData = true;
        String emptyLine = lineStream.read();
        
        if (!StringUtil.isEmpty(emptyLine))
          throw new IOException("Empty line after -DOCSTART- not empty: '" + emptyLine +"'!");
        
        continue;
      }

      String fields[] = line.split(" ");
      /*for (String field : fields) {
        System.err.println(field);
      }*/

      // For English: WORD  POS-TAG SC-TAG NE-TAG
      if ((lang.equalsIgnoreCase("en") || lang.equalsIgnoreCase("it"))  && (fields.length == 4)) {
        sentence.add(fields[0]);
        tags.add(fields[3]); // 3 is NE-TAG
      }
      // For German: WORD  LEMA-TAG POS-TAG SC-TAG NE-TAG
      else if (lang.equalsIgnoreCase("de") && (fields.length == 5)) {
        sentence.add(fields[0]);
        tags.add(fields[4]); // 4 is NE-TAG
      }
      else {
        throw new IOException("Incorrect number of fields per line for language: '" + line + "'!");
      }
    }

    if (sentence.size() > 0) {

      // convert name tags into spans
      List<Span> names = new ArrayList<Span>();

      int beginIndex = -1;
      int endIndex = -1;
      for (int i = 0; i < tags.size(); i++) {

        String tag = tags.get(i);

        if (tag.equals("O")) {
          // O means we don't have anything this round.
          if (beginIndex != -1) {
            names.add(Conll02NameStream.extract(beginIndex, endIndex, tags.get(beginIndex)));
            beginIndex = -1;
            endIndex = -1;
          }
        }
        else if (tag.startsWith("B-")) {
          // B- prefix means we have two same entities next to each other
          if (beginIndex != -1) {
            names.add(Conll02NameStream.extract(beginIndex, endIndex, tags.get(beginIndex)));
          }
          beginIndex = i;
          endIndex = i + 1;
        }
        else if (tag.startsWith("I-")) {
          // I- starts or continues a current name entity
          if (beginIndex == -1) {
            beginIndex = i;
            endIndex = i + 1;
          }
          else if (!tag.endsWith(tags.get(beginIndex).substring(1))) {
            // we have a new tag type following a tagged word series
            // also may not have the same I- starting the previous!
            names.add(Conll02NameStream.extract(beginIndex, endIndex, tags.get(beginIndex)));
            beginIndex = i;
            endIndex = i + 1;
          }
          else {
            endIndex ++;
          }
        }
        else {
          throw new IOException("Invalid tag: " + tag);
        }
      }

      // if one span remains, create it here
      if (beginIndex != -1)
        names.add(Conll02NameStream.extract(beginIndex, endIndex, tags.get(beginIndex)));
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

