package aiml.script;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;

public class OtherElement extends SimpleScriptElement {  
  private String escapeQuotes(String attValue) {
    return attValue.replace((CharSequence)"\"",(CharSequence)"&quot;");
  }
  public Script parse(XmlPullParser parser) throws XmlPullParserException, IOException, AimlParserException {
    StringBuffer b = new StringBuffer();
    b.append('<').append(parser.getName());
    for (int i = 0; i < parser.getAttributeCount(); i++) {
      b.append(' ').append(parser.getAttributeName(i)).append("=\"").append(escapeQuotes(parser.getAttributeValue(i))).append("\"");
    }
    if (parser.isEmptyElementTag()) {
      b.append('/').append('>');
      parser.nextTag();
      parser.next();
      return new TextElement(b.toString());
    } else
      b.append('>');
    String ETag = "</"+parser.getName()+">";      
    super.parse(parser);
    if (content instanceof EmptyScript) {
      return new TextElement(b.append(ETag).toString());      
    } else {
      Block result = new Block();
      result.addScript(new TextElement(b.toString()));
      result.addScript(content);
      result.addScript(new TextElement(ETag));
      return result;
    }
  }

  public String evaluate(MatchState m) {
    throw new UnsupportedOperationException("evaluate()");
  }

  public String execute(MatchState m, int depth) {
    throw new UnsupportedOperationException("execute()");
  }

}
