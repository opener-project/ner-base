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

package eus.ixa.ixa.pipe.nerc;

import ixa.kaflib.KAFDocument;
import ixa.kaflib.Opinion;
import ixa.kaflib.Term;
import ixa.kaflib.WF;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.Span;

/**
 * Annotation class for Opinion Target Extraction (OTE).
 * 
 * @author ragerri
 * @version 2015-04-29
 * 
 */
public class OpinionTargetExtractor {

  /**
   * The factory to construct Name objects.
   */
  private NameFactory nameFactory;
  /**
   * The NameFinder to do the opinion target extraction.
   */
  private StatisticalNameFinder oteExtractor;
  /**
   * Clear features after every sentence or when a -DOCSTART- mark appears.
   */
  private String clearFeatures;

  
  public OpinionTargetExtractor(final Properties properties) throws IOException {

    this.clearFeatures = properties.getProperty("clearFeatures");
    nameFactory = new NameFactory();
    oteExtractor = new StatisticalNameFinder(properties, nameFactory);
  }
  
  /**
   * Extract Opinion Targets.
   * @param kaf the KAFDocument
   * @throws IOException if io errors
   */
  public final void extractOpinionTargets(final KAFDocument kaf) throws IOException {

    List<List<WF>> sentences = kaf.getSentences();
    for (List<WF> sentence : sentences) {
      //process each sentence
      String[] tokens = new String[sentence.size()];
      String[] tokenIds = new String[sentence.size()];
      for (int i = 0; i < sentence.size(); i++) {
        tokens[i] = sentence.get(i).getForm();
        tokenIds[i] = sentence.get(i).getId();
      }
      if (clearFeatures.equalsIgnoreCase("docstart") && tokens[0].startsWith("-DOCSTART-")) {
        oteExtractor.clearAdaptiveData();
      }
      List<Name> names = oteExtractor.getNames(tokens);
      for (Name name : names) {
        Integer startIndex = name.getSpan().getStart();
        Integer endIndex = name.getSpan().getEnd();
        List<Term> nameTerms = kaf.getTermsFromWFs(Arrays.asList(Arrays
            .copyOfRange(tokenIds, startIndex, endIndex)));
        ixa.kaflib.Span<Term> neSpan = KAFDocument.newTermSpan(nameTerms);
        Opinion opinion = kaf.newOpinion();
        opinion.createOpinionTarget(neSpan);
      }
      if (clearFeatures.equalsIgnoreCase("yes")) {
        oteExtractor.clearAdaptiveData();
      }
    }
    oteExtractor.clearAdaptiveData();
  }

  /**
   * Output annotation as NAF.
   * 
   * @param kaf
   *          the naf document
   * @return the string containing the naf document
   */
  public final String annotateOTEsToKAF(KAFDocument kaf) {
    return kaf.toString();
  }
  
  /**
   * Output annotation in OpenNLP format.
   * 
   * @param kaf
   *          the naf document
   * @return the string containing the annotated document
   */
  public final String annotateOTEsToOpenNLP(KAFDocument kaf) {
    StringBuilder sb = new StringBuilder();
    List<List<WF>> sentences = kaf.getSentences();
    for (List<WF> sentence : sentences) {
      String[] tokens = new String[sentence.size()];
      String[] tokenIds = new String[sentence.size()];
      for (int i = 0; i < sentence.size(); i++) {
        tokens[i] = sentence.get(i).getForm();
        tokenIds[i] = sentence.get(i).getId();
      }
      if (clearFeatures.equalsIgnoreCase("docstart") && tokens[0].startsWith("-DOCSTART-")) {
        oteExtractor.clearAdaptiveData();
      }
      Span[] statSpans = oteExtractor.nercToSpans(tokens);
      boolean isClearAdaptiveData = false;
      if (clearFeatures.equalsIgnoreCase("yes")) {
        isClearAdaptiveData = true;
      }
      Span[] allSpansArray = NameFinderME.dropOverlappingSpans(statSpans);
      NameSample nameSample = new NameSample(tokens, allSpansArray, isClearAdaptiveData);
      sb.append(nameSample.toString()).append("\n");
    }
    oteExtractor.clearAdaptiveData();
    return sb.toString();
  }

}
