package aiml.script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
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

  public String evaluate(MatchState m) {
    StringBuffer result =  new StringBuffer();
    for (Iterator<ScriptElement> i = items.iterator(); i.hasNext(); ) {
      result.append(i.next().evaluate(m));
      if (i.hasNext()) result.append(" + ");
    }
    return result.toString();
  }
  
  public String toString() {
    return "["+items.toString()+"]";    
  }

  public String execute(MatchState m) {
    StringBuffer result =  new StringBuffer();
    for (ScriptElement item : items) {
      result.append(item.execute(m)).append('\n');
    }
    return result.toString();
  }
}
