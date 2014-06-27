package es.ehu.si.ixa.pipe.nerc.dict;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;


/**
 * 
 * Class to load a directory containing dictionaries into a list of
 * Dictionaries. The files need to have the following structure: 
 * Barack Obama\tperson\n
 * 
 * Every file located in the directory passed as the argument of the --dirPath 
 * parameter will be loaded.
 * 
 * @author ragerri
 * @version 2014/06/25
 * 
 */
public class Dictionaries {

  /**
   * The list of dictionary names.
   */
  private static List<String> dictNames;
  /**
   * The list of dictionaries as HashMap<String, String>.
   */
  private static List<Dictionary> dictionaries;
  /**
   * The list of lowercase dictionaries as HashMap<String, String>.
   */
  private static List<Dictionary> dictionariesIgnoreCase;

  /**
   * Construct the dictionaries from the input directory path.
   * 
   * @param inputDir
   *          the input directory
   */
  public Dictionaries(final String inputDir) {
    if (dictNames == null && dictionaries == null && dictionariesIgnoreCase == null) {
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
  public final List<Dictionary> getDictionaries() {
    return dictionaries;
  }

  /**
   * Get the lower case dictionaries.
   * 
   * @return a list of the dictionaries as HashMaps
   */
  public final List<Dictionary> getIgnoreCaseDictionaries() {
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
    File inputPath = new File(inputDir);
    if (inputPath.isDirectory()) {
      Collection<File> files = FileUtils.listFiles(inputPath, null, true);
      List<File> fileList = new ArrayList<File>(files);
      dictNames = new ArrayList<String>(files.size());
      dictionaries = new ArrayList<Dictionary>(files.size());
      dictionariesIgnoreCase = new ArrayList<Dictionary>(files.size());
      for (int i = 0; i < fileList.size(); ++i) {
        System.err.println("\tloading dictionary:...."
            + fileList.get(i).getCanonicalPath());
        dictNames.add(fileList.get(i).getName());
        dictionaries.add(new Dictionary());
        dictionariesIgnoreCase.add(new Dictionary());

        List<String> fileLines = FileUtils.readLines(fileList.get(i), "UTF-8");
        for (String line : fileLines) {
          String[] lineArray = line.split("\t");
          if (lineArray.length == 2) {
            dictionaries.get(i).populate(lineArray[0], lineArray[1]);
            if ((!line.equalsIgnoreCase("in"))
                && (!line.equalsIgnoreCase("on"))
                && (!line.equalsIgnoreCase("us"))
                && (!line.equalsIgnoreCase("or"))
                && (!line.equalsIgnoreCase("am"))) {
              dictionariesIgnoreCase.get(i).populate(
                  lineArray[0].toLowerCase(), lineArray[1]);
            }
          }
        }
      }
      System.err.println("found " + dictionaries.size() + " dictionaries");
    }

  }

}
