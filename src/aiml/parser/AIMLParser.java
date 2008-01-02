package aiml.parser;

/**
 * <p>Title: AIML Pull Parser</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author Kim Sullivan
 * @version 1.0
 */

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import aiml.bot.Bot;
import aiml.bot.InvalidPropertyException;
import aiml.classifier.Classifier;
import aiml.classifier.DuplicatePathException;
import aiml.classifier.MultipleContextsException;
import aiml.classifier.Path;
import aiml.script.Block;
import aiml.script.Script;

public class AIMLParser {

  Logger log = Logger.getLogger(AIMLParser.class.getName());
  XmlPullParser parser;
  Bot bot;
  Path currentPath;

  public AIMLParser(Bot bot) throws XmlPullParserException {
    parser = XmlPullParserFactory.newInstance().newPullParser();
    this.bot = bot;
  }

  private boolean isEvent(int eventType, String name)
      throws XmlPullParserException {
    return (parser.getEventType() == eventType && (name == null || parser.getName().equals(
        name)));
  }

  private void require(int eventType, String name, String failMessage)
      throws AimlSyntaxException, XmlPullParserException {
    if ((parser.getEventType() == eventType && (name == null || parser.getName().equals(
        name))))
      return;
    else
      throw new AimlSyntaxException("Syntax error: " + failMessage + " " +
          parser.getPositionDescription());
  }

  private void require(int eventType, String name) throws AimlSyntaxException,
      XmlPullParserException {
    if (parser == null)
      throw new IllegalArgumentException();
    if ((parser.getEventType() == eventType && (name == null || parser.getName().equals(
        name))))
      return;
    else
      throw new AimlSyntaxException("Syntax error: expected " +
          XmlPullParser.TYPES[eventType] + " '" + name + "' " +
          parser.getPositionDescription());
  }

  private String requireAttrib(String name) throws AimlSyntaxException {
    if (parser.getAttributeValue(null, name) == null)
      throw new AimlSyntaxException("Syntax error: mandatory attribute '" +
          name + "' missing from element '" + parser.getName() + "' " +
          parser.getPositionDescription());
    else
      return parser.getAttributeValue(null, name);
  }

  private void doAiml() throws IOException, XmlPullParserException,
      AimlParserException {
    parser.nextTag();
    require(XmlPullParser.START_TAG, "aiml", "root element must be 'aiml'");
    String version = requireAttrib("version");
    if (!version.equals("1.0") && !version.equals("1.0.1"))
      throw new InvalidAimlVersionException(
          "Unsupported AIML version, refusing forward compatible processing mode" +
              parser.getPositionDescription());

    parser.nextTag();
    currentPath = new Path();
    doCategoryList();
    require(XmlPullParser.END_TAG, "aiml");
    parser.next();
    assert (isEvent(XmlPullParser.END_DOCUMENT, null)) : "Syntax error, no markup allowed after the root element";
  }

  private void doCategoryList() throws IOException, XmlPullParserException,
      AimlParserException {
    try {
      do {
        switch (parser.getEventType()) {
        case XmlPullParser.START_TAG:
          if (parser.getName().equals("category")) {
            doCategory();
          } else if (parser.getName().equals("contextgroup")) {
            doContextGroup();
          } else if (parser.getName().equals("topic")) {
            doTopic();
          } else
            throw new AimlSyntaxException(
                "Syntax error: expected category, contextgroup or topic " +
                    parser.getPositionDescription());
          break;
        case XmlPullParser.END_TAG:
          if (parser.getName().equals("aiml") ||
              parser.getName().equals("contextgroup") ||
              parser.getName().equals("topic"))
            return;
          throw new AimlSyntaxException("Syntax error: end tag '" +
              parser.getName() + "' without opening tag " +
              parser.getPositionDescription());
        default:
          assert (false) : "Something very unexpected happened";
        }
        //parser.nextTag();
      } while (true);
    } catch (MultipleContextsException e) {
      throw new AimlSyntaxException(
          "Syntax error, paralell contexts and/or context overloading not allowed. Context " +
              e.getMessage() + " " + parser.getPositionDescription(), e);
    }
  }

  private void doTopic() throws IOException, XmlPullParserException,
      AimlParserException, MultipleContextsException {
    require(XmlPullParser.START_TAG, "topic");
    currentPath.save();
    String pattern = requireAttrib("name");
    currentPath.add("topic", pattern);
    parser.nextTag();
    doCategoryList();
    require(XmlPullParser.END_TAG, "topic");
    parser.nextTag();
    currentPath.restore();
  }

  private void doContextGroup() throws IOException, XmlPullParserException,
      AimlParserException, MultipleContextsException {
    require(XmlPullParser.START_TAG, "contextgroup");
    parser.nextTag();
    currentPath.save();
    doContextList();
    doCategoryList();
    require(XmlPullParser.END_TAG, "contextgroup");
    parser.nextTag();
    currentPath.restore();
  }

