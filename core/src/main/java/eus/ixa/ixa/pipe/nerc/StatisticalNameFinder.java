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

package eus.ixa.ixa.pipe.nerc;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

/**
 * Statistical Sequence Labelling based on Apache OpenNLP Machine Learning API.
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
   * Construct a probabilistic name finder specifying lang, model and beamsize.
   * @param props the properties to be loaded
   */
  public StatisticalNameFinder(final Properties props) {
    String lang = props.getProperty("language");
    String model = props.getProperty("model");
    TokenNameFinderModel nerModel = loadModel(lang, model);
    nameFinder = new NameFinderME(nerModel);
  }

  /**
   * Construct a StatisticalNameFinder specifying the language,
   * a name factory, the model, the features and the beam size for
   * decoding.
   *
   * @param props the properties
   * @param aNameFactory the name factory to construct Name objects
   */
  public StatisticalNameFinder(final Properties props, final NameFactory aNameFactory) {

    String lang = props.getProperty("language");
    String model = props.getProperty("model");
    this.nameFactory = aNameFactory;
    TokenNameFinderModel nerModel = loadModel(lang, model);
    nameFinder = new NameFinderME(nerModel);
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
    Span[] origSpans = nercToSpans(tokens);
    Span[] neSpans = NameFinderME.dropOverlappingSpans(origSpans);
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
  public final Span[] nercToSpans(final String[] tokens) {
    Span[] annotatedText = nameFinder.find(tokens);
    List<Span> probSpans = new ArrayList<Span>(Arrays.asList(annotatedText));
    return probSpans.toArray(new Span[probSpans.size()]);
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
  private final TokenNameFinderModel loadModel(final String lang, final String model) {
    long lStartTime = new Date().getTime();
    try {
      synchronized(nercModels) {
        if (!nercModels.containsKey(lang)) {
          nercModels.put(lang, new TokenNameFinderModel(new FileInputStream(model)));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    long lEndTime = new Date().getTime();
    long difference = lEndTime - lStartTime;

    return nercModels.get(lang);
  }
}
