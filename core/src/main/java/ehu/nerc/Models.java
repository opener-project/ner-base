/*
 * Copyright 2013 Rodrigo Agerri

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

package ehu.nerc;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import opennlp.tools.namefind.TokenNameFinderModel;

public class Models {

	private static Map<String, TokenNameFinderModel> posModelMap = new HashMap<String, TokenNameFinderModel>();
  private InputStream nerModel;

  public InputStream getNERModelInputStream(String langCmdOption) {
    
    if (langCmdOption.equals("de")) {
      nerModel = getClass().getResourceAsStream(
          "/de-nerc-perceptron-500-0-testa.bin");
    }

    if (langCmdOption.equals("en")) {
      nerModel = getClass().getResourceAsStream(
          "/en-nerc-perceptron-500-0-testa.bin");
    }

    if (langCmdOption.equals("es")) {
      nerModel = getClass().getResourceAsStream("/es-nerc-500-4-testa.bin");
    }
    
    if (langCmdOption.equals("it")) {
      nerModel = getClass().getResourceAsStream(
          "/it-nerc-perceptron-500-0-09.bin");
    }
    
    if (langCmdOption.equals("nl")) {
      nerModel = getClass().getResourceAsStream(
          "/nl-nerc-perceptron-500-0-testa.bin");
    }
    
    if (langCmdOption.equals("fr")) {
        nerModel = getClass().getResourceAsStream(
            "/fr-ner-all.bin");
      }
    
    return nerModel;
  }
  
  public TokenNameFinderModel getNercModel(String lang) {
	  TokenNameFinderModel model = posModelMap.get(lang);
		if (model == null) {
			model=loadModel(lang);
			posModelMap.put(lang, model);
		}
		return model;
	}

	private TokenNameFinderModel loadModel(String lang) {
		InputStream modelInputStream = null;
		try {
			modelInputStream = getNERModelInputStream(lang);
			TokenNameFinderModel model = new TokenNameFinderModel(modelInputStream);
			return model;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (modelInputStream != null) {
				try {
					modelInputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
