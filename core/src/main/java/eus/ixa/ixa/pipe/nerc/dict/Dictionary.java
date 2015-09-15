/*
 *  Copyright 2015 Rodrigo Agerri

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
package eus.ixa.ixa.pipe.nerc.dict;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import opennlp.tools.namefind.BilouCodec;
import opennlp.tools.namefind.BioCodec;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.SerializableArtifact;

/**
 * Dictionary class which creates a HashMap String, String from 
 * a tab separated file name\tclass\t.
 * 
 * @author ragerri
 * @version 2015-03-30
 * 
 */
public class Dictionary implements SerializableArtifact {
  
  private static final Pattern tabPattern = Pattern.compile("\t");

  public static class DictionarySerializer implements ArtifactSerializer<Dictionary> {

    public Dictionary create(InputStream in) throws IOException,
        InvalidFormatException {
      return new Dictionary(in);
    }

    public void serialize(Dictionary artifact, OutputStream out)
        throws IOException {
      artifact.serialize(out);
    }
  }
  
  private Map<String, String> dictionary = new HashMap<String, String>();

  public Dictionary(InputStream in) throws IOException {

    BufferedReader breader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
    String line;
    while ((line = breader.readLine()) != null) {
      String[] lineArray = tabPattern.split(line);
      if (lineArray.length == 2) {
        dictionary.put(lineArray[0].toLowerCase(), lineArray[1]);
      } else {
        System.err.println(lineArray[0] + " is not well formed!");
      }
    }
  }

  /**
   * Look up a string in the dictionary.
   * @param string the string to be searched
   * @return the string found
   */
  public String lookup(String string) {
    return dictionary.get(string);
  }
  
  /**
   * Get the key,value size of the dictionary.
   * @return maximum token count in the dictionary
   */
  public int getMaxTokenCount() {
    return dictionary.size();
  }
  
  /**
   * Get the Map String, String dictionary.
   * @return the dictionary map
   */
  public final Map<String, String> getDict() {
    return dictionary;
  }
  
  /**
   * Performs gazetteer match in a bio encoding.
   * @param tokens the sentence
   * @return the list of named entities in the current sentence
   */
  public List<String> getBioDictionaryMatch(String[] tokens) {

    List<String> entitiesList = new ArrayList<String>();

    String prefix = "-" + BioCodec.START;
    String gazEntry = null;
    String searchSpan = null;
    // iterative over tokens from the beginning
    for (int i = 0; i < tokens.length; i++) {
      gazEntry = null;
      int j;
      // iterate over tokens from the end
      for (j = tokens.length - 1; j >= i; j--) {
        // create span for search in dictionary Map; the first search takes as span
        // the whole sentence
        searchSpan = createSpan(tokens, i, j);
        gazEntry = lookup(searchSpan.toLowerCase());
        if (gazEntry != null) {
          break;
        }
      }
      prefix = "-" + BioCodec.START;
      // multi-token case
      if (gazEntry != null) {
        while (i < j) {
          entitiesList.add((gazEntry + prefix).intern());
          prefix = "-" + BioCodec.CONTINUE;
          i++;
        }
      }
      // one word case or last member of span
      if (gazEntry != null) {
        entitiesList.add((gazEntry + prefix).intern());
      } else {
        entitiesList.add(BioCodec.OTHER);
      }
    }
    return entitiesList;
  }
  
  /**
   * Performs gazetteer match in a bilou encoding.
   * @param tokens the sentence
   * @return the list of named entities in the current sentence
   */
  public List<String> getBilouDictionaryMatch(String[] tokens) {

    List<String> entitiesList = new ArrayList<String>();

    String prefix = "-" + BilouCodec.START;
    String gazClass = null;
    String searchSpan = null;
    // iterative over tokens from the beginning
    for (int i = 0; i < tokens.length; i++) {
      gazClass = null;
      int j;
      // iterate over tokens from the end
      for (j = tokens.length - 1; j >= i; j--) {
        // create span for search in dictionary Map; the first search takes as span
        // the whole sentence
        searchSpan = createSpan(tokens, i, j);
        gazClass = lookup(searchSpan.toLowerCase());
        if (gazClass != null) {
          break;
        }
      }
      prefix = "-" + BilouCodec.START;
      // multi-token case
      if (gazClass != null) {
        while (i < j) {
          entitiesList.add((gazClass + prefix).intern());
          prefix = "-" + BilouCodec.CONTINUE;
          i++;
        }
      }
      // one word case or last member of span
      if (gazClass != null) {
        if (prefix.equals("-" + BilouCodec.START)) {
          entitiesList.add((gazClass + "-" + BilouCodec.UNIT).intern());
        } else if (prefix.equals("-" + BilouCodec.CONTINUE)) {
          entitiesList.add((gazClass + "-" + BilouCodec.LAST).intern());
        }       
      } else {
        entitiesList.add(BilouCodec.OTHER);
      }
    }
    return entitiesList;
  }
  
  /**
   * Create a multi token entry search in the dictionary Map.
   * @param tokens the sentence
   * @param from the start index
   * @param to the end index
   * @return the string representing the possibly multi token entity
   */
  private String createSpan(String[] tokens, int from, int to) {
    String tokenSpan = "";
    for (int i = from; i < to; i++) {
      tokenSpan += tokens[i] + " ";
    }
    tokenSpan += tokens[to];
    return tokenSpan;
  }

  public void serialize(OutputStream out) throws IOException {
    Writer writer = new BufferedWriter(new OutputStreamWriter(out));
    for (Entry<String, String> entry : dictionary.entrySet()) {
        writer.write(entry.getKey() + "\t" + entry.getValue() + "\n");
    }
    writer.flush();
  }

  public Class<?> getArtifactSerializerClass() {
    return DictionarySerializer.class;
  }

}
