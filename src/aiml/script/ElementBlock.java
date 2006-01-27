package aiml.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.parser.AimlParserException;

public class ElementBlock extends Block {

  @Override
  public Script parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    blockName=parser.getName();
    Script lastScript;
    parser.next();
    while (!((parser.getEventType()==XmlPullParser.END_TAG) && parser.getName().equals(blockName))) {
      lastScript = ElementParserFactory.getElementParser(parser);
      if (!(lastScript instanceof EmptyScript || lastScript instanceof TextElement))
        items.add(lastScript);
    }
    switch (items.size()) {
      case 1:
        return items.get(0);
      case 0:
        return new EmptyScript();
      default:
        return this;
    }
  }

}
