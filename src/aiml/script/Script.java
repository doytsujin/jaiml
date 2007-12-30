package aiml.script;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

  public static String formal(String text) {
    Pattern p = Pattern.compile("(?:^|\\p{javaWhitespace})(\\p{javaLowerCase})");
    Matcher m = p.matcher(text);
    StringBuffer result = new StringBuffer();

    while (m.find()) {
      m.appendReplacement(result, m.group().toUpperCase());
    }
    m.appendTail(result);
    return result.toString();
  }
}
