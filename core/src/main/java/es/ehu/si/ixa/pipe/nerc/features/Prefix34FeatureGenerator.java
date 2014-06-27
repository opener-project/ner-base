package es.ehu.si.ixa.pipe.nerc.features;


import java.util.List;

import opennlp.tools.util.featuregen.FeatureGeneratorAdapter;

	public class Prefix34FeatureGenerator extends FeatureGeneratorAdapter {

	  private static final int PREFIX_LENGTH = 4;
	  
	  public static String[] getPrefixes(String lex) {
	    String[] prefs = new String[PREFIX_LENGTH];
	    for (int li = 3, ll = PREFIX_LENGTH; li < ll; li++) {
	      prefs[li] = lex.substring(0, Math.min(li + 1, lex.length()));
	    }
	    return prefs;
	  }
	  
	  public void createFeatures(List<String> features, String[] tokens, int index,
	      String[] previousOutcomes) {
	    String[] prefs = Prefix34FeatureGenerator.getPrefixes(tokens[index]);
	    for (String pref : prefs) {
	      features.add("pre=" + pref);
	    }
	  }
	}

	

