package aiml.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public abstract class EmptyElement implements Script {

  public Script parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    if (!parser.isEmptyElementTag())
      throw new AimlSyntaxException("Syntax error while parsing " + parser.getName() + " element in template: element must be empty "+parser.getPositionDescription());
    parser.nextTag();
    parser.next();
    return this;
  }

}
