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

import aiml.bot.Bot;
import aiml.bot.InvalidPropertyException;
import aiml.classifier.Classifier;
import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class BotElement extends EmptyElement {
  private String name;

  public Script parse(XmlPullParser parser, Classifier classifier) throws XmlPullParserException,
      IOException, AimlParserException {
    name = parser.getAttributeValue(null, "name");
    if (name == null)
      throw new AimlSyntaxException(
          "Syntax error: mandatory attribute 'name' missing from element '" +
              parser.getName() + "' " + parser.getPositionDescription());
    return super.parse(parser, classifier);
  }

  public String evaluate(MatchState m) {
    try {
      return m.getEnvironment().getBot().getProperty(name);
    } catch (InvalidPropertyException e) {
      Logger.getLogger(BotElement.class.getName()).warning(
          "bot element referencing unknown property " + name);
      return Bot.UNKNOWN_PROPERTY;
    }
  }

  public String toString() {
    return "$_bot['" + name + "']";
  }

}
