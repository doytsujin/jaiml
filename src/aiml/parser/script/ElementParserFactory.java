package aiml.parser.script;

import java.io.IOException;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.parser.AimlParserException;

public class ElementParserFactory {
  
  private static HashMap<String,Class<? extends ScriptElement>> elements = new HashMap<String, Class<? extends ScriptElement>>();
  private static Class<? extends ScriptElement> text;
  
  static {
    text=TextElement.class;
  }
  
  private ElementParserFactory() {
    super();
    // TODO Auto-generated constructor stub
  }
  
  public static void addElementParser(String name, Class<? extends ScriptElement> c) {
    elements.put(name, c);    
  }
  
  public static void addTextParser(Class<? extends ScriptElement> c) {
    text=c;
  }
  
  public static ScriptElement getElementParser(XmlPullParser parser) throws XmlPullParserException, AimlParserException, IOException {
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
