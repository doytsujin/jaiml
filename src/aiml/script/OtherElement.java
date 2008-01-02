package aiml.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;

/**
 * <p>
 * This is a element parser class that handles all other elements that are not
 * part of AIML but might appear in the text. The specification requires, that
 * such elements are returned as character data.
 * </p>
 * <p>
 * This class serves only as a parser, and returns either a {@link TextElement}
 * or a {@link Block} consisting of the contents of this element (as a
 * {@link Script}) surrounded by a textual representation of the surrounding
 * tags.
 * </p>
 * 
 * <p>
 * This class doesn't preserve the original formatting of the element in the
 * source file, but recreates the tags from parser data
 * </p>
 * 
 * @author Kim Sullivan
 * 
 */
public class OtherElement extends SimpleScriptElement {
  private String escapeQuotes(String attValue) {
    return attValue.replace("\"", "&quot;");
  }

  public Script parse(XmlPullParser parser) throws XmlPullParserException,
      IOException, AimlParserException {
    StringBuffer b = new StringBuffer();
    // First recreate the tag with all it's attributes
    b.append('<').append(parser.getName());
    for (int i = 0; i < parser.getAttributeCount(); i++) {
      b.append(' ').append(parser.getAttributeName(i)).append("=\"").append(
          escapeQuotes(parser.getAttributeValue(i))).append("\"");
    }
    // Check if it's an empty tag
    if (parser.isEmptyElementTag()) {
      b.append('/').append('>');
      parser.nextTag();
      parser.next();
      return new TextElement(b.toString());
    } else {
      b.append('>');

      String ETag = "</" + parser.getName() + ">";
      // Parse the contents of this element
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
  }

  public String evaluate(MatchState m) {
    throw new UnsupportedOperationException("evaluate()");
  }

}
