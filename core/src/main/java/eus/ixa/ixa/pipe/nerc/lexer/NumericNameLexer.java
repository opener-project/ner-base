/*
 * Copyright 2014 Rodrigo Agerri

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

package eus.ixa.ixa.pipe.nerc.lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import eus.ixa.ixa.pipe.nerc.Name;
import eus.ixa.ixa.pipe.nerc.NameFactory;


/**
 *  NumericNameLexer is based on the {@link NumericLexer} class.
 * <ol>
 *  <li> Numeric DATE Recognition.
 *  <li> Recognition of numeric TIME expressions.
 *  <li> Percents.
 *  <li> Currency expressions.
 *  </ol>
 *
 * For more CLI options, please check {@code CLI} javadoc and README file.
 * @author ragerri
 * @version 2014-05-14
 */
public class NumericNameLexer {

  /**
   * The lexer specification.
   */
  private NumericLexer jlexer;
  private Name nextToken;
  
  /**
   * Construct a new NumericNameLexer.
   * @param breader Reader
   * @param nameFactory The NameFactory that will be invoked to convert
   *        each string extracted by the @link NumericLexer into a @Name object
   */
  public NumericNameLexer(final BufferedReader breader, final NameFactory nameFactory) {
    jlexer = new NumericLexer(breader, nameFactory);
  }
  
  /**
   * Returns found expressions as a List of names.
   *
   * @return A list of all tokens remaining in the underlying Reader
   */
  public List<Name> getNumericNames() {
    List<Name> result = new ArrayList<Name>();
    while (hasNextToken()) {
      result.add(getNextToken());
    }
    return result;
  }
  
  /**
   * Check if the next token provided by yylex() is null.
   * @return true or false
   */
  public boolean hasNextToken() {
    if (nextToken == null) {
      try {
        nextToken = jlexer.yylex();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return nextToken != null;
  }
  
  
  /**
   * Get the next token provided by yylex().
   * @return the next name
   */
  public Name getNextToken() {
    if (nextToken == null) {
      try {
        nextToken = jlexer.yylex();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    Name result = nextToken;
    nextToken = null;
    if (result == null) {
      throw new NoSuchElementException();
    }
    return result;
  }
  
}
