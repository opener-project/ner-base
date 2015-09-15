/*
 *  Copyright 2014 Rodrigo Agerri

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

package eus.ixa.ixa.pipe.nerc.train;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.TerminateToolException;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 * Utility functions to read and save ObjectStreams.
 * @author ragerri
 */
public final class InputOutputUtils {

  /**
   * Private constructor. This class should only be used statically.
   */
  private InputOutputUtils() {

  }
  
  /**
   * Remove punctuation.
   * @param resource the dictionary path
   * @return the dictionary path without punctuation
   */
  public static final String normalizeLexiconName(String resource) {
    resource = resource.replaceAll("\\p{P}", "");
    return resource;
  }
  
  /**
   * Get an input stream from a resource name. This could be either an
   * absolute path pointing to a resource in the classpath or a file
   * or a directory in the file system. If found in the classpath
   * that will be loaded first.
   *
   * @param resource
   *          the name of the resource (absolute path with no starting /)
   * @return the inputstream of the dictionary
   */
  public static final InputStream getDictionaryResource(final String resource) {
    
    InputStream dictInputStream;
    Path resourcePath = Paths.get(resource);
    String normalizedPath = resourcePath.toString();
    dictInputStream = getStreamFromClassPath(normalizedPath);
    if (dictInputStream == null) {
      try {
        dictInputStream = new FileInputStream(normalizedPath);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
    return new BufferedInputStream(dictInputStream);
  }

  /**
   * Load a resource from the classpath.
   * 
   * @param normalizedPath the path normalized using {@code Paths} functions.
   * @return the input stream of the resource
   */
  private static InputStream getStreamFromClassPath(String normalizedPath) {
    InputStream dictInputStream = null;
    String[] dictPaths = normalizedPath.split("src/main/resources");
    if (dictPaths.length == 2) {
      dictInputStream = InputOutputUtils.class.getClassLoader().getResourceAsStream(dictPaths[1]);
    } else {
      String[] windowsPaths = normalizedPath.split("src\\\\main\\\\resources\\\\");
      if (windowsPaths.length == 2) {
        dictInputStream = InputOutputUtils.class.getClassLoader().getResourceAsStream(windowsPaths[1]);
      }
    }
    return dictInputStream;
  }

  /**
   * Check input file integrity.
   * @param name
   *          the name of the file
   * @param inFile
   *          the file
   */
  private static void checkInputFile(final String name, final File inFile) {

    String isFailure = null;

    if (inFile.isDirectory()) {
      isFailure = "The " + name + " file is a directory!";
    } else if (!inFile.exists()) {
      isFailure = "The " + name + " file does not exist!";
    } else if (!inFile.canRead()) {
      isFailure = "No permissions to read the " + name + " file!";
    }

    if (null != isFailure) {
      throw new TerminateToolException(-1, isFailure + " Path: "
          + inFile.getAbsolutePath());
    }
  }

  /**
   * Load the parameters in the {@code TrainingParameters} file.
   * @param paramFile
   *          the training parameters file
   * @return default loading of the parameters
   */
  public static TrainingParameters loadTrainingParameters(final String paramFile) {
    return loadTrainingParameters(paramFile, false);
  }

  /**
   * Load the parameters in the {@code TrainingParameters} file.
   * 
   * @param paramFile
   *          the parameter file
   * @param supportSequenceTraining
   *          wheter sequence training is supported
   * @return the parameters
   */
  private static TrainingParameters loadTrainingParameters(
      final String paramFile, final boolean supportSequenceTraining) {

    TrainingParameters params = null;

    if (paramFile != null) {

      checkInputFile("Training Parameter", new File(paramFile));

      InputStream paramsIn = null;
      try {
        paramsIn = new FileInputStream(new File(paramFile));

        params = new opennlp.tools.util.TrainingParameters(paramsIn);
      } catch (IOException e) {
        throw new TerminateToolException(-1,
            "Error during parameters loading: " + e.getMessage(), e);
      } finally {
        try {
          if (paramsIn != null) {
            paramsIn.close();
          }
        } catch (IOException e) {
          System.err.println("Error closing the input stream");
        }
      }

      if (!TrainerFactory.isValid(params.getSettings())) {
        throw new TerminateToolException(1, "Training parameters file '"
            + paramFile + "' is invalid!");
      }
    }

    return params;
  }

  /**
   * Read the file into an {@code ObjectStream}.
   * 
   * @param infile
   *          the string pointing to the file
   * @return the object stream
   */
  public static ObjectStream<String> readFileIntoMarkableStreamFactory(final String infile) {

    InputStreamFactory inputStreamFactory = null;
    try {
      inputStreamFactory = new MarkableFileInputStreamFactory(
          new File(infile));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    ObjectStream<String> lineStream = null;
    try {
      lineStream = new PlainTextByLineStream(
          (inputStreamFactory), "UTF-8");
    } catch (IOException e) {
      CmdLineUtil.handleCreateObjectStreamError(e);
    }
    return lineStream;
  }
}