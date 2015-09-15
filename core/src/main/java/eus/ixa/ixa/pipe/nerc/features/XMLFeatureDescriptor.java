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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.TrainingParameters;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import eus.ixa.ixa.pipe.nerc.StringUtils;
import eus.ixa.ixa.pipe.nerc.train.Flags;
import eus.ixa.ixa.pipe.nerc.train.InputOutputUtils;

/**
 * Class to automatically generate the feature descriptor from a trainParams.properties file.
 * @author ragerri
 * @version 2015-03-30
 */
public final class XMLFeatureDescriptor {
  
  /**
   * The leftWindow length.
   */
  private static int leftWindow = -1;
  /**
   * The rightWindow length.
   */
  private static int rightWindow = -1;
  /**
   * The minimum character ngram to be applied to a token.
   */
  private static int minCharNgram = -1;
  /**
   * The maximum character ngram to be applied to a token.
   */
  private static int maxCharNgram = -1;
  
  /**
   * This class is not to be instantiated.
   */
  private XMLFeatureDescriptor() {
  }
  
  /**
   * Get the left window feature length.
   * @return the leftWindow length
   */
  public static int getLeftWindow() {
    return leftWindow;
  }
  /**
   * Get the right window feature length.
   * @return the rightWindow length
   */
  public static int getRightWindow() {
    return rightWindow;
  }
  /**
   * Get the minimum character ngram.
   * @return the minimum character ngram value
   */
  public static int getMinCharNgram() {
    return minCharNgram;
  }
  
  /**
   * Get the maximum character ngram.
   * @return the maximum character ngram value
   */
  public static int getMaxCharNgram() {
    return maxCharNgram;
  }
  
