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
import java.util.Map;

import opennlp.tools.ngram.NGramModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.StringList;
import opennlp.tools.util.featuregen.CustomFeatureGenerator;
import opennlp.tools.util.featuregen.FeatureGeneratorResourceProvider;

/**
 * The {@link CharacterNgramFeatureGenerator} uses character ngrams to
 * generate features about each token.
 * The minimum and maximum length can be specified.
 */
public class CharacterNgramFeatureGenerator extends CustomFeatureGenerator {
  
  private Map<String, String> attributes;

  /**
   * Initializes the current instance.
   */
  public CharacterNgramFeatureGenerator() {
  }

  public void createFeatures(List<String> features, String[] tokens, int index, String[] preds) {

    NGramModel model = new NGramModel();
    model.add(tokens[index], Integer.parseInt(attributes.get("minLength")), Integer.parseInt(attributes.get("maxLength")));

    for (StringList tokenList : model) {

      if (tokenList.size() > 0) {
        features.add("ng=" + tokenList.getToken(0).toLowerCase());
      }
    }
  }

  @Override
  public void updateAdaptiveData(String[] tokens, String[] outcomes) {
  }

  @Override
  public void clearAdaptiveData() {
  }

  @Override
  public void init(Map<String, String> properties,
      FeatureGeneratorResourceProvider resourceProvider)
      throws InvalidFormatException {
    this.attributes = properties;
    
  }
}

