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
package eus.ixa.ixa.pipe.nerc.features;

import java.util.List;
import java.util.Map;

import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.featuregen.CustomFeatureGenerator;
import opennlp.tools.util.featuregen.FeatureGeneratorResourceProvider;

/**
 * Ciaramita and Altun (2006) word shape features. This feature generator
 * can also be placed on a sliding window.
 * 
 * @author ragerri
 * @version 2015-03-17
 */
public class WordShapeSuperSenseFeatureGenerator extends CustomFeatureGenerator {

  public WordShapeSuperSenseFeatureGenerator() {

  }

  public void createFeatures(List<String> features, String[] tokens, int index,
      String[] preds) {

    String normalizedToken = normalize(tokens[index]);
    features.add("sh=" + normalizedToken);
    features.add("w,sh=" + tokens[index].toLowerCase() + "," + normalizedToken);
  }

  /**
   * Normalize upper case, lower case, digits and duplicate characters.
   * 
   * @param token
   *          the token to be normalized
   * @return the normalized token
   */
  public static String normalize(String token) {
    String normalizedToken = "";

    char currentCharacter;
    int prevCharType = -1;
    char charType = '~';
    boolean addedStar = false;
    for (int i = 0; i < token.length(); i++) {

      currentCharacter = token.charAt(i);
      if (currentCharacter >= 'A' && currentCharacter <= 'Z') {
        charType = 'X';
      } else if (currentCharacter >= 'a' && currentCharacter <= 'z') {
        charType = 'x';
      } else if (currentCharacter >= '0' && currentCharacter <= '9') {
        charType = 'd';
      } else {
        charType = currentCharacter;
      }

      if (charType == prevCharType) {
        if (!addedStar) {
          normalizedToken += "*";
          addedStar = true;
        }
      } else {
        addedStar = false;
        normalizedToken += charType;
      }
      prevCharType = charType;
    }
    return normalizedToken;
  }

  @Override
  public void clearAdaptiveData() {

  }

  @Override
  public void updateAdaptiveData(String[] tokens, String[] outcomes) {

  }

  @Override
  public void init(Map<String, String> properties,
      FeatureGeneratorResourceProvider resources) throws InvalidFormatException {

  }

}
