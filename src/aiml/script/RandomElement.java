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
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class RandomElement implements Script {

  private ArrayList<Script> items = new ArrayList<Script>();
  private final Random random = new Random();

  private void parseItem(XmlPullParser parser) throws XmlPullParserException,
      IOException, AimlParserException {
    if (!(parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals(
        "li")))
      throw new AimlSyntaxException(
          "Syntax error: expecting start tag 'li' while parsing 'random' " +
              parser.getPositionDescription());
    items.add(new Block().parse(parser));
    if (!(parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals(
        "li")))
      throw new AimlSyntaxException(
          "Syntax error: expecting end tag 'li' while parsing 'random' " +
              parser.getPositionDescription());
    parser.nextTag();
  }

  public Script parse(XmlPullParser parser) throws XmlPullParserException,
      IOException, AimlParserException {
    parser.nextTag();
    do {
      parseItem(parser);
    } while (!(parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals(
        "random")));
    if (items.size() == 1) {
      Logger.getLogger(RandomElement.class.getName()).warning(
          "random element " + parser.getPositionDescription() +
              " contains only one alternative");
      parser.next();
      return items.get(0);
    } else {
      parser.next();
      return this;
    }

  }

  public String evaluate(MatchState m) {
    return items.get(random.nextInt(items.size())).evaluate(m);
  }

  public String toString() {
    return "random(" + items.size() + ":" + items + ")";
  }
}
