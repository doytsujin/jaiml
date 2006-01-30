package aiml.script;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class Switch implements Script {
  private String name;
  private HashMap<String, Script> cases = new HashMap<String, Script>();
  private Script defaultCase;
  Switch(String name) {
    this.name=name;
  }
  
  private void parseCase(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    if (!(parser.getEventType()==XmlPullParser.START_TAG && parser.getName().equals("li")))
      throw new AimlSyntaxException("Syntax error: expecting start tag 'li' while parsing switch type condition cases "+parser.getPositionDescription());
    if (defaultCase != null)
      throw new AimlSyntaxException("Syntax error: no cases allowed after the default case in switch type condition "+parser.getPositionDescription());
    
    String value = parser.getAttributeValue(null,"value");
    if (value == null) {
      defaultCase = new Block().parse(parser);
    } else {
      if (cases.containsKey(value))
        throw new AimlSyntaxException("Syntax error: duplicate case " + name + "==\"" + value + "\" in switch type condition "+parser.getPositionDescription());
      cases.put(value,new Block().parse(parser));
    }
    if (!(parser.getEventType()==XmlPullParser.END_TAG && parser.getName().equals("li")))
      throw new AimlSyntaxException("Syntax error: expecting end tag 'li' while parsing switch type condition cases "+parser.getPositionDescription());
    parser.nextTag();
  }
  
  public Script parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    parser.nextTag();
    do {
      parseCase(parser);
    } while (!(parser.getEventType()==XmlPullParser.END_TAG && parser.getName().equals("condition")));
    if (cases.size() == 1 && defaultCase == null) {
      Logger.getLogger(this.getClass().getName()).warning("switch type condition at "+parser.getPositionDescription() + " contains only one case");
      Entry<String,Script> theCase = cases.entrySet().iterator().next();
      
      parser.next();
      
      return new If(name,theCase.getKey(),theCase.getValue());

    } else if (cases.size() == 0 && defaultCase !=null) {
      Logger.getLogger(this.getClass().getName()).warning("switch type condition at "+parser.getPositionDescription() + " contains only default case");
      parser.next();
      return defaultCase;
    } else {
      if (defaultCase instanceof EmptyScript)
        defaultCase = null;
      parser.next();
      return this;
    }
    
  }


  public String evaluate(MatchState m) {
    StringBuffer result = new StringBuffer();
    result.append("switch($").append(name);
    Iterator<Entry<String,Script>> i = cases.entrySet().iterator();
    for (;i.hasNext();) {
      Entry<String,Script> theCase = i.next();
      result.append(",\"").append(theCase.getKey()).append("\":").append(theCase.getValue().evaluate(m));
    }
    if ((defaultCase !=null)) {
      result.append(",default:").append(defaultCase.evaluate(m));
    }
    result.append(')');
    return result.toString();
  }

  public String execute(MatchState m, int depth) {
    StringBuffer result = new StringBuffer();
    result.append(Formatter.tab(depth)).append("switch($").append(name).append(") {\n");
    Iterator<Entry<String,Script>> i = cases.entrySet().iterator();
    for (;i.hasNext();) {
      Entry<String,Script> theCase = i.next();
      result.append(Formatter.tab(depth+1)).append("case \"").append(theCase.getKey()).append("\":\n");
      result.append(theCase.getValue().execute(m, depth+2)).append("\n");
      result.append(Formatter.tab(depth+2)).append("break;\n");
    }
    if ((defaultCase !=null)) {
      result.append(Formatter.tab(depth+1)).append("default:\n");
      result.append(defaultCase.execute(m, depth+2)).append('\n');
    }
    result.append(Formatter.tab(depth)).append('}');  
    return result.toString();        
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append("switch($").append(name).append(',').append(cases.toString());
    if ((defaultCase !=null)) {
      result.append(",default:").append(defaultCase.toString());
    }
    result.append(')');
    return result.toString();
  }
}
