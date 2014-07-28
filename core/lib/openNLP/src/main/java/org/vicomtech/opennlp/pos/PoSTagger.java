package org.vicomtech.opennlp.pos;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.POSModel;

import org.vicomtech.opennlp.pos.TagsetMappings.TagSet;
import org.vicomtech.opennlp.util.Utils;

public class PoSTagger {

	private POSTaggerME postagger;
	private TagSet tagSet;
	
	/**
	 * The PosTagger class uses an internal instance of OpenNLP POSTaggerME.
	 * @param posmodel		OpenNLP POSTagger instance
	 */
	public PoSTagger(POSModel posmodel) {
		this.postagger = new POSTaggerME(posmodel);
		this.tagSet = TagSet.NONE;
	}
	
	/**
	 * The PosTagger class uses an internal instance of OpenNLP POSTaggerME.
	 * @param posmodel		OpenNLP POSTagger instance
	 * @param tagSet		TagSet mapping, converts tagSet to KAF tags
	 */
	public PoSTagger(POSModel posmodel, TagSet tagSet) {
		this.postagger = new POSTaggerME(posmodel);
		this.tagSet = tagSet;
	}
	
	/**
	 * The PosTagger class uses an internal instance of OpenNLP POSTaggerME.
	 * @param pos_model_path	POSTagger model path
	 */
	public PoSTagger(String pos_model_path) throws IOException {
		InputStream modelIn = Utils.path2Stream(pos_model_path);
		init(modelIn, TagSet.NONE);
	}
	
	/**
	 * The PosTagger class uses an internal instance of OpenNLP POSTaggerME.
	 * @param pos_model_path	POSTagger model path
	 * @param tagSet			TagSet mapping, converts tagSet to KAF tags
	 */
	public PoSTagger(String pos_model_path, TagSet tagSet) throws IOException {
		InputStream modelIn = Utils.path2Stream(pos_model_path);
		init(modelIn, tagSet);
	}
	
	/**
	 * The PosTagger class uses an internal instance of OpenNLP POSTaggerME.
	 * @param modelIn			POSTagger model input stream
	 */
	public PoSTagger(InputStream modelIn) throws IOException {
		init(modelIn, TagSet.NONE);
	}
	
	/**
	 * The PosTagger class uses an internal instance of OpenNLP POSTaggerME.
	 * @param modelIn			POSTagger model input stream
	 * @param tagSet			TagSet mapping, converts tagSet to KAF tags
	 */
	public PoSTagger(InputStream modelIn, TagSet tagSet) throws IOException {
		init(modelIn, tagSet);
	}
	
	private void init(InputStream modelIn, TagSet tagSet) throws IOException {
		try {
			this.postagger = new POSTaggerME(new POSModel(modelIn));
			this.tagSet = tagSet;
		}
		finally {
			if (modelIn != null) modelIn.close();
		}
	}

	public String[] postag(String[] tokens) {
		String[] postags = this.postagger.tag(tokens);
		// convert tag format
		TagsetMappings.convertPostags(postags, this.tagSet);
		return postags;
	}
	
	public List<String[]> postag(Path inFile) throws IOException {
		List<String> lines = Files.readAllLines(inFile, Charset.forName("UTF-8"));
		
		List<String[]> postags = new ArrayList<String[]>();
		for (String line : lines) {
			String[] tokens = line.split(" ");
			String[] tags = this.postag(tokens);
			postags.add(tags);
		}
		
		return postags;
	}
	
	public POSTaggerME getPoSModel() {
		return this.postagger;
	}
	
	public TagSet getTagSet() {
		return this.tagSet;
	}
}
