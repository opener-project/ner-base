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
package eus.ixa.ixa.pipe.nerc.dict;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import eus.ixa.ixa.pipe.nerc.StringUtils;

/**
 * 
 * Class to load a directory containing dictionaries into a list of
 * Dictionaries. The files need to have the following structure: Barack
 * Obama\tperson\n
 * 
 * Every file located in the directory passed as a parameter will be loaded.
 * 
 * @author ragerri
 * @version 2014/06/25
 * 
 */
public class Dictionaries {
  
  private static final Pattern tabPattern = Pattern.compile("\t");

  public static boolean DEBUG = false;
  /**
   * The list of dictionary names.
   */
  private static List<String> dictNames;
  /**
   * The list of dictionaries as HashMap<String, String>.
   */
  private static List<Map<String, String>> dictionaries;
  /**
   * The list of lowercase dictionaries as HashMap<String, String>.
   */
  private static List<Map<String, String>> dictionariesIgnoreCase;

  /**
   * Construct the dictionaries from the input directory path.
   * 
   * @param inputDir
   *          the input directory
   */
  public Dictionaries(final String inputDir) {
    if (dictNames == null && dictionaries == null
        && dictionariesIgnoreCase == null) {
      try {
        loadDictionaries(inputDir);
      } catch (IOException e) {
        e.getMessage();
      }
    }

  }

  /**
   * Get the list of dictionaries as HashMaps.
   * 
   * @return a list of the dictionaries as HashMaps
   */
  public final List<Map<String, String>> getDictionaries() {
    return dictionaries;
  }

  /**
   * Get the lower case dictionaries.
   * 
   * @return a list of the dictionaries as HashMaps
   */
  public final List<Map<String, String>> getIgnoreCaseDictionaries() {
    return dictionariesIgnoreCase;
  }

  /**
   * Get the dictionary names.
   * 
   * @return the list of dictionary names
   */
  public final List<String> getDictNames() {
    return dictNames;
  }

  /**
   * Load the dictionaries.
   * 
   * @param inputDir
   *          the input directory
   * @throws IOException
   *           throws an exception if directory does not exist
   */
  private void loadDictionaries(final String inputDir) throws IOException {
    List<File> fileList = StringUtils.getFilesInDir(new File(inputDir));
    dictNames = new ArrayList<String>(fileList.size());
    dictionaries = new ArrayList<Map<String, String>>(fileList.size());
    dictionariesIgnoreCase = new ArrayList<Map<String, String>>(fileList.size());
    System.err.println("\tloading dictionaries in " + inputDir + " directory");
    for (int i = 0; i < fileList.size(); ++i) {
      if (DEBUG) {
        System.err.println("\tloading dictionary:...."
            + fileList.get(i).getCanonicalPath());
      }
      dictNames.add(fileList.get(i).getCanonicalPath());
      dictionaries.add(new HashMap<String, String>());
      dictionariesIgnoreCase.add(new HashMap<String, String>());

      List<String> fileLines = Files.readLines(fileList.get(i), Charsets.UTF_8);
      for (String line : fileLines) {
        String[] lineArray = tabPattern.split(line);
        if (lineArray.length == 2) {
          dictionaries.get(i).put(lineArray[0], lineArray[1]);
          if ((!line.equalsIgnoreCase("in")) && (!line.equalsIgnoreCase("on"))
              && (!line.equalsIgnoreCase("us"))
              && (!line.equalsIgnoreCase("or"))
              && (!line.equalsIgnoreCase("am"))) {
            dictionariesIgnoreCase.get(i).put(lineArray[0].toLowerCase(),
                lineArray[1]);
          }
        }
      }
    }
    System.err.println("found " + dictionaries.size() + " dictionaries");
  }
}
