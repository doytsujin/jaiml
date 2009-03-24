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

package aiml.script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.Classifier;
import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

/**
 * <p>
 * This class handles an if-then-else type condition:
 * </p>
 * 
 * <pre>
 *   &lt;condition&gt;
 *      &lt;li name=&quot;varA&quot; value=&quot;A&quot;&gt;true if (varA==&quot;A&quot;)&lt;/li&gt;&lt;!--else--&gt;
 *      &lt;li name=&quot;varB&quot; value=&quot;B&quot;&gt;true if (varB==&quot;B&quot;)&lt;/li&gt;&lt;!--else--&gt;
 *      &lt;li name=&quot;varC&quot; value=&quot;C&quot;&gt;true if (varC==&quot;C&quot;)&lt;/li&gt;
 *   &lt;/condition&gt;
 * </pre>
 * 
 * <p>
 * An if-then-else block can contain a default case:
 * </p>
 * 
 * <pre>
 *   &lt;condition&gt;
 *      &lt;li name=&quot;varA&quot; value=&quot;&quot;&gt;Variable A is not set&lt;/li&gt;      
 *      &lt;li&gt;Variable A is set&lt;/li&gt;
 *   &lt;/condition&gt;
 * </pre>
 * 
 * <p>
 * If the block contains only one case, an {@link If} is returned. If the block
 * contains only the default case, only the block with the default case is
 * returned.
 * </p>
 * 
 * <p>
 * As the name suggests, the conditions are evaluated sequentially, unlike
 * {@link Switch} which does a direct lookup.
 * </p>
 * 
 * @author Kim Sullivan
 * 
 */
public class IfThenElse implements Script {

  private static class Entry {
    private String name;
    private String value;
    private Script content;

    public Entry(String name, String value, Script content) {
      this.name = name;
      this.value = value;
      this.content = content;
    }

    public boolean isTrue(MatchState m) {
      return m.getEnvironment().getVar(name).equalsIgnoreCase(value);
    }

    public String evaluate(MatchState m) {
      return content.evaluate(m);
    }

    public String toString() {
      return "(($" + name + "==\"" + value + "\") ? " + content + " : \"\")";
    }
  }

  ArrayList<Entry> conditions = new ArrayList<Entry>();
  Script defaultBlock;

  private void parseEntry(XmlPullParser parser, Classifier classifier)
      throws XmlPullParserException, IOException, AimlParserException {
    if (!(parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals(
        "li")))
      throw new AimlSyntaxException(
          "Syntax error: expecting start tag 'li' while parsing if-else conditions " +
              parser.getPositionDescription());

    if (defaultBlock != null)
      throw new AimlSyntaxException(
          "Syntax error: no conditions allowed after the default block in if-else conditions " +
              parser.getPositionDescription());

    String name = parser.getAttributeValue(null, "name");
    String value = parser.getAttributeValue(null, "value");
    if (value == null && name == null) {
      defaultBlock = new Block().parse(parser, classifier);
    } else if (value != null && name != null) {
      conditions.add(new Entry(name, value, new Block().parse(parser,
          classifier)));
    } else {
      throw new AimlSyntaxException(
          "Syntax error, both name and value attributes must be present in if-else condition " +
              parser.getPositionDescription());
    }

    if (!(parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals(
        "li")))
      throw new AimlSyntaxException(
          "Syntax error: expecting end tag 'li' while parsing if-else conditions " +
              parser.getPositionDescription());
    parser.nextTag();
  }

  public Script parse(XmlPullParser parser, Classifier classifier)
      throws XmlPullParserException, IOException, AimlParserException {
    parser.nextTag();
    do {
      parseEntry(parser, classifier);
    } while (!(parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals(
        "condition")));

    if (conditions.size() == 1 && defaultBlock == null) {
      Logger.getLogger(this.getClass().getName()).warning(
          "if-else type condition at " + parser.getPositionDescription() +
              " contains only one condition");
      parser.next();
      Entry e = conditions.get(0);
      return new If(e.name, e.value, e.content);
    } else if (conditions.size() == 0 && defaultBlock != null) {
      Logger.getLogger(this.getClass().getName()).warning(
          "if-else type condition at " + parser.getPositionDescription() +
              " contains only default block");
      parser.next();
      return defaultBlock;
    } else {
      if (defaultBlock instanceof EmptyScript)
        defaultBlock = null;
      parser.next();
      return this;
    }
  }

  public String evaluate(MatchState m) {
    for (Entry condition : conditions) {
      if (condition.isTrue(m))
        return condition.evaluate(m);
    }
    if (defaultBlock != null) {
      return defaultBlock.evaluate(m);
    }
    return "";
  }

  public String toString() {
    return "ifElse(" + conditions.toString() +
        ((defaultBlock != null) ? defaultBlock : "") + ")";
  }
}
