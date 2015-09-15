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

import java.io.IOException;

import opennlp.tools.namefind.BilouCodec;
import opennlp.tools.namefind.BioCodec;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.NameSampleTypeFilter;
import opennlp.tools.namefind.TokenNameFinderEvaluator;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import eus.ixa.ixa.pipe.nerc.formats.CoNLL02Format;
import eus.ixa.ixa.pipe.nerc.formats.CoNLL03Format;

/**
 * Abstract class for common training functionalities. Every other trainer class
 * needs to extend this class.
 * @author ragerri
 * @version 2015-02-25
 */
public abstract class AbstractTrainer implements Trainer {
  
  /**
   * The language.
   */
  private String lang;
  /**
   * String holding the training data.
   */
  private String trainData;
  /**
   * String holding the testData.
   */
  private String testData;
  /**
   * ObjectStream of the training data.
   */
  private ObjectStream<NameSample> trainSamples;
  /**
   * ObjectStream of the test data.
   */
  private ObjectStream<NameSample> testSamples;
  /**
   * The corpus format: conll02, conll03 and opennlp.
   */
  private String corpusFormat;
  /**
   * beamsize value needs to be established in any class extending this one.
   */
  private int beamSize;
  /**
   * The sequence encoding of the named entity spans, e.g., BIO or BILOU.
   */
  private String sequenceCodec;
  /**
   * Reset the adaptive features every newline in the training data.
   */
  private String clearTrainingFeatures;
  /**
   * Reset the adaptive features every newline in the testing data.
   */
  private String clearEvaluationFeatures;
  /**
   * features needs to be implemented by any class extending this one.
   */
  private TokenNameFinderFactory nameClassifierFactory;

  /**
   * Construct a trainer with training and test data, and with options for
   * language, beamsize for decoding, sequence codec and corpus format (conll or opennlp).
   * @param params the training parameters
   * @throws IOException
   *           io exception
   */
  public AbstractTrainer(final TrainingParameters params) throws IOException {
    
    this.lang = Flags.getLanguage(params);
    this.clearTrainingFeatures = Flags.getClearTrainingFeatures(params);
    this.clearEvaluationFeatures = Flags.getClearEvaluationFeatures(params);
    this.corpusFormat = Flags.getCorpusFormat(params);
    this.trainData = params.getSettings().get("TrainSet");
    this.testData = params.getSettings().get("TestSet");
    trainSamples = getNameStream(trainData, clearTrainingFeatures, corpusFormat);
    testSamples = getNameStream(testData, clearEvaluationFeatures, corpusFormat);
    this.beamSize = Flags.getBeamsize(params);
    this.sequenceCodec = Flags.getSequenceCodec(params);
    if (params.getSettings().get("Types") != null) {
      String netypes = params.getSettings().get("Types");
      String[] neTypes = netypes.split(",");
      trainSamples = new NameSampleTypeFilter(neTypes, trainSamples);
      testSamples = new NameSampleTypeFilter(neTypes, testSamples);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * es.ehu.si.ixa.pipe.nerc.train.Trainer#train(opennlp.tools.util
   * .TrainingParameters)
   */
  public final TokenNameFinderModel train(final TrainingParameters params) {
    if (getNameClassifierFactory() == null) {
      throw new IllegalStateException(
          "Classes derived from AbstractNameFinderTrainer must create and fill the AdaptiveFeatureGenerator features!");
    }
    TokenNameFinderModel trainedModel = null;
    TokenNameFinderEvaluator nerEvaluator = null;
    try {
      trainedModel = NameFinderME.train(lang, null, trainSamples, params,
          nameClassifierFactory);
      NameFinderME nerTagger = new NameFinderME(trainedModel);
      nerEvaluator = new TokenNameFinderEvaluator(nerTagger);
      nerEvaluator.evaluate(testSamples);
    } catch (IOException e) {
      System.err.println("IO error while loading traing and test sets!");
      e.printStackTrace();
      System.exit(1);
    }
    System.out.println("Final Result: \n" + nerEvaluator.getFMeasure());
    return trainedModel;
  }

  /**
   * Getting the stream with the right corpus format.
   * @param inputData
   *          the input data
   * @param clearFeatures clear the features
   * @param aCorpusFormat
   *          the corpus format
   * @return the stream from the several corpus formats
   * @throws IOException
   *           the io exception
   */
  public static ObjectStream<NameSample> getNameStream(final String inputData,
      final String clearFeatures, final String aCorpusFormat) throws IOException {
    ObjectStream<NameSample> samples = null;
    if (aCorpusFormat.equalsIgnoreCase("conll03")) {
      ObjectStream<String> nameStream = InputOutputUtils.readFileIntoMarkableStreamFactory(inputData);
      samples = new CoNLL03Format(clearFeatures, nameStream);
    } else if (aCorpusFormat.equalsIgnoreCase("conll02")) {
      ObjectStream<String> nameStream = InputOutputUtils.readFileIntoMarkableStreamFactory(inputData);
      samples = new CoNLL02Format(clearFeatures, nameStream);
    } else if (aCorpusFormat.equalsIgnoreCase("opennlp")) {
      ObjectStream<String> nameStream = InputOutputUtils.readFileIntoMarkableStreamFactory(inputData);
      samples = new NameSampleDataStream(nameStream);
    } else {
      System.err.println("Test set corpus format not valid!!");
      System.exit(1);
    }
    return samples;
  }
 
  /**
   * Get the features which are implemented in each of the trainers extending
   * this class.
   * @return the features
   */
  public final TokenNameFinderFactory getNameClassifierFactory() {
    return nameClassifierFactory;
  }
  
  public final TokenNameFinderFactory setNameClassifierFactory(TokenNameFinderFactory tokenNameFinderFactory) {
    this.nameClassifierFactory = tokenNameFinderFactory;
    return nameClassifierFactory;
  }
  
  /**
   * Get the language.
   * @return the language
   */
  public final String getLanguage() {
    return lang;
  }

  /**
   * Set the language.
   * @param aLang
   *          the language
   */
  public final void setLanguage(final String aLang) {
    this.lang = aLang;
  }
  
  /**
   * Get the Sequence codec.
   * @return the sequence codec
   */
  public final String getSequenceCodec() {
    String seqCodec = null;
    if ("BIO".equals(sequenceCodec)) {
      seqCodec = BioCodec.class.getName();
    }
    else if ("BILOU".equals(sequenceCodec)) {
      seqCodec = BilouCodec.class.getName();
    }
    return seqCodec;
  }
  
  /**
   * Set the sequence codec.
   * @param aSeqCodec the sequence codec to be set
   */
  public final void setSequenceCodec(final String aSeqCodec) {
    this.sequenceCodec = aSeqCodec;
  }
  
  public final int getBeamSize() {
    return beamSize;
  }

}
