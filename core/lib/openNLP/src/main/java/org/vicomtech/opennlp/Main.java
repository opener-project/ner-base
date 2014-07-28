package org.vicomtech.opennlp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import opennlp.tools.util.Span;

import org.apache.commons.lang3.StringUtils;
import org.vicomtech.opennlp.ner.NERTagger;
import org.vicomtech.opennlp.ner.NERTester;
import org.vicomtech.opennlp.ner.NERTrainer;
import org.vicomtech.opennlp.tools.namefind.TokenNameFinderModel;

/**
 * Hello world!
 *
 */
public class Main {
	
	
    public static void main( String[] args ) {
    	int iterations = 40;
    	int cutoff = 5;
    	
    	String type = "all";
    	String LANG = "fr";
    	String DIR = "/home/VICOMTECH/aazpeitia/workspace/openNLP/";
    	String CORPUS_FOLDER          = "/corpus/";
    	String XML_DESCRIPTORS_FOLDER = "/xml_descriptors/";
    	String MODELS_FOLDER          = "/models/";
    	String RESULTS_FOLDER         = "/results/";
    	
    	String train = DIR+LANG+CORPUS_FOLDER+"ester.train";
    	String dev   = DIR+LANG+CORPUS_FOLDER+"ester.dev";
    	String test  = DIR+LANG+CORPUS_FOLDER+"ester.test";
    	
    	//String xml_descriptor = DIR+LANG+XML_DESCRIPTORS_FOLDER+"XML_Descriptor_Default.xml";
    	String xml_descriptor = DIR+LANG+XML_DESCRIPTORS_FOLDER+"XML_Descriptor_Default_PoS.xml";
    	
    	String DEFAULT_FOLDER             = "1.Default/";
    	String SENTENCE_FOLDER            = "2.Sentence/";
    	String TOKEN_WINDOW_FOLDER        = "3.TokenWindow/";
    	String TOKENCLASS_WINDOW_FOLDER   = "4.TokenClassWindow/";
    	String TOKENPATTERN_WINDOW_FOLDER = "5.TokenPatternWindow/";
    	String BIGRAM_WINDOW_FOLDER       = "6.BigramWindow/";
    	String PREFIX_WINDOW_FOLDER       = "7.PrefixWindow/";
    	String SUFFIX_WINDOW_FOLDER       = "8.SuffixWindow/";
    	String CHARNGRAM_FOLDER           = "9.CharNGram/";
    	String POS_FOLDER                 = "10.POS/";
    	String CUTOFF_FOLDER              = "11.Cutoff/";
//    	String ITERATIONS_FOLDER          = "12.Iterations/";
    	String ITERATIONS_FOLDER          = "12.Iterations/POS/";
    	
    	String resultsFile = DIR+LANG+RESULTS_FOLDER+ITERATIONS_FOLDER+"resultttttt.txt";
    	BufferedWriter bw = null;
    	try {
    		bw = new BufferedWriter(new FileWriter(resultsFile));
    		
    		for (int i=iterations; i<=80; i+=20) {
    			bw.write("-----------------------------------------------------------------------------------------------------------------");
        		bw.newLine();
        		bw.write("iteration: "+i);
        		bw.newLine();
        		bw.write("-----------------------------------------------------------------------------------------------------------------");
        		bw.newLine();bw.newLine();
        		
        		//String ner_model_file = DIR+LANG+MODELS_FOLDER+ITERATIONS_FOLDER
            	//		+"ner_ester-ngram_4_6-tw_2_1-tcw_1_2-tpw_0_0-prev-big_1_1-pre_1_0-suf_1_0-sent_t_f-cut_"+cutoff+"-iter_"+i+".bin";
        		
        		String ner_model_file = DIR+LANG+MODELS_FOLDER+ITERATIONS_FOLDER
            			+"ner_ester-ngram_4_6-tw_2_1-tcw_1_2-tpw_0_0-prev-big_1_1-pre_1_0-suf_1_0-pos_0_3_ftb-sent_t_f-cut_"+cutoff+"-iter_"+i+".bin";
        		
    			NERTrainer trainer = new NERTrainer(LANG, type, ner_model_file, i, cutoff, xml_descriptor);
    	    	TokenNameFinderModel model = trainer.train(train);
        		
    	    	NERTester tester = new NERTester(model);
    	    	
        		bw.write(tester.test(dev, false).toString());
        		bw.newLine();bw.newLine();bw.newLine();
    		}
	    	
//	    	NERTagger tagger = new NERTagger(ner_model_file);
//	    	String[] tokens = {"bonne", "journée" , "journée", "qui", "commence", "avec", "Fabrice", "Drouelle", "."};
//	    	Span[] spans = tagger.tag(tokens);
//	    	String[] nes = Span.spansToStrings(spans, tokens);
//	    	System.out.println(StringUtils.join(nes, " "));
    	} 
    	catch (IOException e) {
			e.printStackTrace();
		}
    	finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
    	
//    	try {
//	    	String ner_model_file = DIR+LANG+MODELS_FOLDER+ITERATIONS_FOLDER
//	    			+"ner_ester-ngram_4_6-tw_2_1-tcw_1_2-tpw_0_0-prev-big_1_1-pre_1_0-suf_1_0-sent_t_f-cut_"+cutoff+"-iter_380.bin";
////    		String ner_model_file = DIR+LANG+MODELS_FOLDER+ITERATIONS_FOLDER
////	    			+"ner_ester-ngram_4_6-tw_2_1-tcw_1_2-tpw_0_0-prev-big_1_1-pre_1_0-suf_1_0-pos_0_3_ftb-sent_t_f-cut_"+cutoff+"-iter_260.bin";
//    		NERTester tester = new NERTester(ner_model_file);
//	        System.out.println(tester.test(dev, true).toString());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
        
    }
}
