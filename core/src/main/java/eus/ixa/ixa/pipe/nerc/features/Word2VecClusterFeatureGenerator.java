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
import eus.ixa.ixa.pipe.nerc.dict.Word2VecCluster;

public class Word2VecClusterFeatureGenerator extends CustomFeatureGenerator implements ArtifactToSerializerMapper {
  
  private Word2VecCluster word2vecCluster;
  private static String unknownClass = "noWord2Vec";
  private Map<String, String> attributes;
  
  
  public Word2VecClusterFeatureGenerator() {
  }
  
  public void createFeatures(List<String> features, String[] tokens, int index,
      String[] previousOutcomes) {
    
    String wordClass = getWordClass(tokens[index].toLowerCase());
    features.add(attributes.get("dict") + "=" + wordClass);
  }
  
  private String getWordClass(String token) {
    String wordClass = word2vecCluster.lookupToken(token);
    if (wordClass == null) {
      wordClass = unknownClass;
    }
    return wordClass;
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
    Object dictResource = resourceProvider.getResource(properties.get("dict"));
    if (!(dictResource instanceof Word2VecCluster)) {
      throw new InvalidFormatException("Not a Word2VecCluster resource for key: " + properties.get("dict"));
    }
    this.word2vecCluster = (Word2VecCluster) dictResource;
    this.attributes = properties;
  }
  
  @Override
  public Map<String, ArtifactSerializer<?>> getArtifactSerializerMapping() {
    Map<String, ArtifactSerializer<?>> mapping = new HashMap<>();
    mapping.put("word2vecserializer", new Word2VecCluster.Word2VecClusterSerializer());
    return Collections.unmodifiableMap(mapping);
  }
}
