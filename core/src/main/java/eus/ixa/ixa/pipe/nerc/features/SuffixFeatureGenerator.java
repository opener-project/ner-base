/*
 * Copyright 2014 Rodrigo Agerri

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
package eus.ixa.ixa.pipe.nerc.features;

import java.util.List;

import opennlp.tools.util.featuregen.FeatureGeneratorAdapter;


public class SuffixFeatureGenerator extends FeatureGeneratorAdapter {

  private static final int SUFFIX_LENGTH = 4;
  
  public static String[] getSuffixes(String lex) {
    String[] suffs = new String[SUFFIX_LENGTH];
    for (int li = 0, ll = SUFFIX_LENGTH; li < ll; li++) {
      suffs[li] = lex.substring(Math.max(lex.length() - li - 1, 0));
    }
    return suffs;
  }
  
  public void createFeatures(List<String> features, String[] tokens, int index,
      String[] previousOutcomes) {
    String[] suffs = SuffixFeatureGenerator.getSuffixes(tokens[index]);
    for (String suff : suffs) {
      features.add("suf=" + suff);
    }
  }
}