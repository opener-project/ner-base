/*
 * Copyright 2015 Rodrigo Agerri

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
import eus.ixa.ixa.pipe.nerc.dict.LemmaResource;
import eus.ixa.ixa.pipe.nerc.dict.MFSResource;
import eus.ixa.ixa.pipe.nerc.dict.POSModelResource;

/**
 * Generates Ciaramita and Altun (2006) features for super sense tagging.
 * Do not use this SuperSenseFeatureGenerator with either the MorphoFeatureGenerator
 * or the MFSFeatureGenerator as both Morphological and MFS features are
 * included in this feature generator.
 * 
 * @author ragerri
 * @version 2015-03-30
 */
public class SuperSenseFeatureGenerator extends CustomFeatureGenerator implements
    ArtifactToSerializerMapper {

  private POSModelResource posModelResource;
  private LemmaResource lemmaDictResource;
  private MFSResource mfsDictResource;
  private String[] currentSentence;
  private String[] currentTags;
  private List<String> currentLemmas;
  private List<String> currentMFSList;
  private Boolean isBio = true;

  public SuperSenseFeatureGenerator() {
  }

  public void createFeatures(List<String> features, String[] tokens, int index,
      String[] previousOutcomes) {

    // cache results for each sentence
    if (currentSentence != tokens) {
      currentSentence = tokens;
      currentTags = posModelResource.posTag(tokens);
      currentLemmas = lemmaDictResource.lookUpLemmaArray(tokens, currentTags);
      if (isBio) {
        currentMFSList = mfsDictResource.getFirstSenseBio(currentLemmas, currentTags);
      } else {
      currentMFSList = mfsDictResource.getFirstSenseBilou(currentLemmas, currentTags);
      }
    }

    String curLemma = currentLemmas.get(index);
    String curTok = tokens[index];
    String curPOS = currentTags[index];
    String curShape = WordShapeSuperSenseFeatureGenerator.normalize(tokens[index]);
    String firstSense = currentMFSList.get(index);
    String prevLabel = null;

    String prevShape = null;
    String prevPOS = null;
    String prevLemma = null;
    String nextShape = null;
    String nextPOS = null;
    String nextLemma = null;

    String prev2Shape = null;
    String prev2POS = null;
    String prev2Lemma = null;
    String next2Shape = null;
    String next2POS = null;
    String next2Lemma = null;

    if (index - 2 >= 0) {
      prev2Shape = WordShapeSuperSenseFeatureGenerator.normalize(tokens[index - 2]);
      prev2Lemma = currentLemmas.get(index - 2);
      prev2POS = currentTags[index - 2];
    }
    if (index - 1 >= 0) {
      prevShape = WordShapeSuperSenseFeatureGenerator.normalize(tokens[index - 1]);
      prevLemma = currentLemmas.get(index - 1);
      prevPOS = currentTags[index - 1];
      prevLabel = previousOutcomes[index - 1];
    }
    if (index + 1 < tokens.length) {
      nextShape = WordShapeSuperSenseFeatureGenerator.normalize(tokens[index + 1]);
      nextLemma = currentLemmas.get(index + 1);
      nextPOS = currentTags[index + 1];
    }
    if (index + 2 < tokens.length) {
      next2Shape = WordShapeSuperSenseFeatureGenerator.normalize(tokens[index + 2]);
      next2Lemma = currentLemmas.get(index + 2);
      next2POS = currentTags[index + 2];
    }

    features.add("bias");

    if (firstSense == null) {
      firstSense = "O";
    }
    features.add("firstSense=" + firstSense);
    features.add("firstSense,curTok=" + firstSense + "," + curLemma);

    if (prevLabel != null) {
      features.add("prevLabel=" + prevLabel);
    }

    if (curPOS.equals("NN") || curPOS.equals("NNS")) {
      features.add("curPOS_common");
    }
    if (curPOS.equals("NNP") || curPOS.equals("NNPS")) {
      features.add("curPOS_proper");
    }
    features.add("curTok=" + curLemma);
    features.add("curPOS=" + curPOS);
    features.add("curPOS_0" + curPOS.charAt(0));

    if (prevPOS != null) {
      features.add("prevTok=" + prevLemma);
      features.add("prevPOS=" + prevPOS);
      features.add("prevPOS_0=" + prevPOS.charAt(0));
    }

    if (nextPOS != null) {
      features.add("nextTok=" + nextLemma);
      features.add("nextPOS=" + nextPOS);
      features.add("nextPOS_0" + nextPOS.charAt(0));
    }

    if (prev2POS != null) {
      features.add("prev2Tok=" + prev2Lemma);
      features.add("prev2POS=" + prev2POS);
      features.add("prev2POS_0=" + prev2POS.charAt(0));
    }
    if (next2POS != null) {
      features.add("next2Tok=" + next2Lemma);
      features.add("next2POS=" + next2POS);
      features.add("next2POS_0=" + next2POS.charAt(0));
    }

    features.add("curShape=" + curShape);
    if (prevPOS != null) {
      features.add("prevShape=" + prevShape);
    }
    if (nextPOS != null) {
      features.add("nextShape=" + nextShape);
    }
    if (prev2POS != null) {
      features.add("prev2Shape=" + prev2Shape);
    }
    if (next2POS != null) {
      features.add("next2Shape=" + next2Shape);
    }

    // word shapes with no window
    String firstCharCurTok = curTok.substring(0, 1);
    if (firstCharCurTok.toLowerCase().equals(firstCharCurTok)) {
      features.add("curTokLowercase");
    } else if (index == 0) {
      features.add("curTokUpperCaseFirstChar");
    } else {
      features.add("curTokUpperCaseOther");
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
    Object posResource = resourceProvider.getResource(properties.get("model"));
    if (!(posResource instanceof POSModelResource)) {
      throw new InvalidFormatException("Not a POSModelResource for key: "
          + properties.get("model"));
    }
    this.posModelResource = (POSModelResource) posResource;
    Object lemmaResource = resourceProvider.getResource(properties.get("dict"));
    if (!(lemmaResource instanceof LemmaResource)) {
      throw new InvalidFormatException("Not a LemmaResource for key: "
          + properties.get("dict"));
    }
    this.lemmaDictResource = (LemmaResource) lemmaResource;
    Object mfsResource = resourceProvider.getResource(properties.get("mfs"));
    if (!(mfsResource instanceof MFSResource)) {
      throw new InvalidFormatException("Not a MFSResource for key: "
          + properties.get("mfs"));
    }
    this.mfsDictResource = (MFSResource) mfsResource;
    if (properties.get("seqCodec").equalsIgnoreCase("bilou")) {
      isBio = false;
    } else {
      isBio = true;
    }
  }

  @Override
  public Map<String, ArtifactSerializer<?>> getArtifactSerializerMapping() {
    Map<String, ArtifactSerializer<?>> mapping = new HashMap<>();
    mapping.put("posmodelserializer",
        new POSModelResource.POSModelResourceSerializer());
    mapping.put("lemmadictserializer",
        new LemmaResource.LemmaResourceSerializer());
    mapping.put("mfsdictserializer", new MFSResource.MFSResourceSerializer());
    return Collections.unmodifiableMap(mapping);
  }
}