  private void doContextList() throws XmlPullParserException,
      AimlSyntaxException, IOException, MultipleContextsException {
    do {
      doContextDef();
    } while (isEvent(XmlPullParser.START_TAG, "context"));
  }

  private void doCategory() throws IOException, XmlPullParserException,
      MultipleContextsException, AimlParserException {
    require(XmlPullParser.START_TAG, "category");
    parser.nextTag();
    currentPath.save();
    if (isEvent(XmlPullParser.START_TAG, "pattern")) {
      doPatternC();
    }
    if (isEvent(XmlPullParser.START_TAG, "that")) {
      doThatC();
    }
    if (isEvent(XmlPullParser.START_TAG, "context"))
      doContextList();
    require(XmlPullParser.START_TAG, "template",
        "expected 'template' element in category");
    Script s = doTemplate();
    require(XmlPullParser.END_TAG, "category");
    try {
      Classifier.add(currentPath, s);
      log.info("added category " + currentPath + "{" + s + "}");
    } catch (DuplicatePathException e) {
      log.warning("Duplicate category " + currentPath + " " +
          parser.getPositionDescription());
    }
    parser.nextTag();
    currentPath.restore();
  }

  private Script doTemplate() throws IOException, XmlPullParserException,
      AimlParserException {
    require(XmlPullParser.START_TAG, "template");
    Script s = new Block();
    s = s.parse(parser);
    require(XmlPullParser.END_TAG, "template");
    parser.nextTag();
    return s;
  }

  private void doContextDef() throws IOException, XmlPullParserException,
      AimlSyntaxException, MultipleContextsException {
    require(XmlPullParser.START_TAG, "context");
    String name = requireAttrib("name");
    parser.next();
    String pattern = doPattern();
    currentPath.add(name, pattern);
    require(XmlPullParser.END_TAG, "context");
    parser.nextTag();
  }

  private String doPattern() throws IOException, XmlPullParserException,
      AimlSyntaxException {
    StringBuffer result = new StringBuffer();
    do {
      switch (parser.getEventType()) {
      case XmlPullParser.START_TAG:
        if (parser.getName().equals("bot"))
          result.append(doBotProperty());
        else
          throw new AimlSyntaxException("Unexpected start tag '" +
              parser.getName() +
              "' while parsing pattern, only 'bot' allowed " +
              parser.getPositionDescription());
        break;
      case XmlPullParser.END_TAG:
        return result.toString();
      case XmlPullParser.TEXT:
        result.append(parser.getText());
        parser.next();
        break;
      case XmlPullParser.END_DOCUMENT:
        throw new AimlSyntaxException(
            "Unexpected end of document while parsing pattern " +
                parser.getPositionDescription());
      default:
        throw new IllegalStateException(
            "Something really weird happened while parsing pattern " +
                parser.getPositionDescription());
      }
    } while (true);
  }

  private String doBotProperty() throws IOException, XmlPullParserException,
      AimlSyntaxException {
    require(XmlPullParser.START_TAG, "bot");
    if (!parser.isEmptyElementTag())
      throw new AimlSyntaxException(
          "Syntax error while parsing bot element in pattern: element must be empty " +
              parser.getPositionDescription());
    String name = requireAttrib("name");
    String result;
    try {
      result = bot.getProperty(name);
    } catch (InvalidPropertyException e) {
      throw new AimlSyntaxException("Syntax error: " + e.getMessage() + " " +
          parser.getPositionDescription());
    }
    parser.nextTag();
    parser.next();
    return result;
  }

  private void doThatC() throws IOException, XmlPullParserException,
      AimlSyntaxException, MultipleContextsException {
    require(XmlPullParser.START_TAG, "that");
    parser.next();
    String pattern = doPattern();
    currentPath.add("that", pattern);
    require(XmlPullParser.END_TAG, "that");
    parser.nextTag();
  }

  private void doPatternC() throws IOException, XmlPullParserException,
      AimlSyntaxException, MultipleContextsException {
    require(XmlPullParser.START_TAG, "pattern");
    parser.next();
    String pattern = doPattern();
    currentPath.add("input", pattern);
    require(XmlPullParser.END_TAG, "pattern");
    parser.nextTag();
  }

  public void load(Reader in) throws IOException, XmlPullParserException,
      AimlParserException {
    parser.setInput(in);
    doAiml();
  }

  public void load(InputStream in, String encoding) throws IOException,
      XmlPullParserException, AimlParserException {
    parser.setInput(in, encoding);
    doAiml();
  }

  public void load(String file) throws XmlPullParserException, IOException,
      AimlParserException {
    parser.setInput(new FileReader(file));
    parser.setProperty("http://xmlpull.org/v1/doc/properties.html#location",
        file);
    doAiml();
  }

  public void load(String file, String encoding) throws XmlPullParserException,
      IOException, AimlParserException {
    parser.setInput(new FileInputStream(file), encoding);
    parser.setProperty("http://xmlpull.org/v1/doc/properties.html#location",
        file);
    doAiml();
  }

}
