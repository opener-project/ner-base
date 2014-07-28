package org.vicomtech.opennlp.pos;

public class TagsetMappings {

	public static enum TagSet {
		NONE, FTB, ANCORA, LEMMATAGS
	}
	
	protected static enum KafTag{
		VERB("V"),
		COMMON_NOUN("N"),
		PROPER_NOUN("R"),
		ADJECTIVE("G"),
		ADVERB("A"),
		DETERMINER("D"),
		PREPOSITION("P"),
		PRONOUN("Q"),
		CONJUNCTION("C"),
		OTHER("O");
		
		private String tagText;
		private KafTag(String tagText){
			this.tagText=tagText;
		}
		
		protected String getTagText(){
			return this.tagText;
		}
		
		public String toString(){
			return this.tagText;
		}
	}
	
	public static TagSet getTagFormat(String tagSet) {
		if (tagSet.equalsIgnoreCase(TagSet.NONE.toString())) {
			return TagSet.NONE;
		}
		else if (tagSet.equalsIgnoreCase(TagSet.FTB.toString())) {
			return TagSet.FTB;
		}
		else if (tagSet.equalsIgnoreCase(TagSet.ANCORA.toString())) {
			return TagSet.ANCORA;
		}
		else if (tagSet.equalsIgnoreCase(TagSet.LEMMATAGS.toString())) {
			return TagSet.LEMMATAGS;
		}
		else {
			return TagSet.NONE;
		}
	}
	
	protected static void convertPostags(String[] postags, TagSet tagSet) {
		switch (tagSet) {
		case FTB :
			for (int i=0; i<postags.length; i++) {
				String ftbTag = postags[i];
				String kafTag = convertFromFtbToKaf(ftbTag);
				postags[i] = kafTag;
			}
			break;
		case ANCORA :
			for (int i=0; i<postags.length; i++) {
				String ancoraTag = postags[i];
				String kafTag = convertFromAnCoraToKaf(ancoraTag);
				postags[i] = kafTag;
			}
			break;
		case LEMMATAGS:
			for (int i=0; i<postags.length; i++) {
				String lemmaTag = postags[i];
				String kafTag = convertFromLemmatagsToKaf(lemmaTag);
				postags[i] = kafTag;
			}
			break;
		case NONE:
			break;
		}
	}
	
	protected static String convertFromFtbToKaf(String frenchTreebankTag){
		KafTag kafTag=KafTag.OTHER;
		if(frenchTreebankTag.startsWith("V")){
			kafTag=KafTag.VERB;
		}else if(frenchTreebankTag.startsWith("NC")){
			kafTag=KafTag.COMMON_NOUN;
		}else if(frenchTreebankTag.startsWith("N")){
			kafTag=KafTag.COMMON_NOUN;
		}else if(frenchTreebankTag.startsWith("NP")){
			kafTag=KafTag.PROPER_NOUN;
		}else if(frenchTreebankTag.startsWith("A")){
			kafTag=KafTag.ADJECTIVE;
		}else if(frenchTreebankTag.startsWith("Adv")){
			kafTag=KafTag.ADVERB;
		}else if(frenchTreebankTag.startsWith("ADV")){
			kafTag=KafTag.ADVERB;
		}else if(frenchTreebankTag.startsWith("D")){
			kafTag=KafTag.DETERMINER;
		}else if(frenchTreebankTag.startsWith("P")){
			kafTag=KafTag.PREPOSITION;
		}else if(frenchTreebankTag.startsWith("PRO")){
			kafTag=KafTag.PRONOUN;
		}else if(frenchTreebankTag.startsWith("CL")){ //pronom clitique
			kafTag=KafTag.PRONOUN;
		}else if(frenchTreebankTag.startsWith("C")){
			kafTag=KafTag.CONJUNCTION;
		}else if(frenchTreebankTag.startsWith("CC")){
			kafTag=KafTag.CONJUNCTION;
		}else if(frenchTreebankTag.startsWith("CS")){
			kafTag=KafTag.CONJUNCTION;
		}
	//	System.out.println("(ftb-to-kaf) Entering "+frenchTreebankTag+", output "+kafTag.toString());
		return kafTag.toString();
	}
	
	protected static String convertFromAnCoraToKaf(String ancoraTag){
		KafTag kafTag=KafTag.OTHER;
		if(ancoraTag.startsWith("v")){
			kafTag=KafTag.VERB;
		}else if(ancoraTag.startsWith("nc")){
			kafTag=KafTag.COMMON_NOUN;
		}else if(ancoraTag.startsWith("np")){
			kafTag=KafTag.PROPER_NOUN;
		}else if(ancoraTag.startsWith("a")){
			kafTag=KafTag.ADJECTIVE;
		}else if(ancoraTag.startsWith("r")){
			kafTag=KafTag.ADVERB;
		}else if(ancoraTag.startsWith("d")){
			kafTag=KafTag.DETERMINER;
		}else if(ancoraTag.startsWith("s")){
			kafTag=KafTag.PREPOSITION;
		}else if(ancoraTag.startsWith("p")){
			kafTag=KafTag.PRONOUN;
		}else if(ancoraTag.startsWith("c")){
			kafTag=KafTag.CONJUNCTION;
		}
		return kafTag.toString();
	}
	
