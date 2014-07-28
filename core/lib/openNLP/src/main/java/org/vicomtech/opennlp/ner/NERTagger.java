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

package org.vicomtech.opennlp.ner;

import opennlp.tools.util.Span;

import java.io.IOException;
import java.io.InputStream;

import org.vicomtech.opennlp.tools.namefind.NameFinderME;
import org.vicomtech.opennlp.tools.namefind.TokenNameFinderModel;
import org.vicomtech.opennlp.util.Utils;

public class NERTagger {

	private NameFinderME nertagger;
	
	public NERTagger(String ner_model_path) throws IOException {
		InputStream modelIn = Utils.path2Stream(ner_model_path);
		try {
			this.nertagger = new NameFinderME(new TokenNameFinderModel(modelIn));
		}
		finally {
			if (modelIn != null) modelIn.close();
		}
	}
	
	public NERTagger(TokenNameFinderModel ner_model) {
		this.nertagger = new NameFinderME(ner_model);
	}
	
	public NERTagger(InputStream modelIn) throws IOException {
		try {
			this.nertagger = new NameFinderME(new TokenNameFinderModel(modelIn));
		}
		finally {
			if (modelIn != null) modelIn.close();
		}
	}
	
	public Span[] tag(String[] words) {
		Span[] tags = this.nertagger.find(words);
		return tags;
	}
}