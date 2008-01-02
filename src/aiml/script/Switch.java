package aiml.script;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

/**
 * <p>
 * This class handles an switch-case type condition block:
 * </p>
 * 
 * <pre>
 *   &lt;condition name=&quot;var&quot;&gt;
 *     &lt;li value=&quot;A&quot;&gt;Variable &quot;var&quot; has the value &quot;A&quot;&lt;/li&gt;
 *     &lt;li value=&quot;B&quot;&gt;Variable &quot;var&quot; has the value &quot;B&quot;&lt;/li&gt;
 *     &lt;li value=&quot;C&quot;&gt;Variable &quot;var&quot; has the value &quot;C&quot;&lt;/li&gt;
 *     &lt;li&gt;Variable &quot;var&quot; has none of the above values&lt;/li&gt;
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
 * Value comparisons are done case insensitive. When evaluating, the correct
 * case is determined via lookup using an O(log n) algorithm - as opposed to
 * {@link IfThenElse}, which does a sequential lookup.
 * </p>
 * 
 * @author Kim Sullivan
 * 
 */
public class Switch implements Script {
  private String name;
  private Map<String, Script> cases = new TreeMap<String, Script>(
      new Comparator<String>() {
        public int compare(String o1, String o2) {
          return o1.compareToIgnoreCase(o2);
        }
      });

  private Script defaultCase;

  Switch(String name) {
    this.name = name;
  }

  private void parseCase(XmlPullParser parser) throws XmlPullParserException,
      IOException, AimlParserException {
    if (!(parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals(
        "li")))
      throw new AimlSyntaxException(
          "Syntax error: expecting start tag 'li' while parsing switch type condition cases " +
              parser.getPositionDescription());
    if (defaultCase != null)
      throw new AimlSyntaxException(
          "Syntax error: no cases allowed after the default case in switch type condition " +
              parser.getPositionDescription());

    if (parser.getAttributeValue(null, "name") != null) {
      throw new AimlSyntaxException(
          "Syntax error: name attribute in switch case not allowed " +
              parser.getPositionDescription());
    }
    String value = parser.getAttributeValue(null, "value");
    if (value == null) {
      defaultCase = new Block().parse(parser);
    } else {
      if (cases.containsKey(value))
        throw new AimlSyntaxException("Syntax error: duplicate case " + name +
            "==\"" + value + "\" in switch type condition " +
            parser.getPositionDescription());
      cases.put(value, new Block().parse(parser));
    }
    if (!(parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals(
        "li")))
      throw new AimlSyntaxException(
          "Syntax error: expecting end tag 'li' while parsing switch type condition cases " +
              parser.getPositionDescription());
    parser.nextTag();
  }

  public Script parse(XmlPullParser parser) throws XmlPullParserException,
      IOException, AimlParserException {
    parser.nextTag();
    do {
      parseCase(parser);
    } while (!(parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals(
        "condition")));
    if (cases.size() == 1 && defaultCase == null) {
      Logger.getLogger(this.getClass().getName()).warning(
          "switch type condition at " + parser.getPositionDescription() +
              " contains only one case");
      Entry<String, Script> theCase = cases.entrySet().iterator().next();

      parser.next();

      return new If(name, theCase.getKey(), theCase.getValue());

    } else if (cases.size() == 0 && defaultCase != null) {
      Logger.getLogger(this.getClass().getName()).warning(
          "switch type condition at " + parser.getPositionDescription() +
              " contains only default case");
      parser.next();
      return defaultCase;
    } else {
      if (defaultCase instanceof EmptyScript)
        defaultCase = null;
      parser.next();
      return this;
    }

  }

  public String evaluate(MatchState m) {
    String value = m.getEnvironment().getVar(name);
    Script aCase = cases.get(value);
    if (aCase != null) {
      return aCase.evaluate(m);
    }
    if (defaultCase != null) {
      return defaultCase.evaluate(m);
    }
    return "";
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append("switch($").append(name).append(',').append(cases.toString());
    if ((defaultCase != null)) {
      result.append(",default:").append(defaultCase.toString());
    }
    result.append(')');
    return result.toString();
  }
}
