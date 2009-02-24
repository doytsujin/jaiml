/*
    jaiml - java AIML library
    Copyright (C) 2004, 2009  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package aiml.script;

import java.io.IOException;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.parser.AimlParserException;

public class ElementParserFactory {

  private static HashMap<String, Class<? extends Script>> elements = new HashMap<String, Class<? extends Script>>();
  private static Class<? extends Script> text;
  private static Class<? extends Script> defaultElementHandler;
  static {
    text = TextElement.class;
    defaultElementHandler = OtherElement.class;
    elements.put("bot", BotElement.class);
    elements.put("set", SetElement.class);
    elements.put("get", GetElement.class);
    elements.put("random", RandomElement.class);
    elements.put("condition", ConditionElement.class);
    elements.put("star", StarElement.class);
    elements.put("thatstar", StarElement.class);
    elements.put("topicstar", StarElement.class);
    elements.put("srai", SraiElement.class);
    elements.put("think", ThinkElement.class);
    elements.put("formal", FormalElement.class);
    elements.put("uppercase", UppercaseElement.class);
    elements.put("lowercase", LowercaseElement.class);
    elements.put("sentence", SentenceElement.class);
    elements.put("that", ThatElement.class);
    elements.put("input", InputElement.class);
    elements.put("version", VersionElement.class);
    elements.put("size", SizeElement.class);
    elements.put("id", IDElement.class);
    elements.put("date", DateElement.class);
    elements.put("sr", SrElement.class);
    elements.put("gender", SubstElement.class);
    elements.put("person", SubstElement.class);
    elements.put("person2", SubstElement.class);
    elements.put("subst", SubstElement.class);

  }

  private ElementParserFactory() {
    super();
  }

  public static void addElementParser(String name, Class<? extends Script> c) {
    elements.put(name, c);
  }

  public static void addTextParser(Class<? extends Script> c) {
    text = c;
  }

  public static Script getElementParser(XmlPullParser parser)
      throws XmlPullParserException, AimlParserException, IOException {
    try {
      switch (parser.getEventType()) {
      case XmlPullParser.TEXT:
        if (text != null) {
          return text.newInstance().parse(parser);
        } else
          throw new NullPointerException("Cannot handle text events " +
              parser.getPositionDescription());
      case XmlPullParser.START_TAG:
        if (elements.containsKey(parser.getName())) {
          return elements.get(parser.getName()).newInstance().parse(parser);
        }
        if (defaultElementHandler != null)
          return defaultElementHandler.newInstance().parse(parser);
        /* fall through */
      default:
        throw new AimlParserException("Unexpected " +
            XmlPullParser.TYPES[parser.getEventType()] + " " +
            parser.getName() + " " + parser.getPositionDescription());
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
