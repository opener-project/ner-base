/*
 *  Copyright 2015 Rodrigo Agerri

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

package eus.ixa.ixa.pipe.nerc.dict;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.SerializableArtifact;



/**
 * This class loads the pos tagger model required for
 * the POS FeatureGenerators. It also provides the serializer
 * required to add it as a resource to the ixa-pipe-nerc
 * model.
 * @author ragerri
 * @version 2015-10-03
 * 
 */
public class POSModelResource implements SerializableArtifact {
  
  public static class POSModelResourceSerializer implements ArtifactSerializer<POSModelResource> {

    public POSModelResource create(InputStream in) throws IOException,
        InvalidFormatException {
      return new POSModelResource(in);
    }

    public void serialize(POSModelResource artifact, OutputStream out)
        throws IOException {
      artifact.serialize(out);
    }
  }
  
  /**
   * The POS model.
   */
  private POSModel posModel;
  /**
   * The POS tagger.
   */
  private POSTaggerME posTagger;
  
  /**
   * Construct the POSModelResource from the inputstream.
   * @param in the input stream
   * @throws IOException io exception
   */
  public POSModelResource(InputStream in) throws IOException {
    posModel = new POSModel(in);
    posTagger = new POSTaggerME(posModel);
  }
  
  /**
   * POS tag the current sentence.
   * @param tokens the current sentence
   * @return the array containing the pos tags
   */
  public String[] posTag(String[] tokens) {
    String[] posTags = posTagger.tag(tokens);
    return posTags;
  }
  
  /**
   * Serialize the POS model into the NERC model.
   * @param out the output stream
   * @throws IOException io exception
   */
  public void serialize(OutputStream out) throws IOException {
    Writer writer = new BufferedWriter(new OutputStreamWriter(out));
    posModel.serialize(out);

    writer.flush();
  }

  public Class<?> getArtifactSerializerClass() {
    return POSModelResourceSerializer.class;
  }

}


