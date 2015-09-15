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
 * This class provides the functionality to create {@link Name} objects. E.g.,
 * the {@link Annotate} class uses the createName functions of this class to
 * create every {@link Name}
 *
 * @author ragerri
 * @version 2014-03-12
 *
 */

public class NameFactory {

  /**
   * Constructs a {@link Name} as a String with a class type (e.g. Person,
   * location, organization, etc.)
   *
   * @param nameString
   *          string to be added to a Name object
   * @param neType
   *          the type of the Name
   * @return a new Name object
   *
   */
  public final Name createName(final String nameString, final String neType) {
    Name name = new Name();
    name.setValue(nameString);
    name.setType(neType);
    return name;
  }

  /**
   * Constructs a {@link Name} as a String with a type and a {@link Span}
   * specified in terms of the number of tokens it contains.
   *
   * @param nameString
   *          string to be added to a Name object
   * @param neType
   *          the type of the Name
   * @param neSpan
   *          the span of the Name
   * @return a new Name object
   *
   */

  public final Name createName(final String nameString, final String neType,
      final Span neSpan) {
    Name name = new Name();
    name.setValue(nameString);
    name.setType(neType);
    name.setSpan(neSpan);
    return name;
  }

  /**
   * Constructs a {@link Name} as a String with corresponding offsets and length
   * from which to calculate start and end position of the Name.
   *
   * @param nameString
   *          string to be added to a Name object
   * @param neType
   *          the type of the Name
   * @param offset
   *          the starting offset of the Name
   * @param length
   *          of the string
   * @return a new Name object
   *
   */
  public final Name createName(final String nameString, final String neType,
      final int offset, final int length) {
    Name name = new Name();
    name.setValue(nameString);
    name.setType(neType);
    name.setStartOffset(offset);
    name.setNameLength(length);
    return name;
  }

  /**
   * Constructs a Name as a String with corresponding offsets and length from
   * which to calculate start and end position of the Name.
   *
   * @param nameString
   *          string to be added to a Name object
   * @param neType
   *          the type of the Name
   * @param neSpan the Span
   * @param offset
   *          the starting offset of the Name
   * @param length
   *          of the string
   * @return a new Name object
   *
   */
  public final Name createName(final String nameString, final String neType,
      final Span neSpan, final int offset, final int length) {
    Name name = new Name();
    name.setValue(nameString);
    name.setType(neType);
    name.setSpan(neSpan);
    name.setStartOffset(offset);
    name.setNameLength(length);
    return name;
  }

}
