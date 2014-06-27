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

import java.util.Iterator;
import java.util.List;


/**
 * NameLexers break up text into individual Objects. The decisions to specify
 * this interface are pragmatically based on the main NameLexer implementation
 * provided by ixa-pipe-nerc, namely, the @link NumericNameLexer. That
 * implementation uses JFlex to create a scanner which recognizes certain
 * patterns in running text and creates @link Name objects. The default API of {@link
 * NumericLexer} provides a <code>yylex()</code> method that behaves roughly like a
 * <code>next()</code> Iterator function.
 * 
 * Thus, NameLexer implementations will probably implement and/or override the
 * <code>next()</code> function. For example @link NumericNameLexer provides
 * an implementation of
 * <code>next()<code> that uses internally the <code>yylex()</code> function of @link
 * NumericLexer to obtain the Name objects. Other implementations are 
 * also expected to implement the <code>next()</code> function.
 * 
 * The same reason (JFlex API) dictates that implementations of this interface
 * are expected to have a constructor takes a Reader as argument.
 * 
 * A NameLexer extends the Iterator interface, but it also provides a lookahead
 * operation <code>lookAhead()</code>.
 * 
 * @author ragerri
 * @version 2014-05-14
 */
public interface NameLexer<T> extends Iterator<T> {

  /**
   * Returns the next token from this NameLexer.
   * 
   * @return the next token
   * @throws java.util.NoSuchElementException
   *           if the are not any tokens.
   */
  public T next();

  /**
   * Returns <code>true</code> if and only if this NameLexer has more elements.
   */
  public boolean hasNext();

  /**
   * Removes from last element returned by the iterator. This method can be
   * called only once per call to next.
   */
  public void remove();

  /**
   * Returns the next token, without removing it, from the NameLexer, so that
   * the same token will be again returned on the next call to next() or
   * lookAhead(). This is useful for conditional decisions on sentence
   * boundaries, for example.
   * 
   * @return the next token
   * @throws java.util.NoSuchElementException
   *           if the token stream has no more tokens.
   */
  public T lookAhead();

  /**
   * Returns all tokens of this NameLexer as a List
   * 
   * @return A list of all the tokens
   */
  public List<T> nameLex();

}
