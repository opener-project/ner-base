/*
 *  Copyright 2015 Rodrigo Agerri

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

import ixa.kaflib.Entity;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.Term;
import ixa.kaflib.WF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Lists;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.Span;
import eus.ixa.ixa.pipe.nerc.dict.Dictionaries;
import eus.ixa.ixa.pipe.nerc.train.Flags;

/**
 * Annotation class of ixa-pipe-nerc.
 *
 * @author ragerri
 * @version 2015-02-25
 *
 */
public class Annotate {

  /**
   * The name factory.
   */
  private NameFactory nameFactory;
  /**
   * The NameFinder to do the annotation. Usually the statistical.
   */
  private StatisticalNameFinder nameFinder;
  /**
   * The dictionaries.
   */
  private Dictionaries dictionaries;
  /**
   * The dictionary name finder.
   */
  private DictionariesNameFinder dictFinder;
  /**
   * The NameFinder Lexer for rule-based name finding.
   */
  private NumericNameFinder numericLexerFinder;
  /**
   * True if the name finder is statistical.
   */
  private boolean statistical;
  /**
   * Activates post processing of statistical name finder with dictionary name
   * finders.
   */
  private boolean postProcess;
  /**
   * Activates name finding using dictionaries only.
   */
  private boolean dictTag;
  /**
   * Activates name finding using {@code NameFinderLexer}s.
   */
  private boolean lexerFind;
  /**
   * Clear features after every sentence or when a -DOCSTART- mark appears.
   */
  private String clearFeatures;

  /**
   * The name of the language to annotate.
   */
  private String language;

  /** It manages the use of the three different name finders: {@code StatisticalNameFinder},
   * {@code DictionariesNameFinder} and {@code NumericNameFinder}. In particular, if --dictTag
   * option in CLI is off, statistical models are used (this is the default). If --dictTag is
   * activated, it has two options, "tag" and "post": tag only tags with a gazetteer and "post"
   * post-processes the probabilistic annotation giving priority to the gazetteer.
   * Obviously, this option depends on the --dictPath parameter being correctly specified. The
   * --lexer numeric option annotates numeric entities (dates, percentages, and so on) via rules.
   * Finally, the oepc option post-process the output using the one entity per class hypothesis.
   * @param properties
   *          the properties
   * @throws IOException
   *           the io thrown
   */
  public Annotate(final Properties properties) throws IOException {

    this.clearFeatures = properties.getProperty("clearFeatures");
    nameFactory = new NameFactory();
    annotateOptions(properties);

    this.language = properties.getProperty("language");
  }

  /**
   * Generates the right options for NERC tagging: using the
   * {@link StatisticalNameFinder} or using the {@link DictionariesNameFinder}
   * or a combination of those with the {@link NumericNameFinder}.
   *
   * @param properties
   *          the parameters to choose the NameFinder are lexer, dictTag and
   *          dictPath
   * @throws IOException
   *           the io exception
   */
  // TODO surely we can simplify this?
  private void annotateOptions(Properties properties) throws IOException {

    String ruleBasedOption = properties.getProperty("ruleBasedOption");
    String dictOption = properties.getProperty("dictTag");
    String dictPath = properties.getProperty("dictPath");

    if (!dictOption.equals(Flags.DEFAULT_DICT_OPTION)) {
      if (dictPath.equals(Flags.DEFAULT_DICT_PATH)) {
        Flags.dictionaryException();
      }
      if (!ruleBasedOption.equals(Flags.DEFAULT_LEXER)) {
        lexerFind = true;
      }
      if (!dictPath.equals(Flags.DEFAULT_DICT_PATH)) {
        if (dictionaries == null) {
          dictionaries = new Dictionaries(dictPath);
          dictFinder = new DictionariesNameFinder(dictionaries, nameFactory);
        }
        if (dictOption.equalsIgnoreCase("tag")) {
          dictTag = true;
          postProcess = false;
          statistical = false;
        } else if (dictOption.equalsIgnoreCase("post")) {
          nameFinder = new StatisticalNameFinder(properties, nameFactory);
          statistical = true;
          postProcess = true;
          dictTag = false;
        } else {
          nameFinder = new StatisticalNameFinder(properties, nameFactory);
          statistical = true;
          dictTag = false;
          postProcess = false;
        }
      }
    } else if (!ruleBasedOption.equals(Flags.DEFAULT_LEXER)) {
      lexerFind = true;
      statistical = true;
      dictTag = false;
      postProcess = false;
      nameFinder = new StatisticalNameFinder(properties, nameFactory);
    } else {
      lexerFind = false;
      statistical = true;
      dictTag = false;
      postProcess = false;
      nameFinder = new StatisticalNameFinder(properties, nameFactory);
    }
  }

