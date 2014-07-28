/*
 *Copyright 2014 Rodrigo Agerri

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

package es.ehu.si.ixa.pipe.nerc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;
import es.ehu.si.ixa.pipe.nerc.dict.Dictionaries;
import es.ehu.si.ixa.pipe.nerc.train.BaselineNameFinderTrainer;
import es.ehu.si.ixa.pipe.nerc.train.DictNameFinderTrainer;
import es.ehu.si.ixa.pipe.nerc.train.NameFinderTrainer;
import es.ehu.si.ixa.pipe.nerc.train.DefaultNameFinderTrainer;

/**
 * Named Entity Recognition module based on Apache OpenNLP Machine Learning API.
 *
 * @author ragerri
 * @version 2014-04-04
 *
 */

public class StatisticalNameFinder implements NameFinder {

  /**
   * The models to use for every language. The keys of the hash are the
   * language codes, the values the models.
   */
  private static ConcurrentHashMap<String, TokenNameFinderModel> nercModels =
      new ConcurrentHashMap<String, TokenNameFinderModel>();

  /**
   * The name finder.
   */
  private NameFinderME nameFinder;
  /**
   * The name factory.
   */
  private NameFactory nameFactory;
  /**
   * The trainer called to obtain the appropriate features.
   */
  private NameFinderTrainer nameFinderTrainer;

  /**
   * Construct a StatisticalNameFinder without name factory.
   *
   * @param lang
   *          the language
   * @param model
   *          the name of the model to be used
   * @param features
   *          the features
   * @param beamsize
   *          the beam size decoding
   */
  public StatisticalNameFinder(final String lang, final String model,
      final String features, final int beamsize) {

    TokenNameFinderModel nerModel = loadModel(lang, model);
    nameFinderTrainer = getNameFinderTrainer(features, beamsize);
    nameFinder = new NameFinderME(nerModel,
        nameFinderTrainer.createFeatureGenerator(), beamsize);
  }

  /**
   * Construct a StatisticalNameFinder without name factory and with
   * default beam size.
   *
   * @param lang the language
   * @param model the model
   * @param features the features
   */
  public StatisticalNameFinder(final String lang, final String model, final String features) {
    this(lang, model, features, CLI.DEFAULT_BEAM_SIZE);
  }

  /**
   * Construct a StatisticalNameFinder without name factory but with dictionary features.
   *
   * @param lang
   *          the language
   * @param model
   *          the name of the model to be used
   * @param features
   *          the features
   * @param beamsize
   *          the beam size decoding
   * @param dictPath the directory containing the dictionaries
   */
  public StatisticalNameFinder(final String lang, final String model,
      final String features, final int beamsize, final Dictionaries dictionaries) {

    TokenNameFinderModel nerModel = loadModel(lang, model);
    nameFinderTrainer = new DictNameFinderTrainer(dictionaries, beamsize);
    nameFinder = new NameFinderME(nerModel,
        nameFinderTrainer.createFeatureGenerator(), beamsize);
  }

  /**
   * Construct a StatisticalNameFinder without name factory and with
   * default beam size.
   *
   * @param lang the language
   * @param model the model
   * @param features the features
   */
  public StatisticalNameFinder(final String lang, final String model, final String features, final Dictionaries dictionaries) {
    this(lang, model, features, CLI.DEFAULT_BEAM_SIZE, dictionaries);
  }

  /**
   * Construct a StatisticalNameFinder specifying the language,
   * a name factory, the model, the features and the beam size for
   * decoding.
   *
   * @param lang the language
   * @param aNameFactory the name factory to construct Name objects
   * @param model the model
   * @param features the features
   * @param beamsize the beam size for decoding
   */
  public StatisticalNameFinder(final String lang, final NameFactory aNameFactory,
      final String model, final String features, final int beamsize) {

    this.nameFactory = aNameFactory;
    TokenNameFinderModel nerModel = loadModel(lang, model);
    nameFinderTrainer = getNameFinderTrainer(features, beamsize);
    nameFinder = new NameFinderME(nerModel,
        nameFinderTrainer.createFeatureGenerator(), beamsize);
  }


  /**
   * Construct a StatisticalNameFinder with name factory and
   * with default beam size.
   *
   * @param lang the language
   * @param aNameFactory the name factory
   * @param model the model
   * @param features the features
   */
  public StatisticalNameFinder(final String lang, final NameFactory aNameFactory,
      final String model, final String features) {
    this(lang, aNameFactory, model, features, CLI.DEFAULT_BEAM_SIZE);
  }

  /**
   * Construct a StatisticalNameFinder specifying the language,
   * a name factory, the model, the features and the beam size for
   * decoding.
   *
   * @param lang the language
   * @param aNameFactory the name factory to construct Name objects
   * @param model the model
   * @param features the features
   * @param beamsize the beam size for decoding
   * @param dictPath the path to the dictionaries
   */
  public StatisticalNameFinder(final String lang, final NameFactory aNameFactory,
      final String model, final String features, final int beamsize, final Dictionaries dictionaries) {

    this.nameFactory = aNameFactory;
    TokenNameFinderModel nerModel = loadModel(lang, model);
    nameFinderTrainer = new DictNameFinderTrainer(dictionaries, beamsize);
    nameFinder = new NameFinderME(nerModel,
        nameFinderTrainer.createFeatureGenerator(), beamsize);
  }


  /**
   * Construct a StatisticalNameFinder with name factory and
   * with default beam size.
   *
   * @param lang the language
   * @param aNameFactory the name factory
   * @param model the model
   * @param features the features
   */
  public StatisticalNameFinder(final String lang, final NameFactory aNameFactory,
      final String model, final String features, final Dictionaries dictionaries) {
    this(lang, aNameFactory, model, features, CLI.DEFAULT_BEAM_SIZE, dictionaries);
  }


