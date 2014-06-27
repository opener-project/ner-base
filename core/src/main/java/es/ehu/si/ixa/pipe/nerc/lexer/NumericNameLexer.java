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

package es.ehu.si.ixa.pipe.nerc.lexer;

import java.io.BufferedReader;
import java.io.IOException;

import es.ehu.si.ixa.pipe.nerc.CLI;
import es.ehu.si.ixa.pipe.nerc.NameFactory;


/**
 *  NumericNameLexer is based on the {@link NumericLexer} class. 
 *  This NameLexer overrides {@link AbstractNameLexer} getToken() method 
 *  by using the {@link NumericLexer} yylex() method.  
 *  
 * Specifically, apart from English Penn Treebank-compliant tokenization, 
 * this NumericNameLexer provides for German, English, Spanish, French, Italian
 * and Dutch:
 *  
 * <ol>
 *  <li> Numeric DATE Recognition.
 *  <li> Recognition of numeric TIME expressions.
 *  <li> Percents.
 *  <li> Currency expressions.
 *  </ol> 
 *
 * For more CLI options, please check {@link CLI} javadoc and README file. 
 * @author ragerri
 * @version 2014-05-14
 * 
 */
 
public class NumericNameLexer<T> extends AbstractNameLexer<T> {

  
  private NumericLexer jlexer;
  
  /**
   * Construct a new NumericNameLexer which uses the @link JFlexLexer specification.
   * 
   * 
   * @param breader Reader
   * @param nameFactory The NameFactory that will be invoked to convert
   *        each string extracted by the @link NumericLexer  into a @Name object
   * 
   */
  public NumericNameLexer(BufferedReader breader, NameFactory nameFactory) {
    jlexer = new NumericLexer(breader, nameFactory);
  }

  /**
   * It obtains the next token. This functions performs the actual recognition 
   * by calling the @link NumericLexer yylex() function.
   *
   * @return the next token or null if none exists.
   */
  @Override
  @SuppressWarnings("unchecked")
  public T getToken() {
    try {
      return (T) jlexer.yylex();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return nextToken;
  }
  
}
