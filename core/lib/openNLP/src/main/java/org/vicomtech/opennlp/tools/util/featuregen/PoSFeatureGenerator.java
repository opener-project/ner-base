/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.vicomtech.opennlp.tools.util.featuregen;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.vicomtech.opennlp.pos.PoSTagger;
import org.vicomtech.opennlp.pos.TagsetMappings.TagSet;
import org.vicomtech.opennlp.util.Utils;

import opennlp.tools.util.featuregen.FeatureGeneratorAdapter;

/**
 * Generates a feature which contains the PoS tag.
 */
public class PoSFeatureGenerator extends FeatureGeneratorAdapter {

	private PoSTagger postagger;
	private Map<String,String> tokPosMap;
	private static final String POS_PREFIX = "p";
	
	public PoSFeatureGenerator(String posModelPath, TagSet tagSet) {
		try {
			this.postagger = new PoSTagger(posModelPath, tagSet);
			this.tokPosMap = new HashMap<String,String>();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(Utils.ERROR_STATUS);
		}
	}
	
	public PoSFeatureGenerator(PoSTagger postagger) {
		this.postagger = postagger;
		this.tokPosMap = new HashMap<String,String>();
	}

	public void createFeatures(List<String> features,
							   String[] tokens,
							   int index,
							   String[] preds) {
		String[] postags = this.getPostags(tokens);
		features.add(POS_PREFIX + "=" + postags[index]);
	}
	
	private String[] getPostags(String[] tokens) {
		String text = StringUtils.join(tokens, " ");
		if (this.tokPosMap.containsKey(text)) {
			return this.tokPosMap.get(text).split(" ");
		}
		else {
			String[] postags = this.postagger.postag(tokens);
			this.tokPosMap.put(text, StringUtils.join(postags, " "));
			return postags;
		}
	}
	
}
