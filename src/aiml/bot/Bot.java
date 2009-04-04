/*
    jaiml - java AIML library
    Copyright (C) 2004, 2009  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package aiml.bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import aiml.classifier.Classifier;
import aiml.context.Context;
import aiml.context.ContextInfo;
import aiml.context.InputContext;
import aiml.context.data.EnvironmentInputSource;
import aiml.context.data.ResponseHistorySource;
import aiml.context.data.VariableSource;
import aiml.environment.Environment;
import aiml.parser.AIMLParser;
import aiml.parser.AimlParserException;
import aiml.parser.CheckingParser;
import aiml.script.Formatter;
import aiml.substitutions.DuplicateSubstitutionException;
import aiml.substitutions.Substitutions;
import aiml.text.ICUSentenceSplitter;
import aiml.text.SentenceSplitter;

public class Bot {
  private static final HashSet<String> PREDEFINED_SUBSTITUTIONS = new HashSet<String>(
      Arrays.asList("input", "gender", "person", "person2"));
  private String name;
  private HashMap<String, String> properties = new HashMap<String, String>();
  private HashMap<String, Substitutions> substitutions = new HashMap<String, Substitutions>();
  private boolean standalone = false;
  private boolean enabled = false;

  private Logger logger = Logger.getLogger(this.getClass().getName());

  private CheckingParser parser;
  private SentenceSplitter sentenceSplitter;
  private Classifier classifier;
  public static final String UNKNOWN_PROPERTY = "";

  public Bot(Classifier classifier) throws XmlPullParserException {
    super();
    parser = new CheckingParser(
        XmlPullParserFactory.newInstance().newPullParser(),
        BotSyntaxException.class);
    this.classifier = classifier;
  }

  public Bot(Classifier classifier, String name) throws XmlPullParserException,
      AimlParserException {
    this(classifier);
    this.name = name;
  }

  public void setProperty(String name, String value) {
    properties.put(name, value);
  }

  public String getProperty(String name) throws InvalidPropertyException {
    if (properties.containsKey(name))
      return properties.get(name);
    else
      throw new InvalidPropertyException("Bot property '" + name +
          "' must be defined for bot '" + this.name + "'");
  }

  public boolean hasProperty(String name) {
    return properties.containsKey(name);
  }

  public String getName() {
    return name;
  }

  /**
   * @return the classifier
   */
  public Classifier getClassifier() {
    return classifier;
  }

  public String applySubstitutions(String list, String text)
      throws InvalidSubstitutionException {
    if (substitutions.containsKey(list)) {
      return substitutions.get(list).apply(text);
    }
    throw new InvalidSubstitutionException("Substitution list '" + list +
        "' must be defined for bot '" + name + "'");
  }

  public void load(String file) throws XmlPullParserException, IOException,
      BotSyntaxException, AimlParserException {
    FileInputStream inputStream = null;
    try {
      inputStream = new FileInputStream(file);
      parser.setInput(inputStream, "UTF-8");
      try {
        parser.setProperty(
            "http://xmlpull.org/v1/doc/properties.html#location", file);
      } catch (XmlPullParserException e) {
      }
      parser.require(XmlPullParser.START_DOCUMENT);
      parser.next();
      standalone = true;
      doBot();
      try {
        parser.require(XmlPullParser.END_DOCUMENT);
      } catch (BotSyntaxException e) {
        throw new BotSyntaxException(
            "Syntax error: no markup allowed after end of root element " +
                parser.getPositionDescription());
      }
    } finally {
      if (inputStream != null)
        inputStream.close();
    }
  }

  public void load(CheckingParser parser) throws BotSyntaxException,
      XmlPullParserException, IOException, AimlParserException {
    CheckingParser oldParser = this.parser;
    this.parser = parser;
    standalone = false;
    doBot();
    this.parser = oldParser;
  }

  private void doBot() throws BotSyntaxException, XmlPullParserException,
      IOException, AimlParserException {
    parser.require(XmlPullParser.START_TAG, "bot");
    String id = parser.requireAttrib("id");

    if (!standalone && name != null && !id.equals(name)) {
      throw new BotSyntaxException("Syntax error: bot ID mismatch " +
          parser.getPositionDescription());
    } else
      name = id;

    String enabledAttr = parser.requireAttrib("enabled");
    if (!standalone && enabledAttr.equals("false")) {
      enabled = false;
    } else {
      enabled = true;
    }

    parser.nextTag();
    if (parser.isEvent(XmlPullParser.START_TAG, "properties")) {
      doProperties();
    }
    if (parser.isEvent(XmlPullParser.START_TAG, "predicates")) {
      doPredicates();
    }
    if (parser.isEvent(XmlPullParser.START_TAG, "substitutions")) {
      doSubstitutions();
    }
    if (parser.isEvent(XmlPullParser.START_TAG, "sentence-splitters")) {
      doSentenceSplitters();
    }
    if (parser.isEvent(XmlPullParser.START_TAG, "listeners")) {
      doListeners();
    }
    if (parser.isEvent(XmlPullParser.START_TAG, "contexts")) {
      doContexts();
    } else {
      // TODO provide actual implementations of topics...
      ContextInfo contextInfo = classifier.getContextInfo();
      contextInfo.registerContext(new InputContext("input",
          new EnvironmentInputSource()));
      contextInfo.registerContext(new Context<String>("that",
          new ResponseHistorySource()));
      contextInfo.registerContext(new Context<String>("topic",
          new VariableSource("topic")));
    }

    if (sentenceSplitter == null) {
      logger.info("No explicit sentence splitting rules defined, using default sentence splitter");
      sentenceSplitter = new ICUSentenceSplitter();
    }

    doLearn();

    parser.require(XmlPullParser.END_TAG, "bot");
    parser.next();
  }

  private void doLearn() throws XmlPullParserException, IOException,
      BotSyntaxException, AimlParserException {
    while (parser.isEvent(XmlPullParser.START_TAG, "learn")) {
      File file = new File(parser.nextText());
      parser.require(XmlPullParser.END_TAG, "learn");
      if (!file.exists() || !file.isFile()) {
        logger.warning("file " + file + " does not exist");
      } else {
        logger.info("Loading file " + file);
        new AIMLParser(this).load(file.getPath(), "UTF-8");
      }
      parser.nextTag();
    }
  }

  private void doContexts() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("doContexts()");

  }

  private void doListeners() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("doListeners()");

  }

  private void doSentenceSplitters() throws XmlPullParserException,
      AimlParserException, IOException {
    parser.require(XmlPullParser.START_TAG, "sentence-splitters");
    parser.nextTag();
    boolean splitter = false;
    String rules = null;
    while (parser.isEvent(XmlPullParser.START_TAG)) {
      parser.require(XmlPullParser.START_TAG, new String[] { "rules",
          "splitter" });
      if ("rules".equals(parser.getName())) {
        if (rules == null) {
          String ruleFile = parser.getAttributeValue(null, "src");
          if (ruleFile != null) {
            try {
              StringBuilder fileData = new StringBuilder(1000);
              BufferedReader reader = new BufferedReader(new FileReader(
                  ruleFile));
              char[] buf = new char[1024];
              int numRead = 0;
              while ((numRead = reader.read(buf)) != -1) {
                fileData.append(buf, 0, numRead);
              }
              reader.close();
              rules = fileData.toString();
            } catch (IOException e) {
              throw new BotSyntaxException(
                  "Error loading sentence splitting rules from file " +
                      parser.getPositionDescription(), e);
            }
            parser.nextTag();
          } else {
            rules = parser.nextText();
          }
          parser.require(XmlPullParser.END_TAG, "rules");
          parser.nextTag();
        } else {
          throw new BotSyntaxException(
              "Syntax error: only one sentence splitting ruleset allowed " +
                  parser.getPositionDescription());
        }

      } else if ("splitter".equals(parser.getName())) {
        if (!splitter) {
          logger.warning("String based sentence splitter not supported, please use sentence splitting rules " +
              parser.getPositionDescription());
          splitter = true;
        }
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, "splitter");
        parser.nextTag();
      }
    }

    if (rules != null) {
      sentenceSplitter = new ICUSentenceSplitter(rules);
    }
    parser.require(XmlPullParser.END_TAG, "sentence-splitters");
    parser.nextTag();
  }

  private void doSubstitutions() throws XmlPullParserException, IOException,
      AimlParserException {
    parser.require(XmlPullParser.START_TAG, "substitutions");
    parser.nextTag();
    while (parser.isEvent(XmlPullParser.START_TAG)) {
      if (PREDEFINED_SUBSTITUTIONS.contains(parser.getName())) {
        doSubstitutions(parser.getName(), parser.getName());
      } else if ("custom".equals(parser.getName())) {
        doSubstitutions(parser.requireAttrib("name"), parser.getName());
      } else {
        throw new BotSyntaxException(
            "Syntax error: unknown substitution list definition \"" +
                parser.getName() + "\" " + parser.getPositionDescription());
      }
    }
    parser.require(XmlPullParser.END_TAG, "substitutions");
    parser.nextTag();
  }

  private void doSubstitutions(String listName, String listTag)
      throws XmlPullParserException, IOException, AimlParserException {
    if (substitutions.containsKey(listName)) {
      throw new BotSyntaxException("Syntax error: substitution list \"" +
          listName + "\" already defined " + parser.getPositionDescription());
    }
    LinkedHashMap<String, String> substMap = new LinkedHashMap<String, String>();
    parser.nextTag();
    while (parser.isEvent(XmlPullParser.START_TAG, "substitute")) {
      substMap.put(parser.requireAttrib("find"),
          parser.requireAttrib("replace"));

      parser.nextTag();
      parser.require(XmlPullParser.END_TAG, "substitute");
      parser.nextTag();
    }
    parser.require(XmlPullParser.END_TAG, listTag);
    parser.nextTag();

    if (substMap.size() == 0) {
      logger.warning("substitution list \"" + listName + "\" is empty");
    }

    try {
      substitutions.put(listName, new Substitutions(substMap));
    } catch (DuplicateSubstitutionException e) {
      throw new BotSyntaxException("Syntax error: substitution list \"" +
          listName + "\" has duplicate entries");
    }

  }

  private void doPredicates() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("doPredicates()");

  }

  private void doProperties() throws XmlPullParserException, IOException,
      AimlParserException {
    parser.require(XmlPullParser.START_TAG, "properties");
    parser.nextTag();
    while (parser.isEvent(XmlPullParser.START_TAG, "property")) {
      doProperty();
    }
    parser.require(XmlPullParser.END_TAG, "properties");
    parser.nextTag();
  }

  private void doProperty() throws XmlPullParserException, IOException,
      AimlParserException {
    parser.require(XmlPullParser.START_TAG, "property");
    if (!parser.isEmptyElementTag()) {
      throw new BotSyntaxException(
          "Syntax error: bot property definition must be empty " +
              parser.getPositionDescription());
    }
    String propName = parser.requireAttrib("name");
    String propValue = parser.requireAttrib("value");
    setProperty(propName, propValue);
    parser.nextTag();//no need to check for correct end tag, because it is empty
    parser.nextTag();

  }

  /**
   * Creates a new single user environment for this bot.
   * 
   * @return A new Environment
   */
  public Environment createEnvironment() {
    return new Environment(this);
  }

  /**
   * Apply input preprocessing and sentence splitting
   * 
   * @param input
   *          The user input
   * @return A list of preprocessed sentences.
   */
  public List<String> preprocessInput(String input) {
    ArrayList<String> result = new ArrayList<String>();
    if (substitutions.containsKey("input")) {
      input = substitutions.get("input").apply(input);
    }
    for (String sentence : sentenceSplitter.split(input)) {
      sentence = Formatter.collapseWhitespace(sentence);
      sentence = Formatter.trimPunctiation(sentence);
      result.add(sentence);
    }
    if (result.size() == 0) {
      result.add("");
    }
    return result;
  }

  public SentenceSplitter getSentenceSplitter() {
    return sentenceSplitter;
  }

}
