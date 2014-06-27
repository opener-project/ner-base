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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.ehu.si.ixa.pipe.nerc.dict.Dictionaries;
import es.ehu.si.ixa.pipe.nerc.dict.Dictionary;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;

/**
 * Named Entity Recognition module based on {@link Dictionary} objects This
 * class provides the following functionalities:
 * 
 * <ol>
 * <li>string matching against of a string (typically tokens) against a
 * Dictionary containing names. This function is also used to implement
 * Dictionary based features in the training package.
 * <li>tag: Provided a Dictionary it tags only the names it matches against it
 * <li>post: This function checks for names in the Dictionary that have not been
 * detected by a {@link StatisticalNameFinder}; it also corrects the Name type
 * for those detected by a {@link StatisticalNameFinder} but also present in a
 * dictionary.
 * </ol>
 * 
 * @author ragerri 2014/03/14
 * 
 */

public class DictionariesNameFinder implements NameFinder {

  /**
   * The name factory to create Name objects.
   */
  private NameFactory nameFactory;
  /**
   * The dictionary to find the names.
   */
  private Dictionaries dictionaries;
  /**
   * Debugging switch.
   */
  private final boolean debug = false;

  /**
   * Construct a DictionaryNameFinder using one dictionary and one named entity
   * class.
   * 
   * @param aDict
   *          the dictionary
   * @param aType
   *          the named entity class
   */
  public DictionariesNameFinder(final Dictionaries aDictionaries) {
    this.dictionaries = aDictionaries;
  }

  /**
   * Construct a DictionaryNameFinder with a dictionary, a type and a name
   * factory.
   * 
   * @param aDict
   *          the dictionary
   * @param aType
   *          the named entity class
   * @param aNameFactory
   *          the factory
   */
  public DictionariesNameFinder(final Dictionaries aDictionaries,
      final NameFactory aNameFactory) {
    this.dictionaries = aDictionaries;
    this.nameFactory = aNameFactory;
  }

  /**
   * {@link Dictionary} based Named Entity Detection and Classification.
   * 
   * @param tokens
   *          the tokenized sentence
   * @return a list of detected {@link Name} objects
   */
  public final List<Name> getNames(final String[] tokens) {

    List<Span> origSpans = nercToSpans(tokens);
    Span[] neSpans = NameFinderME.dropOverlappingSpans(origSpans
        .toArray(new Span[origSpans.size()]));
    List<Name> names = getNamesFromSpans(neSpans, tokens);
    return names;
  }

  /**
   * Detects Named Entities in a {@link Dictionary} by NE type ignoring case.
   * 
   * @param tokens
   *          the tokenized sentence
   * @return spans of the Named Entities
   */
  public final List<Span> nercToSpans(final String[] tokens) {
    List<Span> neSpans = new ArrayList<Span>();
    for (Dictionary neDict : dictionaries.getIgnoreCaseDictionaries()) {
      for (Map.Entry<String, String> neEntry : neDict.getDict().entrySet()) {
        String neForm = neEntry.getKey();
        String neType = neEntry.getValue();
        List<Integer> neIds = StringUtils.exactTokenFinderIgnoreCase(neForm,
            tokens);
        if (!neIds.isEmpty()) {
          Span neSpan = new Span(neIds.get(0), neIds.get(1), neType);
          if (debug) {
            System.err.println(neSpans.toString());
          }
          neSpans.add(neSpan);
        }
      }
    }

    return neSpans;
  }

  /**
   * Detects Named Entities in a {@link Dictionary} by NE type This method is
   * case sensitive.
   * 
   * @param tokens
   *          the tokenized sentence
   * @return spans of the Named Entities all
   */
  public final List<Span> nercToSpansExact(final String[] tokens) {
    List<Span> neSpans = new ArrayList<Span>();
    for (Dictionary neDict : dictionaries.getDictionaries()) {
      for (Map.Entry<String, String> neEntry : neDict.getDict().entrySet()) {
        String neForm = neEntry.getKey();
        String neType = neEntry.getValue();
        List<Integer> neIds = StringUtils.exactTokenFinder(neForm,
            tokens);
        if (!neIds.isEmpty()) {
          Span neSpan = new Span(neIds.get(0), neIds.get(1), neType);
          if (debug) {
            System.err.println(neSpans.toString());
          }
          neSpans.add(neSpan);
        }
      }
    }
    return neSpans;
  }

  /**
   * Creates a list of {@link Name} objects from spans and tokens.
   * 
   * @param neSpans
   *          the spans of the entities in the sentence
   * @param tokens
   *          the tokenized sentence
   * @return a list of {@link Name} objects
   */
  public final List<Name> getNamesFromSpans(final Span[] neSpans,
      final String[] tokens) {
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
   * Clear the adaptiveData for each document.
   */
  public void clearAdaptiveData() {
    // nothing to clear
  }

}
