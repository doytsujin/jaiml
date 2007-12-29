package aiml.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;

public interface Script {

  public Script parse(XmlPullParser parser) throws XmlPullParserException,
      IOException, AimlParserException;

  public String evaluate(MatchState m);
}

class Formatter {
  public static String tab(int length) {
    StringBuffer b = new StringBuffer();
    for (int i = 0; i < length; i++)
      b.append("  ");
    return b.toString();
  }
}
