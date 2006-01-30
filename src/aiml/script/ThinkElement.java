package aiml.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class ThinkElement extends NonEmptyElement {

  public Script parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    String name = parser.getName();
    content = new ElementBlock().parse(parser);    
    if (parser.getEventType()==XmlPullParser.END_TAG && parser.getName().equals(name)) {
      parser.next();
      return this;
    } else
      throw new AimlSyntaxException("Syntax error: expected end tag '" + name + "' "+parser.getPositionDescription());
  }

  public String evaluate(MatchState m) {
    return "think("+content.evaluate(m)+")";
  }

  public String execute(MatchState m, int depth) {
    return Formatter.tab(depth) + "think("+content.evaluate(m)+");";
  }
  
  public String toString() {
    return "think("+content+")";
  }
  
}