  /**
   * Method to produce a list of the {@link Name} objects classified by the
   * probabilistic model.
   *
   * Takes an array of tokens, calls nercToSpans function for probabilistic NERC
   * and returns a List of {@link Name} objects containing the nameString, the
   * type and the {@link Span}
   *
   * @param tokens
   *          an array of tokenized text
   * @return a List of names
   */
  public final List<Name> getNames(final String[] tokens) {
    List<Span> origSpans = nercToSpans(tokens);
    Span[] neSpans = NameFinderME.dropOverlappingSpans(origSpans
        .toArray(new Span[origSpans.size()]));
    List<Name> names = getNamesFromSpans(neSpans, tokens);
    return names;
  }

  /**
   * This method receives as input an array of tokenized text and calls the
   * NameFinderME.find(tokens) to recognize and classify Named Entities. It
   * outputs the spans of the detected and classified Named Entities.
   *
   * From Apache OpenNLP documentation: "After every document clearAdaptiveData
   * must be called to clear the adaptive data in the feature generators. Not
   * calling clearAdaptiveData can lead to a sharp drop in the detection rate
   * after a few documents."
   *
   * @param tokens
   *          an array of tokenized text
   * @return an list of {@link Span}s of Named Entities
   */
  public final List<Span> nercToSpans(final String[] tokens) {
    Span[] annotatedText = nameFinder.find(tokens);
    clearAdaptiveData();
    List<Span> probSpans = new ArrayList<Span>(Arrays.asList(annotatedText));
    return probSpans;
  }

  /**
   * Creates a list of {@link Name} objects from spans and tokens.
   *
   * @param neSpans the named entity spans of a sentence
   * @param tokens the tokens in the sentence
   * @return a list of {@link Name} objects
   */
  public final List<Name> getNamesFromSpans(final Span[] neSpans, final String[] tokens) {
    List<Name> names = new ArrayList<Name>();
    for (Span neSpan : neSpans) {
      String nameString = StringUtils.getStringFromSpan(neSpan, tokens);
      String neType = neSpan.getType();
      Name name = nameFactory.createName(nameString, neType, neSpan);
      names.add(name);
    }
    return names;
  }

  /**
   * Forgets all adaptive data which was collected during previous calls to one
   * of the find methods. This method is typically called at the end of a
   * document.
   *
   * From Apache OpenNLP documentation: "After every document clearAdaptiveData
   * must be called to clear the adaptive data in the feature generators. Not
   * calling clearAdaptiveData can lead to a sharp drop in the detection rate
   * after a few documents."
   */
  public final void clearAdaptiveData() {
    nameFinder.clearAdaptiveData();
  }

  /**
   * Loads statically the probabilistic model. Every instance of this finder
   * will share the same model.
   *
   * @param lang the language
   * @param model the model to be loaded
   * @return the model as a {@link TokenNameFinder} object
   */
  public final TokenNameFinderModel loadModel(final String lang, final String model) {
    InputStream trainedModel = null;

    try {
      // Load the model if it's not there yet.
      if ( !nercModels.containsKey(lang) ) {
        if (model.equalsIgnoreCase("default")) {
          trainedModel = getDefaultModelStream(lang, model);
        } else {
          trainedModel = new FileInputStream(model);
        }

        nercModels.put(lang, new TokenNameFinderModel(trainedModel));
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (trainedModel != null) {
        try {
          trainedModel.close();
        } catch (IOException e) {
          System.err.println("Could not load model!");
        }
      }
    }

    return nercModels.get(lang);
  }

  /**
   * Method to back-off to a default model when no model is
   * chosen in the command line.
   *
   * @param lang the language
   * @param model the default value to load the baseline model
   * @return the input stream from a model
   */
  private InputStream getDefaultModelStream(final String lang, final String model) {
    InputStream trainedModelInputStream = null;
    if (lang.equalsIgnoreCase("de")) {
      trainedModelInputStream = getClass().getResourceAsStream("/de/de-nerc-perceptron-baseline-c0-b3-conll03-testa.bin");
    }
    if (lang.equalsIgnoreCase("en")) {
      trainedModelInputStream = getClass().getResourceAsStream("/en/en-nerc-perceptron-baseline-c0-b3-conll03-ontonotes-4.0-4-types.bin");
    }
    if (lang.equalsIgnoreCase("es")) {
      trainedModelInputStream = getClass().getResourceAsStream("/es/es-nerc-maxent-baseline-750-c4-b3-conll02-testa.bin");
    }
    if (lang.equalsIgnoreCase("fr")) {
      trainedModelInputStream = getClass().getResourceAsStream("/fr/fr-ner-pos.bin");
    }
    if (lang.equalsIgnoreCase("it")) {
      trainedModelInputStream = getClass().getResourceAsStream("/it/it-nerc-perceptron-baseline-c0-b3-evalita07.bin");
    }
    if (lang.equalsIgnoreCase("nl")) {
      trainedModelInputStream = getClass().getResourceAsStream("/nl/nl-nerc-perceptron-baseline-c0-b3-conll02-testa.bin");
    }
    return trainedModelInputStream;
  }

  /**
   * Instantiates a NameFinderTrainer with specific features and beam size.
   *
   * @param features the features
   * @param beamsize the beam size
   * @return an instance of a NameFinderTrainer
   */
  public final NameFinderTrainer getNameFinderTrainer(final String features, final int beamsize) {
    if (features.equalsIgnoreCase("baseline")) {
      nameFinderTrainer = new BaselineNameFinderTrainer(beamsize);
    } else if (features.equalsIgnoreCase("opennlp")) {
      nameFinderTrainer = new DefaultNameFinderTrainer(beamsize);
    }
    return nameFinderTrainer;
  }

}
