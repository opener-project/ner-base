/*
 *Copyright 2014 Rodrigo Agerri

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

package eus.ixa.ixa.pipe.nerc;

import opennlp.tools.util.Span;

/**
 * A <code>Name</code> object contains a single String, a {@link Span}, a
 * startOffset and the length of the String. These attributes are set or
 * returned in response to requests.
 *
 * @author ragerri
 * @version 2013-03-12
 *
 */
public class Name {

  /**
   * The string of the name.
   */
  private String str;

  /**
   * The {@link Span} of the Name.
   */
  private Span neSpan;
  /**
   * The Name Entity class.
   */
  private String type;

  /**
   * Start position of the <code>Name</code> in the original input string.
   */
  private int startOffset = -1;

  /**
   * Length of the Name in the original input string.
   */
  private int nameLength = -1;

  /**
   * Create a new <code>Name</code> with a null content (i.e., str).
   */
  public Name() {
  }

  /**
   * Create a new <code>Name</code> with the given string.
   *
   * @param aStr
   *          the new label's content
   * @param aType
   *          the class of the name
   */
  public Name(final String aStr, final String aType) {
    this.str = aStr;
    this.type = aType.toUpperCase();
  }

  /**
   * Create a new <code>Name</code> with the given string and Span.
   *
   * @param aStr
   *          the new label's content
   * @param aType
   *          the Named Entity class of the Name
   * @param aNeSpan
   *          the span of the Name
   */
  public Name(final String aStr, final String aType, final Span aNeSpan) {
    this.str = aStr;
    this.type = aType.toUpperCase();
    this.neSpan = aNeSpan;
  }

  /**
   * Creates a new <code>Name</code> with the given content.
   *
   * @param aStr
   *          The new label's content
   * @param aType
   *          Named Entity class of the Name
   * @param aStartOffset
   *          Start offset in original text
   * @param aNameLength
   *          End offset in original text
   */
  public Name(final String aStr, final String aType,
      final int aStartOffset, final int aNameLength) {
    this.str = aStr;
    this.type = aType.toUpperCase();
    setStartOffset(aStartOffset);
    setNameLength(aNameLength);
  }

  /**
   * Creates a new <code>Name</code> with the given content.
   *
   * @param aStr
   *          The new label's content
   * @param aType
   *          Name Entity class of the Name
   * @param aNeSpan
   *          the name span
   * @param aStartOffset
   *          Start offset in original text
   * @param aNameLength
   *          End offset in original text
   */
  public Name(final String aStr, final String aType, final Span aNeSpan,
      final int aStartOffset, final int aNameLength) {
    this.str = aStr;
    this.type = aType.toUpperCase();
    this.neSpan = aNeSpan;
    setStartOffset(aStartOffset);
    setNameLength(aNameLength);
  }

  /**
   * Return the word value of the label (or null if none).
   *
   * @return String the word value for the label
   */
  public final String value() {
    return str;
  }

  /**
   * Return the type of the Name.
   *
   * @return the type of the Name
   */
  public final String getType() {
    return type;
  }

  /**
   * Return the Span (or null if none).
   *
   * @return the Span
   */
  public final Span getSpan() {
    return neSpan;
  }

  /**
   * Set the value for the label.
   *
   * @param value
   *          The value for the label
   */
  public final void setValue(final String value) {
    str = value;
  }

  /**
   * Set type of the Name.
   *
   * @param neType
   *          the class of the Name
   */
  public final void setType(final String neType) {
    type = neType.toUpperCase();
  }

  /**
   * Set the Span for the Name.
   *
   * @param span
   *          the Span of the name
   */
  public final void setSpan(final Span span) {
    neSpan = span;
  }

  /**
   * Set the label from a String.
   *
   * @param aStr
   *          The str for the label
   */
  public final void setFromString(final String aStr) {
    this.str = aStr;
  }

  @Override
  public final String toString() {
    return str;
  }

  /**
   * @return the starting offset
   */
  public final int startOffset() {
    return startOffset;
  }

  /**
   * @return the length in characters of the name
   */
  public final int nameLength() {
    return nameLength;
  }

  /**
   * @param beginPosition
   *          the starting character
   */
  public final void setStartOffset(final int beginPosition) {
    this.startOffset = beginPosition;
  }

  /**
   * @param aNameLength
   *          the length of the name
   */
  public final void setNameLength(final int aNameLength) {
    this.nameLength = aNameLength;
  }
}
