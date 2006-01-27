package aiml.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.parser.AimlParserException;

public abstract class NonEmptyElement extends SimpleScriptElement {

  public Script parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    super.parse(parser);
    if (content instanceof EmptyScript)
      return content;
    else
      return this;
  }

}
