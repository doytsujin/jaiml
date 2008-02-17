package aiml.script;

import java.io.IOException;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.InvalidWildcardReferenceException;
import aiml.classifier.MatchState;
import aiml.context.ContextInfo;
import aiml.context.UnknownContextException;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class StarElement extends EmptyElement {
  private int context;
  private int index;

  public StarElement() {
    super();
  }

  public StarElement(int context, int index) {
    this();
    this.context = context;
    this.index = index;
  }

  public Script parse(XmlPullParser parser) throws XmlPullParserException,
      IOException, AimlParserException {
    String type = parser.getName();
    String contextName = parser.getAttributeValue(null, "context");
    try {
      if (type.equals("thatstar") && contextName == null) {
        context = ContextInfo.getContext("that").getOrder();
      } else if (type.equals("topicstar") && contextName == null) {
        context = ContextInfo.getContext("topic").getOrder();
      } else if (type.equals("star")) {
        if (contextName == null)
          context = ContextInfo.getContext("input").getOrder();
        else
          context = ContextInfo.getContext(contextName).getOrder();
      } else {
        throw new AimlSyntaxException("Syntax error: wildcard reference tag " +
            type + "may not contain a reference to a context " +
            parser.getPositionDescription());
      }
    } catch (UnknownContextException e) {
      throw new AimlSyntaxException("Syntax error: unknown context " +
          contextName + " in wildcard reference " +
          parser.getPositionDescription());
    }

    String indexNumber = parser.getAttributeValue(null, "index");
    if (indexNumber == null)
      index = 1;
    else
      try {
        index = Integer.parseInt(indexNumber);
        if (index <= 0)
          throw new NumberFormatException("less than 1");
      } catch (NumberFormatException e) {
        throw new AimlSyntaxException(
            "Syntax error: index must be an integer number equal or greater than 1 " +
                parser.getPositionDescription(), e);
      }

    return super.parse(parser);
  }

  public String evaluate(MatchState m) {
    try {
      return m.getWildcard(context, index).getValue();
    } catch (InvalidWildcardReferenceException e) {
      Logger.getLogger(StarElement.class.getName()).severe(e.getMessage());
      return "";
    }
  }

  public String toString() {
    return "star[" + ContextInfo.getContext(context).getName() + "," + index +
        "]";
  }

}
