package aiml.parser.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.parser.AimlParserException;

public interface ScriptElement {
 
  public ScriptElement parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException;
  
  public String evaluate();
}