  /**
   * Generate the XML feature descriptor from the TrainingParameters prop file.
   * @param params the properties file
   * @return the XML feature descriptor
   * @throws IOException if input output fails
   */
  public static String createXMLFeatureDescriptor(TrainingParameters params) throws IOException {
    
    Element aggGenerators = new Element("generators");
    Document doc = new Document(aggGenerators);
    
    //<generators>
    //  <cache>
    //    <generators>
    Element cached = new Element("cache");
    Element generators = new Element("generators");
    //<window prevLength="2" nextLength="2">
    //  <token />
    //</window>
    if (Flags.isTokenFeature(params)) {
      setWindow(params);
      Element tokenFeature = new Element("custom");
      tokenFeature.setAttribute("class", TokenFeatureGenerator.class.getName());
      Element tokenWindow = new Element("window");
      tokenWindow.setAttribute("prevLength", Integer.toString(leftWindow));
      tokenWindow.setAttribute("nextLength", Integer.toString(rightWindow));
      tokenWindow.addContent(tokenFeature);
      generators.addContent(tokenWindow);
      System.err.println("-> Token features added!: Window range " + leftWindow + ":" + rightWindow);
    }
    if (Flags.isTokenClassFeature(params)) {
      setWindow(params);
      Element tokenClassFeature = new Element("custom");
      tokenClassFeature.setAttribute("class", TokenClassFeatureGenerator.class.getName());
      Element tokenClassWindow = new Element("window");
      tokenClassWindow.setAttribute("prevLength", Integer.toString(leftWindow));
      tokenClassWindow.setAttribute("nextLength", Integer.toString(rightWindow));
      tokenClassWindow.addContent(tokenClassFeature);
      generators.addContent(tokenClassWindow);
      System.err.println("-> Token Class Features added!: Window range " + leftWindow + ":" + rightWindow);
    }
    if (Flags.isWordShapeSuperSenseFeature(params)) {
      setWindow(params);
      Element wordShapeSuperSenseFeature = new Element("custom");
      wordShapeSuperSenseFeature.setAttribute("class", WordShapeSuperSenseFeatureGenerator.class.getName());
      Element wordShapeWindow = new Element("window");
      wordShapeWindow.setAttribute("prevLength", Integer.toString(leftWindow));
      wordShapeWindow.setAttribute("nextLength", Integer.toString(rightWindow));
      wordShapeWindow.addContent(wordShapeSuperSenseFeature);
      generators.addContent(wordShapeWindow);
      System.err.println("-> Word Shape SuperSense Features added!: Window range " + leftWindow + ":" + rightWindow);
    }
    if (Flags.isOutcomePriorFeature(params)) {
      Element outcomePriorFeature = new Element("custom");
      outcomePriorFeature.setAttribute("class", OutcomePriorFeatureGenerator.class.getName());
      generators.addContent(outcomePriorFeature);
      System.err.println("-> Outcome Prior Features added!");
    }
    if (Flags.isPreviousMapFeature(params)) {
      Element previousMapFeature = new Element("custom");
      previousMapFeature.setAttribute("class", PreviousMapFeatureGenerator.class.getName());
      generators.addContent(previousMapFeature);
      System.err.println("-> Previous Map Features added!");
    }
    if (Flags.isSentenceFeature(params)) {
      Element sentenceFeature = new Element("custom");
      sentenceFeature.setAttribute("class", SentenceFeatureGenerator.class.getName());
      sentenceFeature.setAttribute("begin", "true");
      sentenceFeature.setAttribute("end", "false");
      generators.addContent(sentenceFeature);
      System.err.println("-> Sentence Features added!");
    }
    if (Flags.isPrefixFeature(params)) {
      Element prefixFeature = new Element("custom");
      prefixFeature.setAttribute("class", Prefix34FeatureGenerator.class.getName());
      generators.addContent(prefixFeature);
      System.err.println("-> Prefix Features added!");
    }
    if (Flags.isSuffixFeature(params)) {
      Element suffixFeature = new Element("custom");
      suffixFeature.setAttribute("class", SuffixFeatureGenerator.class.getName());
      generators.addContent(suffixFeature);
      System.err.println("-> Suffix Features added!");
    }
    if (Flags.isBigramClassFeature(params)) {
      Element bigramFeature = new Element("custom");
      bigramFeature.setAttribute("class", BigramClassFeatureGenerator.class.getName());
      generators.addContent(bigramFeature);
      System.err.println("-> Bigram Class Features added!");
    }
    if (Flags.isTrigramClassFeature(params)) {
      Element trigramFeature = new Element("custom");
      trigramFeature.setAttribute("class", TrigramClassFeatureGenerator.class.getName());
      generators.addContent(trigramFeature);
      System.err.println("-> Trigram Class Features added!");
    }
    if (Flags.isFourgramClassFeature(params)) {
      Element fourgramFeature = new Element("custom");
      fourgramFeature.setAttribute("class", FourgramClassFeatureGenerator.class.getName());
      generators.addContent(fourgramFeature);
      System.err.println("-> Fourgram Class Features added!");
    }
    if (Flags.isFivegramClassFeature(params)) {
      Element fivegramFeature = new Element("custom");
      fivegramFeature.setAttribute("class", FivegramClassFeatureGenerator.class.getName());
      generators.addContent(fivegramFeature);
      System.err.println("-> Fivegram Class Features added!");
    }
    if (Flags.isCharNgramClassFeature(params)) {
      setNgramRange(params);
      Element charngramFeature = new Element("custom");
      charngramFeature.setAttribute("class", CharacterNgramFeatureGenerator.class.getName());
      charngramFeature.setAttribute("minLength", Integer.toString(minCharNgram));
      charngramFeature.setAttribute("maxLength",Integer.toString(maxCharNgram));
      generators.addContent(charngramFeature);
      System.err.println("-> CharNgram Class Features added!");
    }
    //Dictionary Features
    if (Flags.isDictionaryFeatures(params)) {
      setWindow(params);
      String dictPath = Flags.getDictionaryFeatures(params);
      String seqCodec = Flags.getSequenceCodec(params);
      List<File> fileList = StringUtils.getFilesInDir(new File(dictPath));
      for (File dictFile : fileList) {
        Element dictFeatures = new Element("custom");
        dictFeatures.setAttribute("class", DictionaryFeatureGenerator.class.getName());
        dictFeatures.setAttribute("dict", InputOutputUtils.normalizeLexiconName(dictFile.getName()));
        dictFeatures.setAttribute("seqCodec", seqCodec);
        Element dictWindow = new Element("window");
        dictWindow.setAttribute("prevLength", Integer.toString(leftWindow));
        dictWindow.setAttribute("nextLength", Integer.toString(rightWindow));
        dictWindow.addContent(dictFeatures);
        generators.addContent(dictWindow);
      }
      System.err.println("-> Dictionary Features added!");
    }
    //Brown clustering features
    if (Flags.isBrownFeatures(params)) {
      setWindow(params);
      //previous 2 maps features
      Element prev2MapFeature = new Element("custom");
      prev2MapFeature.setAttribute("class", Prev2MapFeatureGenerator.class.getName());
      generators.addContent(prev2MapFeature);
      //previous map and token feature (in window)
      Element prevMapTokenFeature = new Element("custom");
      prevMapTokenFeature.setAttribute("class", PreviousMapTokenFeatureGenerator.class.getName());
      Element prevMapTokenWindow = new Element("window");
      prevMapTokenWindow.setAttribute("prevLength", Integer.toString(leftWindow));
      prevMapTokenWindow.setAttribute("nextLength", Integer.toString(rightWindow));
      prevMapTokenWindow.addContent(prevMapTokenFeature);
      generators.addContent(prevMapTokenWindow);
      //brown clustering features
      String brownClusterPath = Flags.getBrownFeatures(params);
      List<File> brownClusterFiles = Flags.getClusterLexiconFiles(brownClusterPath);
      for (File brownClusterFile : brownClusterFiles) {
        //brown bigram class features
        Element brownBigramFeatures = new Element("custom");
        brownBigramFeatures.setAttribute("class", BrownBigramFeatureGenerator.class.getName());
        brownBigramFeatures.setAttribute("dict", InputOutputUtils.normalizeLexiconName(brownClusterFile.getName()));
        generators.addContent(brownBigramFeatures);
        //brown token feature
        Element brownTokenFeature = new Element("custom");
        brownTokenFeature.setAttribute("class", BrownTokenFeatureGenerator.class.getName());
        brownTokenFeature.setAttribute("dict", InputOutputUtils.normalizeLexiconName(brownClusterFile.getName()));
        Element brownTokenWindow = new Element("window");
        brownTokenWindow.setAttribute("prevLength", Integer.toString(leftWindow));
        brownTokenWindow.setAttribute("nextLength", Integer.toString(rightWindow));
        brownTokenWindow.addContent(brownTokenFeature);
        generators.addContent(brownTokenWindow);
        //brown token class feature
        Element brownTokenClassFeature = new Element("custom");
        brownTokenClassFeature.setAttribute("class", BrownTokenClassFeatureGenerator.class.getName());
        brownTokenClassFeature.setAttribute("dict", InputOutputUtils.normalizeLexiconName(brownClusterFile.getName()));
        Element brownTokenClassWindow = new Element("window");
        brownTokenClassWindow.setAttribute("prevLength", Integer.toString(leftWindow));
        brownTokenClassWindow.setAttribute("nextLength", Integer.toString(rightWindow));
        brownTokenClassWindow.addContent(brownTokenClassFeature);
        generators.addContent(brownTokenClassWindow);
      }
      System.err.println("-> Brown Cluster Features added!");
    }
    //Clark clustering features
    if (Flags.isClarkFeatures(params)) {
      setWindow(params);
      String clarkClusterPath = Flags.getClarkFeatures(params);
      List<File> clarkClusterFiles = Flags.getClusterLexiconFiles(clarkClusterPath);
      for (File clarkCluster: clarkClusterFiles) {
        Element clarkFeatures = new Element("custom");
        clarkFeatures.setAttribute("class", ClarkFeatureGenerator.class.getName());
        clarkFeatures.setAttribute("dict", InputOutputUtils.normalizeLexiconName(clarkCluster.getName()));
        Element clarkWindow = new Element("window");
        clarkWindow.setAttribute("prevLength", Integer.toString(leftWindow));
        clarkWindow.setAttribute("nextLength", Integer.toString(rightWindow));
        clarkWindow.addContent(clarkFeatures);
        generators.addContent(clarkWindow);
      }
      System.err.println("-> Clark Cluster Features added!");
    }
    //word2vec clustering features
    if (Flags.isWord2VecClusterFeatures(params)) {
      setWindow(params);
      String word2vecClusterPath = Flags.getWord2VecClusterFeatures(params);
      List<File> word2vecClusterFiles = Flags.getClusterLexiconFiles(word2vecClusterPath);
      for (File word2vecFile : word2vecClusterFiles) {
        Element word2vecClusterFeatures = new Element("custom");
        word2vecClusterFeatures.setAttribute("class", Word2VecClusterFeatureGenerator.class.getName());
        word2vecClusterFeatures.setAttribute("dict", InputOutputUtils.normalizeLexiconName(word2vecFile.getName()));
        Element word2vecClusterWindow = new Element("window");
        word2vecClusterWindow.setAttribute("prevLength", Integer.toString(leftWindow));
        word2vecClusterWindow.setAttribute("nextLength", Integer.toString(rightWindow));
        word2vecClusterWindow.addContent(word2vecClusterFeatures);
        generators.addContent(word2vecClusterWindow);
      }
      System.err.println("-> Word2Vec Clusters Features added!");
    }
    //Morphological features
    if (Flags.isMorphoFeatures(params)) {
      setWindow(params);
      String morphoPath = Flags.getMorphoFeatures(params);
      String[] morphoResources = Flags.getMorphoResources(morphoPath);
      String morphoRange = Flags.getMorphoFeaturesRange(params);
      Element morphoClassFeatureElement = new Element("custom");
      morphoClassFeatureElement.setAttribute("class", MorphoFeatureGenerator.class.getName());
      morphoClassFeatureElement.setAttribute("model", InputOutputUtils.normalizeLexiconName(new File(morphoResources[0]).getName()));
      morphoClassFeatureElement.setAttribute("dict", InputOutputUtils.normalizeLexiconName(new File(morphoResources[1]).getName()));
      morphoClassFeatureElement.setAttribute("range", morphoRange);
      Element morphoClassFeatureWindow = new Element("window");
      morphoClassFeatureWindow.setAttribute("prevLength", Integer.toString(leftWindow));
      morphoClassFeatureWindow.setAttribute("nextLength", Integer.toString(rightWindow));
      morphoClassFeatureWindow.addContent(morphoClassFeatureElement);
      generators.addContent(morphoClassFeatureWindow);
      System.err.println("-> Morphological Features added!");
    }
    if (Flags.isMFSFeatures(params)) {
      setWindow(params);
      String mfsPath = Flags.getMFSFeatures(params);
      String[] mfsResources = Flags.getMFSResources(mfsPath);
      String mfsRange = Flags.getMFSFeaturesRange(params);
      String seqCodec = Flags.getSequenceCodec(params);
      Element mfsClassFeatureElement = new Element("custom");
      mfsClassFeatureElement.setAttribute("class", MFSFeatureGenerator.class.getName());
      mfsClassFeatureElement.setAttribute("model", InputOutputUtils.normalizeLexiconName(new File(mfsResources[0]).getName()));
      mfsClassFeatureElement.setAttribute("dict", InputOutputUtils.normalizeLexiconName(new File(mfsResources[1]).getName()));
      mfsClassFeatureElement.setAttribute("mfs", InputOutputUtils.normalizeLexiconName(new File(mfsResources[2]).getName()));
      mfsClassFeatureElement.setAttribute("range", mfsRange);
      mfsClassFeatureElement.setAttribute("seqCodec", seqCodec);
      Element mfsClassFeatureWindow = new Element("window");
      mfsClassFeatureWindow.setAttribute("prevLength", Integer.toString(leftWindow));
      mfsClassFeatureWindow.setAttribute("nextLength", Integer.toString(rightWindow));
      mfsClassFeatureWindow.addContent(mfsClassFeatureElement);
      generators.addContent(mfsClassFeatureWindow);
      System.err.println("-> MFS Features added");
      }
    if (Flags.isSuperSenseFeatures(params)) {
      String mfsPath = Flags.getSuperSenseFeatures(params);
      String[] mfsResources = Flags.getSuperSenseResources(mfsPath);
      String mfsRange = Flags.getSuperSenseFeaturesRange(params);
      String seqCodec = Flags.getSequenceCodec(params);
      Element mfsClassFeatureElement = new Element("custom");
      mfsClassFeatureElement.setAttribute("class", SuperSenseFeatureGenerator.class.getName());
      mfsClassFeatureElement.setAttribute("model", InputOutputUtils.normalizeLexiconName(new File(mfsResources[0]).getName()));
      mfsClassFeatureElement.setAttribute("dict", InputOutputUtils.normalizeLexiconName(new File(mfsResources[1]).getName()));
      mfsClassFeatureElement.setAttribute("mfs", InputOutputUtils.normalizeLexiconName(new File(mfsResources[2]).getName()));
      mfsClassFeatureElement.setAttribute("range", mfsRange);
      mfsClassFeatureElement.setAttribute("seqCodec", seqCodec);
      generators.addContent(mfsClassFeatureElement);
      System.err.println("-> SuperSense Features added!");
      }
    
    aggGenerators.addContent(cached);
    cached.addContent(generators);
    
    XMLOutputter xmlOutput = new XMLOutputter();
    Format format = Format.getPrettyFormat();
    xmlOutput.setFormat(format);
    return xmlOutput.outputString(doc);
    
  }
  
