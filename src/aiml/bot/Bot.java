/*
    jaiml - java AIML library
    Copyright (C) 2004-2008  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package aiml.bot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import aiml.context.ContextInfo;
import aiml.context.EnvironmentInputContext;
import aiml.context.StringContext;
import aiml.environment.Environment;
import aiml.parser.AIMLParser;
import aiml.parser.AimlParserException;
import aiml.substitutions.DuplicateSubstitutionException;
import aiml.substitutions.Substitutions;

public class Bot {
  private static final HashSet<String> PREDEFINED_SUBSTITUTIONS = new HashSet<String>(
      Arrays.asList("input", "gender", "person", "person2"));
  private String name;
  private HashMap<String, String> properties = new HashMap<String, String>();
  private HashMap<String, Substitutions> substitutions = new HashMap<String, Substitutions>();
  private boolean standalone = false;
  private boolean enabled = false;

  private Logger logger = Logger.getLogger(this.getClass().getName());

  private XmlPullParser parser;
  public static final String UNKNOWN_PROPERTY = "";

  public Bot() throws XmlPullParserException {
    super();
    parser = XmlPullParserFactory.newInstance().newPullParser();
  }

  public Bot(String name) throws XmlPullParserException {
    this();
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
    parser.setInput(new FileInputStream(file), "UTF-8");
    try {
      parser.setProperty("http://xmlpull.org/v1/doc/properties.html#location",
          file);
    } catch (XmlPullParserException e) {
    }
    require(XmlPullParser.START_DOCUMENT, null);
    parser.next();
    standalone = true;
    doBot();
    try {
      require(XmlPullParser.END_DOCUMENT, null);
    } catch (BotSyntaxException e) {
      throw new BotSyntaxException(
          "Syntax error: no markup allowed after end of root element " +
              parser.getPositionDescription());
    }
  }

  public void load(XmlPullParser parser) throws BotSyntaxException,
      XmlPullParserException, IOException, AimlParserException {
    XmlPullParser oldParser = this.parser;
    this.parser = parser;
    standalone = false;
    doBot();
    this.parser = oldParser;
  }

  private boolean isEvent(int eventType, String name)
      throws XmlPullParserException {
    return (parser.getEventType() == eventType && (name == null || parser.getName().equals(
        name)));
  }

  private void require(int eventType, String name, String failMessage)
      throws XmlPullParserException, BotSyntaxException {
    if ((parser.getEventType() == eventType && (name == null || parser.getName().equals(
        name))))
      return;
    else
      throw new BotSyntaxException("Syntax error: " + failMessage + " " +
          parser.getPositionDescription());
  }

  private void require(int eventType, String name) throws BotSyntaxException,
      XmlPullParserException {
    if (parser == null)
      throw new IllegalArgumentException();
    if ((parser.getEventType() == eventType && (name == null || parser.getName().equals(
        name))))
      return;
    else
      throw new BotSyntaxException("Syntax error: expected " +
          XmlPullParser.TYPES[eventType] + " '" + name + "' " +
          parser.getPositionDescription());
  }

  private String requireAttrib(String name) throws BotSyntaxException {
    if (parser.getAttributeValue(null, name) == null)
      throw new BotSyntaxException("Syntax error: mandatory attribute '" +
          name + "' missing from element '" + parser.getName() + "' " +
          parser.getPositionDescription());
    else
      return parser.getAttributeValue(null, name);
  }

  private void doBot() throws BotSyntaxException, XmlPullParserException,
      IOException, AimlParserException {
    require(XmlPullParser.START_TAG, "bot");
    String id = requireAttrib("id");

    if (!standalone && name != null && !id.equals(name)) {
      throw new BotSyntaxException("Syntax error: bot ID mismatch " +
          parser.getPositionDescription());
    } else
      name = id;

    String enabledAttr = requireAttrib("enabled");
    if (!standalone && enabledAttr.equals("false")) {
      enabled = false;
    } else {
      enabled = true;
    }

    parser.nextTag();
    if (isEvent(XmlPullParser.START_TAG, "properties")) {
      doProperties();
    }
    if (isEvent(XmlPullParser.START_TAG, "predicates")) {
      doPredicates();
    }
    if (isEvent(XmlPullParser.START_TAG, "substitutions")) {
      doSubstitutions();
    }
    if (isEvent(XmlPullParser.START_TAG, "sentence-splitters")) {
      doSentenceSplitters();
    }
    if (isEvent(XmlPullParser.START_TAG, "listeners")) {
      doListeners();
    }
    if (isEvent(XmlPullParser.START_TAG, "contexts")) {
      doContexts();
    } else {
      // TODO provide actual implementations of topics...
      ContextInfo.registerContext(new EnvironmentInputContext("input"));
      ContextInfo.registerContext(new StringContext("that", "dummy that"));
      ContextInfo.registerContext(new StringContext("topic", "dummy topic"));
    }
    doLearn();

    require(XmlPullParser.END_TAG, "bot");
    parser.next();
  }

  private void doLearn() throws XmlPullParserException, IOException,
      BotSyntaxException, AimlParserException {
    while (isEvent(XmlPullParser.START_TAG, "learn")) {
      File file = new File(parser.nextText());
      require(XmlPullParser.END_TAG, "learn");
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

  private void doSentenceSplitters() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("doSentenceSplitters()");

  }

  private void doSubstitutions() throws BotSyntaxException,
      XmlPullParserException, IOException {
    require(XmlPullParser.START_TAG, "substitutions");
    parser.nextTag();
    while (isEvent(XmlPullParser.START_TAG, null)) {
      if (PREDEFINED_SUBSTITUTIONS.contains(parser.getName())) {
        doSubstitutions(parser.getName(), parser.getName());
      } else if ("custom".equals(parser.getName())) {
        doSubstitutions(requireAttrib("name"), parser.getName());
      } else {
        throw new BotSyntaxException(
            "Syntax error: unknown substitution list definition \"" +
                parser.getName() + "\" " + parser.getPositionDescription());
      }
    }
    require(XmlPullParser.END_TAG, "substitutions");
    parser.nextTag();
  }

  private void doSubstitutions(String listName, String listTag)
      throws BotSyntaxException, XmlPullParserException, IOException {
    if (substitutions.containsKey(listName)) {
      throw new BotSyntaxException("Syntax error: substitution list \"" +
          listName + "\" already defined " + parser.getPositionDescription());
    }
    LinkedHashMap<String, String> substMap = new LinkedHashMap<String, String>();
    parser.nextTag();
    while (isEvent(XmlPullParser.START_TAG, "substitute")) {
      substMap.put(requireAttrib("find"), requireAttrib("replace"));

      parser.nextTag();
      require(XmlPullParser.END_TAG, "substitute");
      parser.nextTag();
    }
    require(XmlPullParser.END_TAG, listTag);
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

  private void doProperties() throws BotSyntaxException,
      XmlPullParserException, IOException {
    require(XmlPullParser.START_TAG, "properties");
    parser.nextTag();
    while (isEvent(XmlPullParser.START_TAG, "property")) {
      doProperty();
    }
    require(XmlPullParser.END_TAG, "properties");
    parser.nextTag();
  }

  private void doProperty() throws BotSyntaxException, XmlPullParserException,
      IOException {
    require(XmlPullParser.START_TAG, "property");
    if (!parser.isEmptyElementTag()) {
      throw new BotSyntaxException(
          "Syntax error: bot property definition must be empty " +
              parser.getPositionDescription());
    }
    String propName = requireAttrib("name");
    String propValue = requireAttrib("value");
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

}