  /**
   * Get the statistical namefinder.
   * @return the statistical namefinder
   */
  public StatisticalNameFinder getStatisticalNameFinder() {
    return nameFinder;
  }

  /**
   * Classify Named Entities creating the entities layer in the
   * {@link KAFDocument} using statistical models, post-processing and/or
   * dictionaries only.
   *
   * @param kaf
   *          the kaf document to be used for annotation
   * @throws IOException
   *           throws exception if problems with the kaf document
   */
  public final void annotateNEs(final KAFDocument kaf) throws IOException {

    List<Span> allSpans = null;
    List<List<WF>> sentences = kaf.getSentences();
    for (List<WF> sentence : sentences) {
      //process each sentence
      String[] tokens = new String[sentence.size()];
      String[] tokenIds = new String[sentence.size()];
      for (int i = 0; i < sentence.size(); i++) {
        tokens[i] = sentence.get(i).getForm();
        tokenIds[i] = sentence.get(i).getId();
      }
      if (statistical) {
        if (clearFeatures.equalsIgnoreCase("docstart") && tokens[0].startsWith("-DOCSTART-")) {
          nameFinder.clearAdaptiveData();
        }
        Span[] statSpans = nameFinder.nercToSpans(tokens);
        allSpans = Lists.newArrayList(statSpans);
      }
      if (postProcess) {
        Span[] dictSpans = dictFinder.nercToSpansExact(tokens);
        SpanUtils.postProcessDuplicatedSpans(allSpans, dictSpans);
        SpanUtils.concatenateSpans(allSpans, dictSpans);
      }
      if (dictTag) {
        Span[] dictOnlySpans = dictFinder.nercToSpansExact(tokens);
        allSpans = Lists.newArrayList(dictOnlySpans);
      }
      if (lexerFind) {
        String sentenceText = StringUtils.getStringFromTokens(tokens);
        StringReader stringReader = new StringReader(sentenceText);
        BufferedReader sentenceReader = new BufferedReader(stringReader);
        numericLexerFinder = new NumericNameFinder(sentenceReader, nameFactory);
        Span[] numericSpans = numericLexerFinder.nercToSpans(tokens);
        SpanUtils.concatenateSpans(allSpans, numericSpans);
      }
      Span[] allSpansArray = NameFinderME.dropOverlappingSpans(allSpans
          .toArray(new Span[allSpans.size()]));
      List<Name> names = new ArrayList<Name>();
      if (statistical) {
        names = nameFinder.getNamesFromSpans(allSpansArray, tokens);
      } else {
        names = dictFinder.getNamesFromSpans(allSpansArray, tokens);
      }
      for (Name name : names) {
        Integer startIndex = name.getSpan().getStart();
        Integer endIndex = name.getSpan().getEnd();
        List<Term> nameTerms = kaf.getTermsFromWFs(Arrays.asList(Arrays
            .copyOfRange(tokenIds, startIndex, endIndex)));
        ixa.kaflib.Span<Term> neSpan = KAFDocument.newTermSpan(nameTerms);
        List<ixa.kaflib.Span<Term>> references = new ArrayList<ixa.kaflib.Span<Term>>();
        references.add(neSpan);
        Entity neEntity = kaf.newEntity(references);
        neEntity.setType(name.getType());
      }
      if (clearFeatures.equalsIgnoreCase("yes")) {
        nameFinder.clearAdaptiveData();
      }
    }
    nameFinder.clearAdaptiveData();
  }

  /**
   * Annotates the given KAF document and sets the linguisticProcessor details.
   *
   * @param enable_timestamp Whether to include a dynamic or static timestamp.
   * @param kaf The KAF document to annotate.
   */
  public final String annotateKAF(final boolean enable_timestamp, final KAFDocument kaf) throws IOException {
    String version   = Annotate.class.getPackage().getImplementationVersion();
    String processor = "ixa-pipe-nerc-" + this.language + "-default";

    if ( enable_timestamp ) {
        kaf.addLinguisticProcessor("entities", processor, version);
    }
    else {
        kaf.addLinguisticProcessor("entities", processor, "now", version);
    }

    annotateNEs(kaf);

    return annotateNEsToKAF(kaf);
  }

  /**
   * Output annotation as NAF.
   *
   * @param kaf
   *          the naf document
   * @return the string containing the naf document
   */
  public final String annotateNEsToKAF(KAFDocument kaf) {
    return kaf.toString();
  }