	protected static String convertFromLemmatagsToKaf(String lemmaTag){
		KafTag kafTag=KafTag.OTHER;
		if(lemmaTag.equalsIgnoreCase("V")){
			kafTag=KafTag.VERB;
		}else if(lemmaTag.equalsIgnoreCase("NC")){
			kafTag=KafTag.COMMON_NOUN;
		}else if(lemmaTag.equalsIgnoreCase("NP")){
			kafTag=KafTag.PROPER_NOUN;
		}else if(lemmaTag.equalsIgnoreCase("adj")){
			kafTag=KafTag.ADJECTIVE;
		}else if(lemmaTag.equalsIgnoreCase("adv")){
			kafTag=KafTag.ADVERB;
		}else if(lemmaTag.equalsIgnoreCase("det")){
			kafTag=KafTag.DETERMINER;
		}else if(lemmaTag.equalsIgnoreCase("prep")){
			kafTag=KafTag.PREPOSITION;
		}else if(lemmaTag.equalsIgnoreCase("PRO")){
			kafTag=KafTag.PRONOUN;
		}else if(lemmaTag.equalsIgnoreCase("ce")){
			kafTag=KafTag.CONJUNCTION;
		}else if(lemmaTag.equalsIgnoreCase("csu")){
			kafTag=KafTag.CONJUNCTION;
		}else if(lemmaTag.equalsIgnoreCase("auxAvoir")){
			kafTag=KafTag.VERB;
		}else if(lemmaTag.equalsIgnoreCase("auxEtre")){
			kafTag=KafTag.VERB;
		}
	//	System.out.println("(lemma-to-kaf) Entering "+lemmaTag+", output "+kafTag.toString());
		return kafTag.toString();
	}
	
	protected static boolean isAncoraDeterminer(String tag) {
		return tag.startsWith("s") || tag.startsWith("S");
	}
	
	protected static boolean isDeterminer(String tag) {
		return tag.equalsIgnoreCase(KafTag.DETERMINER.tagText);
	}
	
	protected static boolean isAdverb(String tag) {
		return tag.equalsIgnoreCase(KafTag.ADVERB.tagText);
	}
	
	protected static boolean isCommonNoun(String tag) {
		return tag.equalsIgnoreCase(KafTag.COMMON_NOUN.tagText);
	}
	
	protected static boolean isProperNoun(String tag) {
		return tag.equalsIgnoreCase(KafTag.PROPER_NOUN.tagText);
	}
	
	protected static boolean isPronoun(String tag) {
		return tag.equalsIgnoreCase(KafTag.PRONOUN.tagText);
	}
	
	protected static boolean isAdjective(String tag) {
		return tag.equalsIgnoreCase(KafTag.ADJECTIVE.tagText);
	}
	
	protected static boolean isConjunction(String tag) {
		return tag.equalsIgnoreCase(KafTag.CONJUNCTION.tagText);
	}
	
	protected static boolean isOther(String tag) {
		return tag.equalsIgnoreCase(KafTag.OTHER.tagText);
	}
	
	protected static boolean isPreposition(String tag) {
		return tag.equalsIgnoreCase(KafTag.PREPOSITION.tagText);
	}
	
	/*
	 * Lemmatization tagset
		det
		np
		auxAvoir
		adj
		caimp
		suffAdj
		cld
		cla
		cldr
		que
		100
		adv
		clr
		ilimp
		cln
		clneg
		prep
		v
		pri
		prel
		nc
		auxEtre
		pro
		clar
		csu
		ce
	 */
	
	/* French TreeBank tagset
  - A (adjective)
  - Adv (adverb)
  - CC (coordinating conjunction)
  - Cl (weak clitic pronoun)
  - CS (subordinating conjunction)
  - D (determiner)
  - ET (foreign word)
  - I (interjection)
  - NC (common noun)
  - NP (proper noun)
  - P (preposition)
  - PREF (prefix)
  - PRO (strong pronoun)
  - V (verb)
  - PONCT (punctuation mark)
	 */
	
	/*
	 * KAF POSTAGS
N	common noun	V	verb
R	proper noun	P	preposition
Q	Pronoun	    A	adverb
D	Determiner	C	conjunction
G	Adjective	O	other

	 */
	
}

