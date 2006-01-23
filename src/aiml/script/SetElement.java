package aiml.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class SetElement implements ScriptElement {
  private String name;
  private ScriptElement value;
  public ScriptElement parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    name=parser.getAttributeValue(null,"name");
    if (name==null)
      throw new AimlSyntaxException("Syntax error: mandatory attribute 'name' missing from element '" + parser.getName() + "' "+ parser.getPositionDescription());
    value = new BlockElement().parse(parser);
    if (parser.getEventType()==XmlPullParser.END_TAG && parser.getName().equals("set")) {
      parser.next();
      return this;
    } else
      throw new AimlSyntaxException("Syntax error: expected end tag 'set' "+parser.getPositionDescription());
  }

  public String evaluate(MatchState m) {
    return "($" + name +"="+value.evaluate(m)+")";
  }

  public String execute(MatchState m) {
    return "print($" + name +"="+value.evaluate(m)+");";
  }
  
  public String toString() {
    return "$" + name +"=" + value;
  }
}
