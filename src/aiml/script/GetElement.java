package aiml.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class GetElement extends SimpleScriptElement {
  private String nameAttr;

  public Script parse(XmlPullParser parser) throws XmlPullParserException,
      IOException, AimlParserException {
    nameAttr = parser.getAttributeValue(null, "name");
    if (nameAttr == null)
      throw new AimlSyntaxException(
          "Syntax error: mandatory attribute 'name' missing from element '" +
              parser.getName() + "' " + parser.getPositionDescription());
    return super.parse(parser);
  }

  public String evaluate(MatchState m) {
    if (content instanceof EmptyScript)
      return "$" + nameAttr;
    else
      return "(isset($" + nameAttr + ") ? $" + nameAttr + " : " +
          content.evaluate(m) + ")";
  }

  public String toString() {
    if (content instanceof EmptyScript)
      return "$" + nameAttr;
    else
      return "(isset($" + nameAttr + ") ? $" + nameAttr + " : " + content + ")";
  }
}
