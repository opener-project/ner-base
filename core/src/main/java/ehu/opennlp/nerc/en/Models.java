package ehu.opennlp.nerc.en;

import java.io.InputStream;

public class Models {

  private InputStream nerModel;

  public InputStream getNERModel(String cmdOption) {

    if (cmdOption.equals("en")) {
      nerModel = getClass().getResourceAsStream(
          "/en-testa-perceptron.bin");
    }

    if (cmdOption.equals("es")) {
      nerModel = getClass().getResourceAsStream("/es-500-4-testa.bin");
    }
    return nerModel;
  }

}
