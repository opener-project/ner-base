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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.Files;

import opennlp.tools.util.Span;

/**
 * Pattern matching and other utility string functions.
 *
 * @author ragerri
 * @version 2013-03-19
 */
public final class StringUtils {

  /**
   * Private constructor.
   */
  private StringUtils() {
    throw new AssertionError("This class is not meant to be instantiated!");
  }

  /**
   * Finds a pattern (typically a named entity string) in a tokenized sentence.
   * It outputs the {@link Span} indexes of the named entity found, if any.
   *
   * @param pattern
   *          a string to find
   * @param tokens
   *          an array of tokens
   * @return token spans of the pattern (e.g. a named entity)
   */
  public static List<Integer> exactTokenFinderIgnoreCase(final String pattern,
      final String[] tokens) {
    String[] patternTokens = pattern.split(" ");
    int i, j;
    int patternLength = patternTokens.length;
    int sentenceLength = tokens.length;
    List<Integer> neTokens = new ArrayList<Integer>();
    for (j = 0; j <= sentenceLength - patternLength; ++j) {
      for (i = 0; i < patternLength && patternTokens[i].equalsIgnoreCase(tokens[i + j]); ++i);
      if (i >= patternLength) {
        neTokens.add(j);
        neTokens.add(i + j);
      }
    }
    return neTokens;
  }

  /**
   * Finds a pattern (typically a named entity string) in a tokenized sentence.
   * It outputs the {@link Span} indexes of the named entity found, if any
   *
   * @param pattern
   *          a string to find
   * @param tokens
   *          an array of tokens
   * @return token spans of the pattern (e.g. a named entity)
   */
  public static List<Integer> exactTokenFinder(final String pattern,
      final String[] tokens) {
    String[] patternTokens = pattern.split(" ");
    int i, j;
    int patternLength = patternTokens.length;
    int sentenceLength = tokens.length;
    List<Integer> neTokens = new ArrayList<Integer>();
    for (j = 0; j <= sentenceLength - patternLength; ++j) {
      for (i = 0; i < patternLength && patternTokens[i].equals(tokens[i + j]); ++i);
      if (i >= patternLength) {
        neTokens.add(j);
        neTokens.add(i + j);
      }
    }
    return neTokens;
  }

  /**
   * Finds a pattern (typically a named entity string) in a sentence string. It
   * outputs the offsets for the start and end characters named entity found, if
   * any.
   *
   * @param pattern
   *          the pattern to be searched
   * @param sentence
   *          the sentence
   * @return a list of integers corresponding to the characters of the string
   *         found
   */
  public static List<Integer> exactStringFinder(final String pattern,
      final String sentence) {
    char[] patternArray = pattern.toCharArray(), sentenceArray = sentence
        .toCharArray();
    int i, j;
    int patternLength = patternArray.length;
    int sentenceLength = sentenceArray.length;
    List<Integer> neChars = new ArrayList<Integer>();
    for (j = 0; j <= sentenceLength - patternLength; ++j) {
      for (i = 0; i < patternLength && patternArray[i] == sentenceArray[i + j]; ++i);
      if (i >= patternLength) {
        neChars.add(j);
        neChars.add(i + j);
      }
    }
    return neChars;
  }

  /**
   *
   * It takes a NE span indexes and the tokens in a sentence and produces the
   * string to which the NE span corresponds to. This function is used to get
   * the Named Entity or Name textual representation from a {@link Span}
   *
   * @param reducedSpan
   *          a {@link Span}
   * @param tokens
   *          an array of tokens
   * @return named entity string
   */
  public static String getStringFromSpan(final Span reducedSpan,
      final String[] tokens) {
    StringBuilder sb = new StringBuilder();
    for (int si = reducedSpan.getStart(); si < reducedSpan.getEnd(); si++) {
      sb.append(tokens[si]).append(" ");
    }
    return sb.toString().trim();
  }

  /**
   * Gets the String joined by a space of an array of tokens.
   *
   * @param tokens
   *          an array of tokens representing a tokenized sentence
   * @return sentence the sentence corresponding to the tokens
   */
  public static String getStringFromTokens(final String[] tokens) {
    StringBuilder sb = new StringBuilder();
    for (String tok : tokens) {
      sb.append(tok).append(" ");
    }
    return sb.toString().trim();
  }
  
  /**
   * Recursively get every file in a directory and add them to a list.
   * 
   * @param inputPath
   *          the input directory
   * @return the list containing all the files
   */
  public static List<File> getFilesInDir(File inputPath) {
    List<File> fileList = new ArrayList<File>();
    for (File aFile : Files.fileTreeTraverser().preOrderTraversal(inputPath)) {
      if (aFile.isFile()) {
        fileList.add(aFile);
      }
    }
    return fileList;
  }

}
