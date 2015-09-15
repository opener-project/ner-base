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

import opennlp.tools.util.featuregen.CustomFeatureGenerator;
import opennlp.tools.util.featuregen.FeatureGeneratorResourceProvider;

public class SentenceFeatureGenerator extends CustomFeatureGenerator {

  private Map<String, String> attributes;

  public SentenceFeatureGenerator() {
  }

  public void createFeatures(List<String> features, String[] tokens, int index,
      String[] previousOutcomes) {

    if (attributes.get("begin").equalsIgnoreCase("true") && index == 0) {
      features.add("S=begin");
    }

    if (attributes.get("end").equalsIgnoreCase("true") && tokens.length == index + 1) {
      features.add("S=end");
    }
  }

  public void init(Map<String, String> attributes, FeatureGeneratorResourceProvider resourceProvider) {
    this.attributes = attributes;
    setBeginSentenceAttribute(attributes);
    setEndSentenceAttribute(attributes);
  }

  private void setBeginSentenceAttribute(Map<String, String> attributes) {
    attributes.put("begin", "true");
  }

  private void setEndSentenceAttribute(Map<String, String> attributes) {
    attributes.put("end", "false");
  }

  @Override
  public void updateAdaptiveData(String[] tokens, String[] outcomes) {

  }

  @Override
  public void clearAdaptiveData() {

  }

}
