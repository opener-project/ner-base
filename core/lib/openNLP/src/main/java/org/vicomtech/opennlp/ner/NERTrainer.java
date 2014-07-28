package org.vicomtech.opennlp.ner;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;

import org.vicomtech.opennlp.tools.namefind.NameFinderME;
import org.vicomtech.opennlp.tools.namefind.TokenNameFinderModel;
import org.vicomtech.opennlp.util.Utils;

import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;


public class NERTrainer {

	private String lang;
	private String type;
	private String ner_model_path;
	private Path train_data_path;
	private Path xml_descriptor_path;
	private int iterations;
	private int cutoff;
	private TokenNameFinderModel model;
	
	public final static String DEF_TYPE       = new String();
	public final static int    DEF_ITERATIONS = 100;
	public final static int    DEF_CUTOFF     = 5;
//////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// CONSTRUCTORS /////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////
	public NERTrainer(String lang,
			  		  String ner_model_path) throws IOException {
		this.init(lang, DEF_TYPE, ner_model_path, DEF_ITERATIONS, DEF_CUTOFF, null);
	}
	
	public NERTrainer(String lang,
					  String type,
					  String ner_model_path) throws IOException {
		this.init(lang, type, ner_model_path, DEF_ITERATIONS, DEF_CUTOFF, null);
	}
	
	public NERTrainer(String lang,
			  		  String ner_model_path,
			  		  int iterations,
			  		  int cutoff) throws IOException {
		this.init(lang, DEF_TYPE, ner_model_path, iterations, cutoff, null);
	}
	
	public NERTrainer(String lang,
					  String type,
					  String ner_model_path,
					  int iterations,
					  int cutoff) throws IOException {
		this.init(lang, type, ner_model_path, iterations, cutoff, null);
	}
	
	public NERTrainer(String lang,
					  String ner_model_path,
					  int iterations,
					  int cutoff,
					  String xml_descriptor_path) throws IOException {
		this.init(lang, DEF_TYPE, ner_model_path, iterations, cutoff, xml_descriptor_path);
	}
	
	public NERTrainer(String lang,
					  String type,
					  String ner_model_path,
					  int iterations,
					  int cutoff,
					  String xml_descriptor_path) throws IOException {
		this.init(lang, type, ner_model_path, iterations, cutoff, xml_descriptor_path);
	}
	
	private void init(String lang,
			  		  String type,
			  		  String ner_model_path,
			  		  int iterations,
			  		  int cutoff,
			  		  String xml_descriptor_path) throws IOException {
		this.lang = lang;
		this.type = type;
		this.ner_model_path = ner_model_path;
		this.iterations = iterations;
		this.cutoff = cutoff;
		if (xml_descriptor_path != null) {
			this.xml_descriptor_path = Utils.getPath(xml_descriptor_path);
		}
		else {
			this.xml_descriptor_path = null;
		}
	}
//////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////// SETTERS ////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}
	
	public void setCutoff(int cutoff) {
		this.cutoff = cutoff;
	}
	
	public void setXMLDescriptor(String xml_descriptor_path) throws IOException {
		this.xml_descriptor_path = Utils.getPath(xml_descriptor_path);
	}
//////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////// TRAINERS ////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////
	public TokenNameFinderModel train(String train_data_path) throws IOException {
		this.train_data_path = Utils.getPath(train_data_path);
		FileInputStream fis = null;
		ObjectStream<NameSample> sampleStream = null;
		BufferedOutputStream modelOut = null;
		try {
			byte[] ba;
			if (this.xml_descriptor_path != null) {
				fis = new FileInputStream(this.xml_descriptor_path.toFile());
				ba = new byte[(int)this.xml_descriptor_path.toFile().length()];
				fis.read(ba);
			}
			else {
				ba = null;
			}
			
			ObjectStream<String> lineStream = new PlainTextByLineStream(
					new FileInputStream(this.train_data_path.toFile()), Charset.forName("UTF-8"));
			sampleStream = new NameSampleDataStream(lineStream);
			
			TrainingParameters params = ModelUtil.createTrainingParameters(iterations, cutoff);
			model = NameFinderME.train(lang, type, sampleStream, params, ba, new HashMap<String,Object>());
			modelOut = new BufferedOutputStream(new FileOutputStream(ner_model_path));
			model.serialize(modelOut);
			return model;
		}
		finally {
			if (fis != null) fis.close();
			if (sampleStream != null) sampleStream.close();
			if (modelOut != null) modelOut.close();
		}
	}
}
