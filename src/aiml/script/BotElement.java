package aiml.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class BotElement implements ScriptElement {
  private String name;
  public ScriptElement parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    if (!parser.isEmptyElementTag())
      throw new AimlSyntaxException("Syntax error while parsing bot constant in template: element must be empty "+parser.getPositionDescription());
    name=parser.getAttributeValue(null,"name");
    if (name==null)
      throw new AimlSyntaxException("Syntax error: mandatory attribute 'name' missing from element '" + parser.getName() + "' "+ parser.getPositionDescription());
    parser.nextTag();
    parser.next();
    return this;
  }

  public String evaluate(MatchState m) {
    return "$_bot['"+name+"']";
  }

  public String execute(MatchState m) {
    return "print($_bot['"+name+"']);";
  }

  public String toString() {
    return "$_bot['"+name+"']";
  }

}
