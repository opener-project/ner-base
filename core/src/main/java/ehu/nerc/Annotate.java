/*
 *  Copyright 2013 Rodrigo Agerri

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

package ehu.nerc;


import ixa.kaflib.KAFDocument;
import ixa.kaflib.Term;
import ixa.kaflib.WF;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;


/**
 * @author ragerri
 * 
 */
public class Annotate {

  private NERC nameFinder;

  public Annotate(String cmdOption) {
    Models modelRetriever = new Models();
    InputStream nerModel = modelRetriever.getNERModel(cmdOption);
    nameFinder = new NERC(nerModel);
  }


  /**
   * 
   * It takes a NE span indexes and the tokens in a sentence and produces the
   * string to which the NE span corresponds to. This function is used to get
   * the NE textual representation from a Span. The NE string will then be added
   * to the span element in the <entities> layer of the KAF document.
   * 
   * @param Span
   *          reducedSpan
   * @param String
   *          [] tokens
   * @return named entity string
   */
  private String getStringFromSpan(Span reducedSpan, String[] tokens) {
    StringBuilder sb = new StringBuilder();
    for (int si = reducedSpan.getStart(); si < reducedSpan.getEnd(); si++) {
      sb.append(tokens[si]).append(" ");
    }
    String neString = sb.toString();
    return neString;
  }

  /**
   * This method uses the Apache OpenNLP to tag Named Entities.
   * 
   * It gets a Map<SentenceId, tokens> from the input KAF document and iterates
   * over the tokens of each sentence to annotate Named Entities.
   * 
   * It also reads <wf>, <terms> elements from the input KAF document and fills
   * the KAF object with those elements plus the annotated Named Entities.
   * 
   * @param LinkedHashMap
   *          <String,List<String>
   * @param List
   *          <WF> termList
   * @param KAF
   *          object. This object is used to take the output data and convert it
   *          to KAF.
   * 
   * @return JDOM KAF document containing <wf>,<terms> and <entities> elements.
 * @throws JDOMException 
   */

  public void annotateNEsToKAF(KAFDocument kaf) throws IOException {
	  
	  List<List<WF>> sentences = kaf.getSentences();
	  for (List<WF> sentence : sentences) {
	    String [] tokens = new String[sentence.size()];
	    String[] tokenIds = new String[sentence.size()];
	    for (int i=0; i < sentence.size(); i++) {
	      tokens[i] = sentence.get(i).getForm();
	      tokenIds[i] = sentence.get(i).getId();
	    }
      // annotate Named Entities 
      Span nameSpans[] = nameFinder.nercAnnotate(tokens);
      Span reducedSpans[] = NameFinderME.dropOverlappingSpans(nameSpans);
      
      // create KAF 
      for (int i=0; i < reducedSpans.length; i++) { 
        String type = reducedSpans[i].getType();
        Integer start_index = reducedSpans[i].getStart();
        Integer end_index = reducedSpans[i].getEnd();
        List<Term> nameTerms = kaf.getTermsFromWFs(Arrays.asList(Arrays.copyOfRange(tokenIds, start_index, end_index)));
        List<List<Term>> references = new ArrayList<List<Term>>();
        references.add(nameTerms);
        kaf.createEntity(type, references);
    }
      
      
      

    }
  }
  
}
