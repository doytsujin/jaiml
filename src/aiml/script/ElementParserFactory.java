package aiml.script;

import java.io.IOException;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.parser.AimlParserException;

public class ElementParserFactory {
  
  private static HashMap<String,Class<? extends Script>> elements = new HashMap<String, Class<? extends Script>>();
  private static Class<? extends Script> text;
  
  static {
    text=TextElement.class;
    elements.put("bot",BotElement.class);
    elements.put("set",SetElement.class);
    elements.put("get",GetElement.class);
    elements.put("random",RandomElement.class);
    elements.put("condition",ConditionElement.class);
  }
  
  private ElementParserFactory() {
    super();
  }
  
  public static void addElementParser(String name, Class<? extends Script> c) {
    elements.put(name, c);    
  }
  
  public static void addTextParser(Class<? extends Script> c) {
    text=c;
  }
  
  public static Script getElementParser(XmlPullParser parser) throws XmlPullParserException, AimlParserException, IOException {
    try {
      switch (parser.getEventType()) {
        case XmlPullParser.TEXT:
          if (text!=null) {                        
            return text.newInstance().parse(parser);
          } else
            throw new NullPointerException("Cannot handle text events "+parser.getPositionDescription());
        case XmlPullParser.START_TAG:
          if (elements.containsKey(parser.getName())) {
            return elements.get(parser.getName()).newInstance().parse(parser);
          }  
        default:
          throw new AimlParserException("Unexpected " + XmlPullParser.TYPES[parser.getEventType()] + " " + parser.getName() + " " + parser.getPositionDescription());
      }          
    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }
  
}