  /**
   * Set the window length from the training parameters file.
   * @param params the properties file
   */
  public static void setWindow(TrainingParameters params) {
    if (leftWindow == -1 || rightWindow == -1) {
      leftWindow = getWindowRange(params).get(0);
      rightWindow = getWindowRange(params).get(1);
    }
  }
  
  /**
   * Get the window range feature.
   * @param params the training parameters
   * @return the list containing the left and right window values
   */
  private static List<Integer> getWindowRange(TrainingParameters params) {
    List<Integer> windowRange = new ArrayList<Integer>();
    String windowParam = Flags.getWindow(params);
    String[] windowArray = windowParam.split("[ :-]");
    if (windowArray.length == 2) {
      windowRange.add(Integer.parseInt(windowArray[0]));
      windowRange.add(Integer.parseInt(windowArray[1]));
    }
    return windowRange;
  }
  
  /**
   * Set the character ngrams minimum and maximum values.
   * @param params the parameters file
   */
  public static void setNgramRange(TrainingParameters params) {
    if (minCharNgram == -1 || maxCharNgram == -1) {
      minCharNgram = getNgramRange(params).get(0);
      maxCharNgram = getNgramRange(params).get(1);
    }
  }

  /**
   * Get the range of the character ngram of current token.
   * @param params the training parameters
   * @return a list containing the initial and maximum ngram values
   */
  public static List<Integer> getNgramRange(TrainingParameters params) {
    List<Integer> ngramRange = new ArrayList<Integer>();
      String charNgramParam = Flags.getCharNgramFeaturesRange(params);
      String[] charngramArray = charNgramParam.split("[ :-]");
      if (charngramArray.length == 2) {
        ngramRange.add(Integer.parseInt(charngramArray[0]));
        ngramRange.add(Integer.parseInt(charngramArray[1]));

      }
    return ngramRange;
  }
  
}
