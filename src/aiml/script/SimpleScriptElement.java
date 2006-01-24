package aiml.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public abstract class SimpleScriptElement implements ScriptElement {

  protected ScriptElement content;
  public ScriptElement parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    String name = parser.getName();
    content = new BlockElement().parse(parser);    
    if (parser.getEventType()==XmlPullParser.END_TAG && parser.getName().equals(name)) {
      parser.next();
      return this;
    } else
      throw new AimlSyntaxException("Syntax error: expected end tag '" + name + "' "+parser.getPositionDescription());
  }
  
}
