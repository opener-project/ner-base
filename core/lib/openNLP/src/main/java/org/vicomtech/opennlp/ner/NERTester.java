package org.vicomtech.opennlp.ner;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import opennlp.tools.cmdline.namefind.NameEvaluationErrorListener;
import opennlp.tools.cmdline.namefind.TokenNameFinderDetailedFMeasureListener;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderEvaluator;
import opennlp.tools.namefind.TokenNameFinderEvaluationMonitor;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.eval.EvaluationMonitor;
import opennlp.tools.util.eval.FMeasure;

import org.vicomtech.opennlp.tools.namefind.NameFinderME;
import org.vicomtech.opennlp.tools.namefind.TokenNameFinderModel;
import org.vicomtech.opennlp.util.Utils;

public class NERTester {

	private TokenNameFinderModel model;
	private Path model_path;
	private Path test_data_path;
	
	private double precision;
	private double recall;
	private double fmeasure;
	
	public final static String PRECISION_KEY = "Precision";
	public final static String RECALL_KEY    = "Recall";
	public final static String FMEASURE_KEY  = "FMeasure";
	
	public NERTester(String model_path) throws IOException {
		this.model_path = Utils.getPath(model_path);
		FileInputStream modelfile = new FileInputStream(this.model_path.toFile());
		this.model = new TokenNameFinderModel(modelfile);
	}
	
	public NERTester(TokenNameFinderModel model) throws IOException {
		this.model = model;
	}
	
	public Map<String,Double> test(String test_data_path, boolean detailed) throws InvalidFormatException, IOException{
		this.test_data_path = Utils.getPath(test_data_path);
		FileInputStream sampleDataIn = null;
		try{
			sampleDataIn = new FileInputStream(this.test_data_path.toFile());
		
			Charset charset = Charset.forName("UTF-8");
			ObjectStream<String> lineStream = new PlainTextByLineStream(sampleDataIn.getChannel(), charset);
			ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
			
			List<EvaluationMonitor<NameSample>> listeners = getListeners(detailed);
			
			TokenNameFinderEvaluator evaluator = new TokenNameFinderEvaluator(
					new NameFinderME(this.model),
					listeners.toArray(new TokenNameFinderEvaluationMonitor[listeners.size()]));
		
			evaluator.evaluate(sampleStream);
			FMeasure scores = evaluator.getFMeasure();
			this.precision = scores.getPrecisionScore();
			this.recall = scores.getRecallScore();
			this.fmeasure = scores.getFMeasure();
			
			Map<String,Double> results = new HashMap<String,Double>();
			results.put(PRECISION_KEY, this.precision);
			results.put(RECALL_KEY, this.recall);
			results.put(FMEASURE_KEY, this.fmeasure);
			
			for (EvaluationMonitor<NameSample> listener : listeners) {
				if (listener instanceof TokenNameFinderDetailedFMeasureListener) {
					System.out.println(listener.toString());
				}
			}
			
			return results;
		}
		finally {
			if (sampleDataIn != null) sampleDataIn.close();
		}
	}
	
	public double getPrecision() {
		return this.precision;
	}
	
	public double getRecall() {
		return this.recall;
	}
	
	public double getFMeasure() {
		return this.fmeasure;
	}
	
	private List<EvaluationMonitor<NameSample>> getListeners(boolean detailed) {
		List<EvaluationMonitor<NameSample>> listeners = new LinkedList<EvaluationMonitor<NameSample>>();
		if (detailed) {
			listeners.add(new NameEvaluationErrorListener());
			listeners.add(new TokenNameFinderDetailedFMeasureListener());
		}
		return listeners;
	}

}