  /**
   * Output annotation in OpenNLP format.
   *
   * @param kaf
   *          the naf document
   * @return the string containing the annotated document
   */
  public final String annotateNEsToOpenNLP(KAFDocument kaf) {
    StringBuilder sb = new StringBuilder();
    List<Span> allSpans = null;
    List<List<WF>> sentences = kaf.getSentences();
    for (List<WF> sentence : sentences) {
      String[] tokens = new String[sentence.size()];
      String[] tokenIds = new String[sentence.size()];
      for (int i = 0; i < sentence.size(); i++) {
        tokens[i] = sentence.get(i).getForm();
        tokenIds[i] = sentence.get(i).getId();
      }
      if (statistical) {
        if (clearFeatures.equalsIgnoreCase("docstart") && tokens[0].startsWith("-DOCSTART-")) {
          nameFinder.clearAdaptiveData();
        }
        Span[] statSpans = nameFinder.nercToSpans(tokens);
        allSpans = Lists.newArrayList(statSpans);
      }
      if (postProcess) {
        Span[] dictSpans = dictFinder.nercToSpansExact(tokens);
        SpanUtils.postProcessDuplicatedSpans(allSpans, dictSpans);
        SpanUtils.concatenateSpans(allSpans, dictSpans);
      }
      if (dictTag) {
        Span[] dictOnlySpans = dictFinder.nercToSpansExact(tokens);
        allSpans = Lists.newArrayList(dictOnlySpans);
      }
      if (lexerFind) {
        String sentenceText = StringUtils.getStringFromTokens(tokens);
        StringReader stringReader = new StringReader(sentenceText);
        BufferedReader sentenceReader = new BufferedReader(stringReader);
        numericLexerFinder = new NumericNameFinder(sentenceReader, nameFactory);
        Span[] numericSpans = numericLexerFinder.nercToSpans(tokens);
        SpanUtils.concatenateSpans(allSpans, numericSpans);
      }
      boolean isClearAdaptiveData = false;
      if (clearFeatures.equalsIgnoreCase("yes")) {
        isClearAdaptiveData = true;
      }
      Span[] allSpansArray = NameFinderME.dropOverlappingSpans(allSpans
          .toArray(new Span[allSpans.size()]));
      NameSample nameSample = new NameSample(tokens, allSpansArray, isClearAdaptiveData);
      sb.append(nameSample.toString()).append("\n");
    }
    nameFinder.clearAdaptiveData();
    return sb.toString();
  }

  /**
   * Enumeration class for CoNLL 2003 BIO format
   */
  private static enum BIO {
    BEGIN("B-"), IN("I-"), OUT("O");
    String tag;

    BIO(String tag) {
      this.tag = tag;
    }

    public String toString() {
      return this.tag;
    }
  }

  /**
   * Output Conll2003 format.
   *
   * @param kaf
   *          the kaf document
   * @return the annotated named entities in conll03 format
   */
  public String annotateNEsToCoNLL2003(KAFDocument kaf) {
    List<Entity> namedEntityList = kaf.getEntities();
    Map<String, Integer> entityToSpanSize = new HashMap<String, Integer>();
    Map<String, String> entityToType = new HashMap<String, String>();
    for (Entity ne : namedEntityList) {
      List<ixa.kaflib.Span<Term>> entitySpanList = ne.getSpans();
      for (ixa.kaflib.Span<Term> spanTerm : entitySpanList) {
        Term neTerm = spanTerm.getFirstTarget();
        // create map from term Id to Entity span size
        entityToSpanSize.put(neTerm.getId(), spanTerm.size());
        // create map from term Id to Entity type
        entityToType.put(neTerm.getId(), ne.getType());
      }
    }

    List<List<WF>> sentences = kaf.getSentences();
    StringBuilder sb = new StringBuilder();
    for (List<WF> sentence : sentences) {
      int sentNumber = sentence.get(0).getSent();
      List<Term> sentenceTerms = kaf.getSentenceTerms(sentNumber);
      String previousType = null;
      boolean previousIsEntity = false;

      for (int i = 0; i < sentenceTerms.size(); i++) {
        Term thisTerm = sentenceTerms.get(i);
        // if term is inside an entity span then annotate B-I entities
        if (entityToSpanSize.get(thisTerm.getId()) != null) {
          int neSpanSize = entityToSpanSize.get(thisTerm.getId());
          String neClass = entityToType.get(thisTerm.getId());
          String neType = this.convertToConLLTypes(neClass);
          // if Entity span is multi token
          if (neSpanSize > 1) {
            for (int j = 0; j < neSpanSize; j++) {
              thisTerm = sentenceTerms.get(i + j);
              sb.append(thisTerm.getForm());
              sb.append("\t");
              sb.append(thisTerm.getLemma());
              sb.append("\t");
              sb.append(thisTerm.getMorphofeat());
              sb.append("\t");
              if (j == 0 && previousIsEntity && previousType.equalsIgnoreCase(neType)) {
                sb.append(BIO.BEGIN.toString());
              } else {
                sb.append(BIO.IN.toString());
              }
              sb.append(neType);
              sb.append("\n");
            }
            previousType = neType;
          } else {
            sb.append(thisTerm.getForm());
            sb.append("\t");
            sb.append(thisTerm.getLemma());
            sb.append("\t");
            sb.append(thisTerm.getMorphofeat());
            sb.append("\t");
            if (previousIsEntity && previousType.equalsIgnoreCase(neType)) {
              sb.append(BIO.BEGIN.toString());
            } else {
              sb.append(BIO.IN.toString());
            }
            sb.append(neType);
            sb.append("\n");
          }
          previousIsEntity = true;
          previousType = neType;
          i += neSpanSize - 1;
        } else {
          sb.append(thisTerm.getForm());
          sb.append("\t");
          sb.append(thisTerm.getLemma());
          sb.append("\t");
          sb.append(thisTerm.getMorphofeat());
          sb.append("\t");
          sb.append(BIO.OUT);
          sb.append("\n");
          previousIsEntity = false;
          previousType = BIO.OUT.toString();
        }
      }
      sb.append("\n");// end of sentence
    }
    return sb.toString();
  }

