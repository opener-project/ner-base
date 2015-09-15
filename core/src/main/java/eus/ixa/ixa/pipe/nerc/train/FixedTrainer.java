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

package eus.ixa.ixa.pipe.nerc.train;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.SequenceCodec;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ArtifactSerializer;
import eus.ixa.ixa.pipe.nerc.StringUtils;
import eus.ixa.ixa.pipe.nerc.dict.BrownCluster;
import eus.ixa.ixa.pipe.nerc.dict.ClarkCluster;
import eus.ixa.ixa.pipe.nerc.dict.Dictionary;
import eus.ixa.ixa.pipe.nerc.dict.LemmaResource;
import eus.ixa.ixa.pipe.nerc.dict.MFSResource;
import eus.ixa.ixa.pipe.nerc.dict.POSModelResource;
import eus.ixa.ixa.pipe.nerc.dict.Word2VecCluster;
import eus.ixa.ixa.pipe.nerc.features.XMLFeatureDescriptor;

/**
 * Training sequence labeler based on Apache OpenNLP Machine Learning API. This class creates
 * a feature set based on the features activated in the trainParams.properties
 * file:
 * <ol>
 * <li>Window: specify left and right window lengths.
 * <li>TokenFeatures: tokens as features in a window length.
 * <li>TokenClassFeatures: token shape features in a window length.
 * <li>WordShapeSuperSenseFeatures: token shape features from Ciaramita and Altun (2006).
 * <li>OutcomePriorFeatures: take into account previous outcomes.
 * <li>PreviousMapFeatures: add features based on tokens and previous decisions.
 * <li>SentenceFeatures: add beginning and end of sentence words.
 * <li>PrefixFeatures: first 4 characters in current token.
 * <li>SuffixFeatures: last 4 characters in current token.
 * <li>BigramClassFeatures: bigrams of tokens and token class.
 * <li>TrigramClassFeatures: trigrams of token and token class.
 * <li>FourgramClassFeatures: fourgrams of token and token class.
 * <li>FivegramClassFeatures: fivegrams of token and token class.
 * <li>CharNgramFeatures: character ngram features of current token.
 * <li>DictionaryFeatures: check if current token appears in some gazetteer.
 * <li>ClarkClusterFeatures: use the clustering class of a token as a feature.
 * <li>BrownClusterFeatures: use brown clusters as features for each feature
 * containing a token.
 * <li>Word2VecClusterFeatures: use the word2vec clustering class of a token as
 * a feature.
 * <li>MorphoFeatures: use pos tags, pos tag class and lemma as features.
 * <li>MFSFeatures: Most Frequent sense feature.
 * <li>SuperSenseFeatures: Ciaramita and Altun (2006) features for super sense tagging.
 * </ol>
 * 
 * @author ragerri
 * @version 2015-03-27
 */
public class FixedTrainer extends AbstractTrainer {
  
  /**
   * Construct a trainer.
   * @param params the training parameters
   * @throws IOException if io errors
   */
  public FixedTrainer(final TrainingParameters params) throws IOException {
    super(params);
    createTrainer(params);
  }

  /**
   * Create {@code TokenNameFinderFactory} with custom features.
   * 
   * @param params
   *          the parameter training file
   * @throws IOException if io error
   */
  public void createTrainer(TrainingParameters params) throws IOException {
    String seqCodec = getSequenceCodec();
    SequenceCodec<String> sequenceCodec = TokenNameFinderFactory
        .instantiateSequenceCodec(seqCodec);
    String featureDescription = XMLFeatureDescriptor
        .createXMLFeatureDescriptor(params);
    System.err.println(featureDescription);
    byte[] featureGeneratorBytes = featureDescription.getBytes(Charset
        .forName("UTF-8"));
    Map<String, Object> resources = loadResources(params, featureGeneratorBytes);
    setNameClassifierFactory(TokenNameFinderFactory.create(
        TokenNameFinderFactory.class.getName(), featureGeneratorBytes,
        resources, sequenceCodec));
  }

