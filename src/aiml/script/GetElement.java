package aiml.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class GetElement extends SimpleScriptElement implements ScriptElement {
  private String nameAttr;

  public ScriptElement parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    nameAttr=parser.getAttributeValue(null,"name");
    if (nameAttr==null)
      throw new AimlSyntaxException("Syntax error: mandatory attribute 'name' missing from element '" + parser.getName() + "' "+ parser.getPositionDescription());
    return super.parse(parser);
  }

  public String evaluate(MatchState m) {
    if (content instanceof EmptyElement)
      return "$" + nameAttr;
    else
      return "(isset($" + nameAttr +") ? $" + nameAttr +" : "+content.evaluate(m)+")";
  }

  public String execute(MatchState m) {
    return "if (isset($" + nameAttr +")\n "+
           "\tprint($" + nameAttr +");\n" + 
           ((content instanceof EmptyElement) ? "":
             "else\n"+
             "\t"+content.execute(m));
  }
  
  public String toString() {
    if (content instanceof EmptyElement)
      return "$" + nameAttr;
    else
      return "(isset($" + nameAttr +") ? $" + nameAttr +" : "+content+")";
  }
}