  /**
   * Output Conll2002 format.
   *
   * @param kaf
   *          the kaf document
   * @return the annotated named entities in conll03 format
   */
  public String annotateNEsToCoNLL2002(KAFDocument kaf) {
    List<Entity> namedEntityList = kaf.getEntities();
    Map<String, Integer> entityToSpanSize = new HashMap<String, Integer>();
    Map<String, String> entityToType = new HashMap<String, String>();
    for (Entity ne : namedEntityList) {
      List<ixa.kaflib.Span<Term>> entitySpanList = ne.getSpans();
      for (ixa.kaflib.Span<Term> spanTerm : entitySpanList) {
        Term neTerm = spanTerm.getFirstTarget();
        entityToSpanSize.put(neTerm.getId(), spanTerm.size());
        entityToType.put(neTerm.getId(), ne.getType());
      }
    }

    List<List<WF>> sentences = kaf.getSentences();
    StringBuilder sb = new StringBuilder();
    for (List<WF> sentence : sentences) {
      int sentNumber = sentence.get(0).getSent();
      List<Term> sentenceTerms = kaf.getSentenceTerms(sentNumber);

      for (int i = 0; i < sentenceTerms.size(); i++) {
        Term thisTerm = sentenceTerms.get(i);

        if (entityToSpanSize.get(thisTerm.getId()) != null) {
          int neSpanSize = entityToSpanSize.get(thisTerm.getId());
          String neClass = entityToType.get(thisTerm.getId());
          String neType = convertToConLLTypes(neClass);
          if (neSpanSize > 1) {
            for (int j = 0; j < neSpanSize; j++) {
              thisTerm = sentenceTerms.get(i + j);
              sb.append(thisTerm.getForm());
              sb.append("\t");
              sb.append(thisTerm.getLemma());
              sb.append("\t");
              sb.append(thisTerm.getMorphofeat());
              sb.append("\t");
              if (j == 0) {
                sb.append(BIO.BEGIN.toString());
              } else {
                sb.append(BIO.IN.toString());
              }
              sb.append(neType);
              sb.append("\n");
            }
          } else {
            sb.append(thisTerm.getForm());
            sb.append("\t");
            sb.append(thisTerm.getLemma());
            sb.append("\t");
            sb.append(thisTerm.getMorphofeat());
            sb.append("\t");
            sb.append(BIO.BEGIN.toString());
            sb.append(neType);
            sb.append("\n");
          }
          i += neSpanSize - 1;
        } else {
          sb.append(thisTerm.getForm());
          sb.append("\t");
          sb.append(thisTerm.getLemma());
          sb.append("\t");
          sb.append(thisTerm.getMorphofeat());
          sb.append("\t");
          sb.append(BIO.OUT);
          sb.append("\n");
        }
      }
      sb.append("\n");// end of sentence
    }
    return sb.toString();
  }

  /**
   * Convert Entity class annotation to CoNLL formats.
   *
   * @param neType
   *          named entity class
   * @return the converted string
   */
  public String convertToConLLTypes(String neType) {
    String conllType = null;
    if (neType.equalsIgnoreCase("PERSON") || neType.equalsIgnoreCase("ORGANIZATION")
        || neType.equalsIgnoreCase("LOCATION") || neType.length() == 3) {
      conllType = neType.substring(0, 3);
    } else {
      conllType = neType;
    }
    return conllType;
  }
}