  /**
   * Load the external resources such as gazetters and clustering lexicons.
   * @param params the training parameters
   * @param featureGenDescriptor the feature generator descriptor
   * @return the map contanining and id and the resource
   * @throws IOException if io error
   */
  public static Map<String, Object> loadResources(TrainingParameters params,
      byte[] featureGenDescriptor) throws IOException {
    Map<String, Object> resources = new HashMap<String, Object>();
    @SuppressWarnings("rawtypes")
    Map<String, ArtifactSerializer> artifactSerializers = TokenNameFinderModel.createArtifactSerializers();
    
    if (Flags.isBrownFeatures(params)) {
      String brownClusterPath = Flags.getBrownFeatures(params);
      String serializerId = "brownserializer";
      List<File> brownClusterFiles = Flags.getClusterLexiconFiles(brownClusterPath);
      for (File brownClusterFile : brownClusterFiles) {
        String brownFilePath = brownClusterFile.getCanonicalPath();
        artifactSerializers.put(serializerId, new BrownCluster.BrownClusterSerializer());
        loadResource(serializerId, artifactSerializers, brownFilePath, featureGenDescriptor, resources);
      }
    }
    if (Flags.isClarkFeatures(params)) {
      String clarkClusterPath = Flags.getClarkFeatures(params);
      String serializerId = "clarkserializer";
      List<File> clarkClusterFiles = Flags.getClusterLexiconFiles(clarkClusterPath);
      for (File clarkClusterFile: clarkClusterFiles) {
        String clarkFilePath = clarkClusterFile.getCanonicalPath();
        artifactSerializers.put(serializerId, new ClarkCluster.ClarkClusterSerializer());
        loadResource(serializerId, artifactSerializers, clarkFilePath, featureGenDescriptor, resources);
      }
    }
    if (Flags.isWord2VecClusterFeatures(params)) {
      String word2vecClusterPath = Flags.getWord2VecClusterFeatures(params);
      String serializerId = "word2vecserializer";
      List<File> word2vecClusterFiles = Flags.getClusterLexiconFiles(word2vecClusterPath);
      for (File word2vecClusterFile : word2vecClusterFiles) {
        String word2vecFilePath = word2vecClusterFile.getCanonicalPath();
        artifactSerializers.put(serializerId, new Word2VecCluster.Word2VecClusterSerializer());
        loadResource(serializerId, artifactSerializers, word2vecFilePath, featureGenDescriptor, resources);
      }
    }
    if (Flags.isDictionaryFeatures(params)) {
      String dictDir = Flags.getDictionaryFeatures(params);
      String serializerId = "dictionaryserializer";
      List<File> fileList = StringUtils.getFilesInDir(new File(dictDir));
      for (File dictFile : fileList) {
        String dictionaryPath = dictFile.getCanonicalPath();
        artifactSerializers.put(serializerId, new Dictionary.DictionarySerializer());
        loadResource(serializerId, artifactSerializers, dictionaryPath, featureGenDescriptor, resources);
      }
    }
    if (Flags.isMorphoFeatures(params)) {
      String morphoResourcesPath = Flags.getMorphoFeatures(params);
      String[] morphoResources = Flags.getMorphoResources(morphoResourcesPath);
      String posSerializerId = "postagserializer";
      String lemmaSerializerId = "lemmaserializer";
      artifactSerializers.put(posSerializerId, new POSModelResource.POSModelResourceSerializer());
      loadResource(posSerializerId, artifactSerializers, morphoResources[0], featureGenDescriptor, resources);
      artifactSerializers.put(lemmaSerializerId, new LemmaResource.LemmaResourceSerializer());
      loadResource(lemmaSerializerId, artifactSerializers, morphoResources[1], featureGenDescriptor, resources);
    }
    if (Flags.isSuperSenseFeatures(params)) {
      String mfsResourcesPath = Flags.getSuperSenseFeatures(params);
      String[] mfsResources = Flags.getSuperSenseResources(mfsResourcesPath);
      String posSerializerId = "postagserializer";
      String lemmaSerializerId = "lemmaserializer";
      String mfsSerializerId = "mfsserializer";
      artifactSerializers.put(posSerializerId, new POSModelResource.POSModelResourceSerializer());
      loadResource(posSerializerId, artifactSerializers, mfsResources[0], featureGenDescriptor, resources);
      artifactSerializers.put(lemmaSerializerId, new LemmaResource.LemmaResourceSerializer());
      loadResource(lemmaSerializerId, artifactSerializers, mfsResources[1], featureGenDescriptor, resources);
      artifactSerializers.put(mfsSerializerId, new MFSResource.MFSResourceSerializer());
      loadResource(mfsSerializerId, artifactSerializers, mfsResources[2], featureGenDescriptor, resources);
    }
    if (Flags.isMFSFeatures(params)) {
      String mfsResourcesPath = Flags.getMFSFeatures(params);
      String[] mfsResources = Flags.getMFSResources(mfsResourcesPath);
      String posSerializerId = "postagserializer";
      String lemmaSerializerId = "lemmaserializer";
      String mfsSerializerId = "mfsserializer";
      artifactSerializers.put(posSerializerId, new POSModelResource.POSModelResourceSerializer());
      loadResource(posSerializerId, artifactSerializers, mfsResources[0], featureGenDescriptor, resources);
      artifactSerializers.put(lemmaSerializerId, new LemmaResource.LemmaResourceSerializer());
      loadResource(lemmaSerializerId, artifactSerializers, mfsResources[1], featureGenDescriptor, resources);
      artifactSerializers.put(mfsSerializerId, new MFSResource.MFSResourceSerializer());
      loadResource(mfsSerializerId, artifactSerializers, mfsResources[2], featureGenDescriptor, resources);
    }
    return resources;
  }

  /**
   * Load a resource by resourceId.
   * @param serializerId the serializer id
   * @param artifactSerializers the serializers in which to put the resource
   * @param resourcePath the canonical path of the resource
   * @param featureGenDescriptor the feature descriptor
   * @param resources the map in which to put the resource
   */
  public static void loadResource(String serializerId, @SuppressWarnings("rawtypes") Map<String, ArtifactSerializer> artifactSerializers, String resourcePath,
      byte[] featureGenDescriptor, Map<String, Object> resources) {

    File resourceFile = new File(resourcePath);
    if (resourceFile != null) {
      String resourceId = InputOutputUtils.normalizeLexiconName(resourceFile.getName());
      ArtifactSerializer<?> serializer = artifactSerializers.get(serializerId);
      InputStream resourceIn = CmdLineUtil.openInFile(resourceFile);
      try {
        resources.put(resourceId, serializer.create(resourceIn));
      } catch (InvalidFormatException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          resourceIn.close();
        } catch (IOException e) {
        }
      }
    }
  }

}
