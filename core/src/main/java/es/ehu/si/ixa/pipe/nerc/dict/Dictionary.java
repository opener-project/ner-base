package es.ehu.si.ixa.pipe.nerc.dict;

import java.util.HashMap;
import java.util.Map;

/**
 * It defines a Dictionary class consisting of a HashMap. The keys are Named
 * Entity tokens and the values are the corresponding Named Entity class.
 * 
 * @author ragerri
 * @version 2014/06/25
 * 
 */
public class Dictionary {

  /**
   * The Map to store the dictionary.
   */
  private Map<String, String> dictMap;

  /**
   * Construct a Dictionary with a Map of Strings.
   * The key is the Named Entity token and the value
   * is the Named Entity class.
   * 
   * @param aMap
   *          the map of strings
   */
  public Dictionary() {
    this.dictMap = new HashMap<String, String>();
  }

  /**
   * Get the Map dictionary.
   * 
   * @return the dictionary as a map
   */
  public final Map<String, String> getDict() {
    return dictMap;
  }

  /**
   * Put a Named Entity token as key and its
   * Named Entity class as value.
   * 
   * @param name
   * @param neType
   */
  public void populate(String name, String neType) {
    dictMap.put(name, neType);
  }

  /**
   * Get the <key,value> size of the dictionary.
   * @return maximum token count in the dictionary
   */
  public int getMaxTokenCount() {
    return dictMap.size();
  }

}
