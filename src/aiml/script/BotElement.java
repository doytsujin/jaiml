package aiml.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class BotElement extends EmptyElement {
  private String name;

  public Script parse(XmlPullParser parser) throws XmlPullParserException,
      IOException, AimlParserException {
    name = parser.getAttributeValue(null, "name");
    if (name == null)
      throw new AimlSyntaxException(
          "Syntax error: mandatory attribute 'name' missing from element '" +
              parser.getName() + "' " + parser.getPositionDescription());
    return super.parse(parser);
  }

  public String evaluate(MatchState m) {
    return "$_bot['" + name + "']";
  }

  public String execute(MatchState m, int depth) {
    return Formatter.tab(depth) + "print($_bot['" + name + "']);";
  }

  public String toString() {
    return "$_bot['" + name + "']";
  }

}
