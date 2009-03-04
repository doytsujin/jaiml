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

import aiml.bot.InvalidSubstitutionException;
import aiml.classifier.Classifier;
import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class SubstElement extends NonEmptyElement {
  private String type;

  public Script parse(XmlPullParser parser, Classifier classifier) throws XmlPullParserException,
      IOException, AimlParserException {
    type = parser.getName();
    if (type.equals("subst")) {
      type = parser.getAttributeValue(null, "name");
      if (type == null) {
        throw new AimlSyntaxException(
            "Syntax error: mandatory attribute 'name' for 'subst' element missing");
      }
    }
    return super.parse(parser, classifier);
  }

  public String evaluate(MatchState m) {
    String text = content.evaluate(m);
    try {

      return m.getEnvironment().getBot().applySubstitutions(type, text);
    } catch (InvalidSubstitutionException e) {
      Logger.getLogger(SubstElement.class.getName()).warning(
          "trying to apply unknown substitution list " + type);
      return text;
    }

  }

  public String toString() {
    return "subst(" + type + "," + content + ")";
  }

}
