package aiml.script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;

public class Block implements Script {
  String blockName;
  ArrayList<Script> items = new ArrayList<Script>();
  
  public Script parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    blockName=parser.getName();
    Script lastScript=new EmptyScript();
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
    for (Iterator<Script> i = items.iterator(); i.hasNext(); ) {
      result.append(i.next().evaluate(m));
      if (i.hasNext()) result.append(" + ");
    }
    return result.toString();
  }
  
  public String toString() {
    return items.toString();    
  }

  public String execute(MatchState m) {
    StringBuffer result =  new StringBuffer();
    for (Script item : items) {
      result.append(item.execute(m)).append('\n');
    }
    return result.toString();
  }
}
