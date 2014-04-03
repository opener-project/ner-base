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

  public Annotate(String langCmdOption) {
//    Models modelRetriever = new Models();
//    InputStream nerModel = modelRetriever.getNERModelInputStream(cmdOption);
//    nameFinder = new NERC(nerModel);
	  nameFinder=new NERC(langCmdOption);
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
   * @param KAFDocument kaf  
   * @return KAF document containing <wf>,<terms> and <entities> elements.
 * 
   */

  public void annotateNEsToKAF(KAFDocument kaf) throws IOException {
    
	List<List<WF>> sentences = kaf.getSentences();
	for (List<WF> sentence : sentences) { 
		//get array of token forms from a list of WF objects
		String[] tokens = new String[sentence.size()];
		String[] tokenIds = new String[sentence.size()];
		
		for (int i = 0; i < sentence.size(); i++) { 
			tokens[i] = sentence.get(i).getForm();
			tokenIds[i] = sentence.get(i).getId();
		}
	  
      Span nameSpans[] = nameFinder.nercAnnotate(tokens);
      Span reducedSpans[] = NameFinderME.dropOverlappingSpans(nameSpans);
      
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
