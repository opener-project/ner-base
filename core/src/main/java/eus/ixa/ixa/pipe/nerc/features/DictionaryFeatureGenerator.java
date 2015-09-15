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
import eus.ixa.ixa.pipe.nerc.dict.Dictionary;

/**
 * Checks if a named entity is in a gazetteer.
 * @author ragerri
 * @version 2015-03-30
 *
 */
public class DictionaryFeatureGenerator extends CustomFeatureGenerator implements  ArtifactToSerializerMapper {

  private String[] currentSentence;
  private List<String> currentEntities;
  private Dictionary dictionary;
  private Map<String, String> attributes;
  private boolean isBilou = true;
  
  public DictionaryFeatureGenerator() {
  }
  
  public void createFeatures(List<String> features, String[] tokens, int index, String[] previousOutcomes) {
    
    // cache results for sentence
    if (currentSentence != tokens) {
      currentSentence = tokens;
      if (isBilou) {
        currentEntities = dictionary.getBilouDictionaryMatch(tokens);
      } else {
        currentEntities = dictionary.getBioDictionaryMatch(tokens);
      }
    }
    
    String currentEntity = currentEntities.get(index);
    
    features.add(attributes.get("dict") + "=" + currentEntity);
    features.add(attributes.get("dict") + "," + "w=" + currentEntity + "," + tokens[index]);
    features.add(attributes.get("dict") + ",w=dict");
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
    if (!(dictResource instanceof Dictionary)) {
      throw new InvalidFormatException("Not a Dictionary resource for key: " + properties.get("dict"));
    }
    this.dictionary = (Dictionary) dictResource;
    this.attributes = properties;
    if (properties.get("seqCodec").equalsIgnoreCase("bio")) {
      isBilou = false;
    } else {
      isBilou = true;
    }
  }

  @Override
  public Map<String, ArtifactSerializer<?>> getArtifactSerializerMapping() {
    Map<String, ArtifactSerializer<?>> mapping = new HashMap<>();
    mapping.put("dictionaryserializer", new Dictionary.DictionarySerializer());
    return Collections.unmodifiableMap(mapping);
  }
}
  
