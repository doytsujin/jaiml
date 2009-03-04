/*
    jaiml - java AIML library
    Copyright (C) 2004, 2009  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package aiml.script;

import java.io.IOException;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.Classifier;
import aiml.classifier.InvalidWildcardReferenceException;
import aiml.classifier.MatchState;
import aiml.context.Context;
import aiml.context.ContextInfo;
import aiml.context.UnknownContextException;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class StarElement extends EmptyElement {
  private Context context;
  private int index;

  private ContextInfo contextInfo = ContextInfo.getInstance();

  public StarElement() {
    super();
  }

  public StarElement(Context context, int index) {
    this();
    this.context = context;
    this.index = index;
  }

  public Script parse(XmlPullParser parser, Classifier classifier)
      throws XmlPullParserException, IOException, AimlParserException {
    String type = parser.getName();
    String contextName = parser.getAttributeValue(null, "context");
    try {
      if (type.equals("thatstar") && contextName == null) {
        context = contextInfo.getContext("that");
      } else if (type.equals("topicstar") && contextName == null) {
        context = contextInfo.getContext("topic");
      } else if (type.equals("star")) {
        if (contextName == null)
          context = contextInfo.getContext("input");
        else
          context = contextInfo.getContext(contextName);
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

    return super.parse(parser, classifier);
  }

  public String evaluate(MatchState m) {
    try {
      return m.getWildcard(context.getOrder(), index).getValue();
    } catch (InvalidWildcardReferenceException e) {
      Logger.getLogger(StarElement.class.getName()).severe(e.getMessage());
      return "";
    }
  }

  public String toString() {
    return "star[" + context.getName() + "," + index + "]";
  }

}
