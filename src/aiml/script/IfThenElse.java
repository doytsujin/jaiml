package aiml.script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class IfThenElse implements Script {
   
  private class Entry {
    private String name;
    private String value;
    private Script content;
    
    public Entry(String name, String value, Script content) {
      this.name = name;
      this.value = value;
      this.content = content;
    }
    
    public boolean isTrue() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Actual evaluation of condition not supported yet.");
    }
 
    public String toString() {
      return "(($" + name + "==\"" + value + "\") ? " + content + " : \"\")";
    }
  }

  ArrayList<Entry> conditions = new ArrayList<Entry>();
  Script defaultBlock;
  
  private void parseEntry(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    if (!(parser.getEventType()==XmlPullParser.START_TAG && parser.getName().equals("li")))
      throw new AimlSyntaxException("Syntax error: expecting start tag 'li' while parsing if-else conditions "+parser.getPositionDescription());

    if (defaultBlock != null)
      throw new AimlSyntaxException("Syntax error: no conditions allowed after the default block in if-else conditions "+parser.getPositionDescription());
    
    String name = parser.getAttributeValue(null,"name");
    String value = parser.getAttributeValue(null,"value");
    if (value == null && name == null) {
      defaultBlock = new Block().parse(parser);
    } else if (value != null && name!= null){
      conditions.add(new Entry(name, value,new Block().parse(parser)));      
    } else {
      throw new AimlSyntaxException("Syntax error, both name and value attributes must be present in if-else condition " +parser.getPositionDescription());
    }
    
    if (!(parser.getEventType()==XmlPullParser.END_TAG && parser.getName().equals("li")))
      throw new AimlSyntaxException("Syntax error: expecting end tag 'li' while parsing switch type condition cases "+parser.getPositionDescription());
    parser.nextTag();
  }

  public Script parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    parser.nextTag();
    do {
      parseEntry(parser);
    } while (!(parser.getEventType()==XmlPullParser.END_TAG && parser.getName().equals("condition")));
    
    if (conditions.size()==1 && defaultBlock == null ) {
      Logger.getLogger(this.getClass().getName()).warning("if-else type condition at "+parser.getPositionDescription() + " contains only one condition");
      parser.next();
      Entry e = conditions.get(0);
      return new If(e.name,e.value,e.content);
    } else if (conditions.size()==0 && defaultBlock != null ) {
      Logger.getLogger(this.getClass().getName()).warning("if-else type condition at "+parser.getPositionDescription() + " contains only default block");
      parser.next();
      return defaultBlock;
    } else {
      if (defaultBlock instanceof EmptyScript)
        defaultBlock = null;
      parser.next();
      return this;
    }
  }

  public String evaluate(MatchState m) {
    StringBuffer result = new StringBuffer();
    result.append('(');
    for (Entry condition : conditions) {
      //(($name=="value") ? content : ($name=="value") ? content : ... : default);
      result.append('(').append(condition.name).append("==").append(condition.value).append(") ? ");
      result.append(condition.content.evaluate(m)).append(" : ");
    }
    if (defaultBlock != null)
      result.append(defaultBlock.evaluate(m));
    else
      result.append("\"\"");
    result.append(')');
    return result.toString();
  }

  public String execute(MatchState m) {
    StringBuffer result = new StringBuffer();
    for (Entry condition : conditions) {
      //(($name=="value") ? content : ($name=="value") ? content : ... : default);
      result.append("if ($").append(condition.name).append("==\"").append(condition.value).append("\") {\n");
      result.append('\t').append(condition.content.execute(m)).append('\n');
      result.append("} else ");
    }
    if (defaultBlock != null)
     result.append("{\n\t").append(defaultBlock.execute(m)).append("\n}\n");
    else
      result.append("{}\n");

    return result.toString();
  }

  
  public String toString() {
    return "ifElse(" + conditions.toString() + ((defaultBlock!=null) ? defaultBlock : "") + ")";
  }
}
