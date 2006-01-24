package aiml.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class GetElement implements ScriptElement {
  private String name;
  private ScriptElement defaultValue;
  public ScriptElement parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    name=parser.getAttributeValue(null,"name");
    if (name==null)
      throw new AimlSyntaxException("Syntax error: mandatory attribute 'name' missing from element '" + parser.getName() + "' "+ parser.getPositionDescription());
    defaultValue = new BlockElement().parse(parser);
    if (parser.getEventType()==XmlPullParser.END_TAG && parser.getName().equals("get")) {
      parser.next();
      return this;
    } else
      throw new AimlSyntaxException("Syntax error: expected end tag 'get' "+parser.getPositionDescription());
  }

  public String evaluate(MatchState m) {
    if (defaultValue instanceof EmptyElement)
      return "$" + name;
    else
      return "(isset($" + name +") ? $" + name +" : "+defaultValue.evaluate(m)+")";
  }

  public String execute(MatchState m) {
    return "if (isset($" + name +")\n "+
           "\tprint($" + name +");\n" + 
           ((defaultValue instanceof EmptyElement) ? "":
             "else\n"+
             "\t"+defaultValue.execute(m));
  }
  
  public String toString() {
    if (defaultValue instanceof EmptyElement)
      return "$" + name;
    else
      return "(isset($" + name +") ? $" + name +" : "+defaultValue+")";
  }
}
