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

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import eus.ixa.ixa.pipe.nerc.lexer.NumericNameLexer;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;

public class NumericNameFinder implements NameFinder {
  
  private NumericNameLexer numericLexer;
  private NameFactory nameFactory;
  
  public NumericNameFinder(BufferedReader breader, NameFactory aNameFactory) {
    this.nameFactory = aNameFactory;
    numericLexer = new NumericNameLexer(breader, aNameFactory);
  }

  public List<Name> getNames(String[] tokens) {
    Span[] origSpans = nercToSpans(tokens);
    Span[] neSpans = NameFinderME.dropOverlappingSpans(origSpans);
    List<Name> names = getNamesFromSpans(neSpans, tokens);
    return names;
  }

  public Span[] nercToSpans(final String[] tokens) {
    List<Span> neSpans = new ArrayList<Span>();
    List<Name> flexNameList = numericLexer.getNumericNames();
    for (Name name : flexNameList) {
      //System.err.println("numeric name: " + name.value());
      List<Integer> neIds = StringUtils.exactTokenFinderIgnoreCase(name.value(), tokens);
      for (int i = 0; i < neIds.size(); i += 2) {
        Span neSpan = new Span(neIds.get(i), neIds.get(i+1), name.getType());
        neSpans.add(neSpan);
      }
    }
    return neSpans.toArray(new Span[neSpans.size()]);
  }

  public List<Name> getNamesFromSpans(Span[] neSpans, String[] tokens) {
    List<Name> names = new ArrayList<Name>();
    for (Span neSpan : neSpans) {
      String nameString = StringUtils.getStringFromSpan(neSpan, tokens);
      String neType = neSpan.getType();
      Name name = nameFactory.createName(nameString, neType, neSpan);
      names.add(name);
    }
    return names;
  }

  public void clearAdaptiveData() {
    // nothing to clear
    
  }

}
