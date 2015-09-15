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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.featuregen.ArtifactToSerializerMapper;
import opennlp.tools.util.featuregen.CustomFeatureGenerator;
import opennlp.tools.util.featuregen.FeatureGeneratorResourceProvider;
import opennlp.tools.util.model.ArtifactSerializer;
import eus.ixa.ixa.pipe.nerc.dict.BrownCluster;

public class BrownTokenFeatureGenerator extends CustomFeatureGenerator implements ArtifactToSerializerMapper {

  private BrownCluster brownLexicon;
  private Map<String, String> attributes;
  private static boolean DEBUG = false;

  public BrownTokenFeatureGenerator() {
  }
  
  public void createFeatures(List<String> features, String[] tokens, int index,
      String[] previousOutcomes) {
    
    List<String> wordClasses = BrownTokenClasses.getWordClasses(tokens[index], brownLexicon);
    if (DEBUG) {
      BrownTokenClasses.printList(wordClasses);
    }
    for (int i = 0; i < wordClasses.size(); i++) {
      features.add(attributes.get("dict") + "=" + wordClasses.get(i));
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
    Object dictResource = resourceProvider.getResource(properties.get("dict"));
    if (!(dictResource instanceof BrownCluster)) {
      throw new InvalidFormatException("Not a BrownCluster resource for key: " + properties.get("dict"));
    }
    this.brownLexicon = (BrownCluster) dictResource;
    this.attributes = properties;
  }

  @Override
  public Map<String, ArtifactSerializer<?>> getArtifactSerializerMapping() {
    Map<String, ArtifactSerializer<?>> mapping = new HashMap<>();
    mapping.put("brownserializer", new BrownCluster.BrownClusterSerializer());
    return Collections.unmodifiableMap(mapping);
  }

  

}
