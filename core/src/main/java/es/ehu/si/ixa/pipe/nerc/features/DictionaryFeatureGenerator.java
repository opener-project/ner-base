package es.ehu.si.ixa.pipe.nerc.features;

import es.ehu.si.ixa.pipe.nerc.DictionaryNameFinder;
import es.ehu.si.ixa.pipe.nerc.dict.Dictionary;

import java.util.List;

import opennlp.tools.util.featuregen.FeatureGeneratorAdapter;


public class DictionaryFeatureGenerator extends FeatureGeneratorAdapter {

  private InSpanGenerator isg;
  
  public DictionaryFeatureGenerator(Dictionary dict) {
    this("",dict);
  }
  public DictionaryFeatureGenerator(String prefix, Dictionary dict) {
    setDictionary(prefix,dict);
  }
  
  public void setDictionary(Dictionary dict) {
    setDictionary("",dict);
  }
  
  public void setDictionary(String name, Dictionary dict) {
    isg = new InSpanGenerator(name, new DictionaryNameFinder(dict));
  }
  
  public void createFeatures(List<String> features, String[] tokens, int index, String[] previousOutcomes) {
    isg.createFeatures(features, tokens, index, previousOutcomes);
  }
  
}
