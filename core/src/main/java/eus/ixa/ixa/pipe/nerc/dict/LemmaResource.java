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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.SerializableArtifact;

/**
 * @author ragerri
 * @version 2015-03-11
 * 
 */
public class LemmaResource implements SerializableArtifact {

  private static final Pattern spacePattern = Pattern.compile("\t");
  
  public static class LemmaResourceSerializer implements ArtifactSerializer<LemmaResource> {

    public LemmaResource create(InputStream in) throws IOException,
        InvalidFormatException {
      return new LemmaResource(in);
    }

    public void serialize(LemmaResource artifact, OutputStream out)
        throws IOException {
      artifact.serialize(out);
    }
  }
  
  /**
   * The dictionary for lemmatization.
   */
  private HashMap<List<String>, String> dictMap;
  
  /**
   * Build the Lemma Dictionary.
   * @param in the input stream
   * @throws IOException the io exception
   */
  public LemmaResource(InputStream in) throws IOException {
    dictMap = new HashMap<List<String>, String>();
    BufferedReader breader = new BufferedReader(new InputStreamReader(
        in));
    String line;
    try {
      while ((line = breader.readLine()) != null) {
        String[] elems = spacePattern.split(line);
        dictMap.put(Arrays.asList(elems[0], elems[2]), elems[1]);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Look-up lemma in dictionary.
   * @param word the word
   * @param postag the postag
   * @return the lemma
   */
  public String lookUpLemma(String word, String postag) {
    String lemma = null;
    String constantTag = "NNP";
    List<String> keys = this.getDictKeys(word, postag);
    // lookup lemma as value of the map
    String keyValue = dictMap.get(keys);
    if (keyValue != null) {
      lemma = keyValue;
    } else if (keyValue == null
        && postag.startsWith(String.valueOf(constantTag))) {
      lemma = word;
    } else if (keyValue == null && word.toUpperCase().equals(word)) {
      lemma = word;
    } else {
      lemma = word.toLowerCase();
    }
    return lemma;
  }
  
  /**
   * Look-up lemmas in dictionary.
   * @param tokens the sentence
   * @param postags the postags for each token
   * @return the lemmas for the sentence
   */
  public List<String> lookUpLemmaArray(String[] tokens, String[] postags) {
    List<String> lemmas = new ArrayList<String>();
    for (int i = 0; i < tokens.length; i++) {
      String lemma = lookUpLemma(tokens[i], postags[i]);
      lemmas.add(lemma);
    }
    return lemmas;
  }
  
  /**
   * Get the dictionary keys (word and postag).
   *
   * @param word
   *          the surface form word
   * @param postag
   *          the assigned postag
   * @return returns the dictionary keys
   */
  private List<String> getDictKeys(final String word,
      final String postag) {
    String constantTag = "NNP";
    List<String> keys = new ArrayList<String>();
    if (postag.startsWith(String.valueOf(constantTag))) {
      keys.addAll(Arrays.asList(word, postag));
    } else {
      keys.addAll(Arrays.asList(word.toLowerCase(), postag));
    }
    return keys;
  }
  
  public void serialize(OutputStream out) throws IOException {
    Writer writer = new BufferedWriter(new OutputStreamWriter(out));

    for (Map.Entry<List<String>, String> entry : dictMap.entrySet()) {
      writer.write(entry.getKey().get(0) + "\t" + entry.getValue() + "\t" + entry.getKey().get(1) +"\n");
    }
    writer.flush();
  }

  public Class<?> getArtifactSerializerClass() {
    return LemmaResourceSerializer.class;
  }

}


