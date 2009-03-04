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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.Classifier;
import aiml.classifier.MatchState;
import aiml.environment.Environment;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class GetElement extends SimpleScriptElement {
  private String nameAttr;

  public Script parse(XmlPullParser parser, Classifier classifier) throws XmlPullParserException,
      IOException, AimlParserException {
    nameAttr = parser.getAttributeValue(null, "name");
    if (nameAttr == null)
      throw new AimlSyntaxException(
          "Syntax error: mandatory attribute 'name' missing from element '" +
              parser.getName() + "' " + parser.getPositionDescription());
    return super.parse(parser, classifier);
  }

  public String evaluate(MatchState m) {
    String result = m.getEnvironment().getVar(nameAttr);
    if (Environment.UNDEFINED_VARIABLE.equals(result)) {
      return content.evaluate(m);
    }
    return result;
  }

  public String toString() {
    if (content instanceof EmptyScript)
      return "$" + nameAttr;
    else
      return "(isset($" + nameAttr + ") ? $" + nameAttr + " : " + content + ")";
  }
}
