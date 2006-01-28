package aiml.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class SubstElement extends NonEmptyElement {
  private String type;

  public Script parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    type = parser.getName();
    if (type.equals("subst")) {
      type = parser.getAttributeValue(null,"name");
      if (type == null) {
        throw new AimlSyntaxException("Syntax error: mandatory attribute 'name' for 'subst' element missing");
      }
    }
    return super.parse(parser);
  }

  public String evaluate(MatchState m) {
    return "subst("+type+","+content.evaluate(m)+")";
  }

  public String execute(MatchState m) {
    return "print(subst("+type+","+content.evaluate(m)+"));";
  }

  public String toString() {
    return "subst("+type+","+content+")";
  }

}
