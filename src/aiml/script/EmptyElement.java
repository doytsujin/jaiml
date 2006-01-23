package aiml.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;

public class EmptyElement implements ScriptElement {

  public ScriptElement parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    return this;
  }

  public String evaluate(MatchState m) {
    return "\"\"";
  }

  public String execute(MatchState m) {
    return "";
  }
  public String toString() {
    return "\"\"";
  }
}
