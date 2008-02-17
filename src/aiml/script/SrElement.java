package aiml.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.context.ContextInfo;
import aiml.parser.AimlParserException;

public class SrElement extends EmptyElement {

  public Script parse(XmlPullParser parser) throws XmlPullParserException,
      IOException, AimlParserException {

    super.parse(parser);
    StarElement star = new StarElement(
        ContextInfo.getContext("input").getOrder(), 1);
    SraiElement srai = new SraiElement();
    srai.content = star;

    return srai;
  }

  public String evaluate(MatchState m) {
    return "srai(star[input,1])";
  }

  public String toString() {
    return "srai(star[input,1])";
  }

}
