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
    Script lastScript;
    parser.next();
    while (!((parser.getEventType()==XmlPullParser.END_TAG) && parser.getName().equals(blockName))) {
      lastScript = ElementParserFactory.getElementParser(parser);
      if (!(lastScript instanceof EmptyScript))
        items.add(lastScript);
    }
    switch (items.size()) {
      case 1:
        return items.get(0);
      case 0:
        return new EmptyScript();
      default:
        return this;
    }
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

  public String execute(MatchState m, int depth) {
    StringBuffer result =  new StringBuffer();
    for (Script item : items) {
      result.append(item.execute(m, depth)).append('\n');
    }
    result.deleteCharAt(result.length()-1);
    return result.toString();
  }
}
