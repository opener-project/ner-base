/*
 *  Copyright 2014 Rodrigo Agerri

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

import java.util.List;

import eus.ixa.ixa.pipe.nerc.Name;

import opennlp.tools.util.Span;

/**
 *
 *
 * @author ragerri
 *
 */
public interface NameFinder {
  /**
   * Generates {@link Name} objects for the given sequence, typically a
   * sentence.
   *
   * @param tokens
   *          an array of the tokens or words
   * @return a list of names
   */
  List<Name> getNames(String[] tokens);

  /**
   * This method receives as input an array of tokenized text and returns the
   * {@link Span}s of the detected and classified Named Entities.
   *
   * @param tokens
   *          an array of tokenized text
   * @return an list of Spans of Named Entities
   */
  Span[] nercToSpans(String[] tokens);

  /**
   * Create a list of {@link Name} objects from spans and tokens.
   *
   * @param neSpans an array of Name Spans
   * @param tokens an array of tokens, usually a sentence
   * @return a list of name objects
   */
  List<Name> getNamesFromSpans(Span[] neSpans, String[] tokens);

  /**
   * Forget all adaptive data which was collected during previous calls to one
   * of the find methods.
   *
   * This method is typically called at the end of a document.
   */
  void clearAdaptiveData();

}
