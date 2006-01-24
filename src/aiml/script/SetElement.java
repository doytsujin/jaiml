package aiml.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class SetElement extends SimpleScriptElement implements ScriptElement {
  private String nameAttr;

  public ScriptElement parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    nameAttr=parser.getAttributeValue(null,"name");
    if (nameAttr==null)
      throw new AimlSyntaxException("Syntax error: mandatory attribute 'name' missing from element '" + parser.getName() + "' "+ parser.getPositionDescription());
    return super.parse(parser);
  }

  public String evaluate(MatchState m) {
    return "($" + nameAttr +"="+content.evaluate(m)+")";
  }

  public String execute(MatchState m) {
    return "print($" + nameAttr +"="+content.evaluate(m)+");";
  }
  
  public String toString() {
    return "$" + nameAttr +"=" + content;
  }
}
