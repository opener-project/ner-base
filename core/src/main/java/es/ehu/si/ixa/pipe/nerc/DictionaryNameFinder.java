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
import java.util.LinkedList;
import java.util.List;

import es.ehu.si.ixa.pipe.nerc.dict.Dictionary;


import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;


/**
 * It detects named entities by dictionary look-up.
 * @author ragerri
 *
 */
public class DictionaryNameFinder implements NameFinder {

  /**
   * The name factory to create Name objects.
   */
  private NameFactory nameFactory;
  /**
   * The dictionary to find the names.
   */
  private Dictionary dictionary;
  /**
   * Debugging switch.
   */
  private final boolean debug = false;

 
  public DictionaryNameFinder(final Dictionary aDictionary) {
    this.dictionary = aDictionary;
  }

  public DictionaryNameFinder(final Dictionary aDictionary,
      final NameFactory aNameFactory) {
    this.dictionary = aDictionary;
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
   * Detects Named Entities against a {@link Dictionary} ignoring case.
   * 
   * @param tokens
   *          the tokenized sentence
   * @return spans of the Named Entities
   */
  public final List<Span> nercToSpans(final String[] tokens) {
    List<Span> namesFound = new LinkedList<Span>();

    for (int offsetFrom = 0; offsetFrom < tokens.length; offsetFrom++) {
      Span nameFound = null;
      String tokensSearching[] = new String[] {};

      for (int offsetTo = offsetFrom; offsetTo < tokens.length; offsetTo++) {
        int lengthSearching = offsetTo - offsetFrom + 1;

        if (lengthSearching > dictionary.getMaxTokenCount()) {
          break;
        } else {
          tokensSearching = new String[lengthSearching];
          System.arraycopy(tokens, offsetFrom, tokensSearching, 0,
              lengthSearching);

          String entryForSearch = StringUtils.getStringFromTokens(tokensSearching).toLowerCase();
          
          if (dictionary.getDict().containsKey(entryForSearch)) {
            nameFound = new Span(offsetFrom, offsetTo + 1, dictionary.getDict().get(entryForSearch));
          }
        }
      }

      if (nameFound != null) {
        namesFound.add(nameFound);
        // skip over the found tokens for the next search
        offsetFrom += (nameFound.length() - 1);
      }
    }
    return namesFound;
  }
  
  /**
   * Detects Named Entities in a {@link Dictionary} taking case into account.
   * 
   * @param tokens
   *          the tokenized sentence
   * @return spans of the Named Entities
   */
  public final List<Span> nercToSpansExact(final String[] tokens) {
    List<Span> namesFound = new LinkedList<Span>();

    for (int offsetFrom = 0; offsetFrom < tokens.length; offsetFrom++) {
      Span nameFound = null;
      String tokensSearching[] = new String[] {};

      for (int offsetTo = offsetFrom; offsetTo < tokens.length; offsetTo++) {
        int lengthSearching = offsetTo - offsetFrom + 1;

        if (lengthSearching > dictionary.getMaxTokenCount()) {
          break;
        } else {
          tokensSearching = new String[lengthSearching];
          System.arraycopy(tokens, offsetFrom, tokensSearching, 0,
              lengthSearching);

          String entryForSearch = StringUtils.getStringFromTokens(tokensSearching);
          
          if (dictionary.getDict().containsKey(entryForSearch)) {
            nameFound = new Span(offsetFrom, offsetTo + 1, dictionary.getDict().get(entryForSearch));
          }
        }
      }

      if (nameFound != null) {
        namesFound.add(nameFound);
        // skip over the found tokens for the next search
        offsetFrom += (nameFound.length() - 1);
      }
    }
    return namesFound;
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
