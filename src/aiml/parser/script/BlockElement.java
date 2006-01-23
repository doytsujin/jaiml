package aiml.parser.script;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.parser.AimlParserException;

public class BlockElement implements ScriptElement {
  String blockName;
  ArrayList<ScriptElement> items = new ArrayList<ScriptElement>();
  
  public ScriptElement parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    blockName=parser.getName();
    ScriptElement lastScript=new EmptyScriptElement();
    parser.next();
    while (!((parser.getEventType()==XmlPullParser.END_TAG) && parser.getName().equals(blockName))) {
      lastScript = ElementParserFactory.getElementParser(parser);
      items.add(lastScript);
    }
    if (items.size()<=1)
      return lastScript;
    else
      return this;
  }

  public String evaluate() {
    StringBuffer result =  new StringBuffer();
    for (ScriptElement item : items) {
      result.append(item.evaluate());
    }
    return result.toString();
  }
  
  public String toString() {
    return super.toString() + "["+items.toString()+"]";    
  }
}
