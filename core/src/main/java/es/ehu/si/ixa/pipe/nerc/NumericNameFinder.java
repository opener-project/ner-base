package es.ehu.si.ixa.pipe.nerc;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import es.ehu.si.ixa.pipe.nerc.lexer.NumericNameLexer;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;

public class NumericNameFinder implements NameFinder {
  
  private NumericNameLexer<Name> numericLexer;
  private NameFactory nameFactory;
  
  public NumericNameFinder(BufferedReader breader, NameFactory aNameFactory) {
    this.nameFactory = aNameFactory;
    numericLexer = new NumericNameLexer<Name>(breader, aNameFactory);
  }

  public List<Name> getNames(String[] tokens) {
    List<Span> origSpans = nercToSpans(tokens);
    Span[] neSpans = NameFinderME.dropOverlappingSpans(origSpans
        .toArray(new Span[origSpans.size()]));
    List<Name> names = getNamesFromSpans(neSpans, tokens);
    return names;
  }

  public List<Span> nercToSpans(final String[] tokens) {
    List<Span> neSpans = new ArrayList<Span>();
    List<Name> flexNameList = numericLexer.nameLex();
    for (Name name : flexNameList) {
      //System.err.println("numeric name: " + name.value());
      List<Integer> neIds = StringUtils.exactTokenFinderIgnoreCase(name.value(), tokens);
      if (!neIds.isEmpty()) {
        Span neSpan = new Span(neIds.get(0), neIds.get(1), name.getType());
        neSpans.add(neSpan);
      }
    }
    return neSpans;
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
