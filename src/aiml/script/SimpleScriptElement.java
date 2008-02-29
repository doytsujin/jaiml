/*
    jaiml - java AIML library
    Copyright (C) 2004-2008  Kim Sullivan

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

import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public abstract class SimpleScriptElement implements Script {

  protected Script content;

  public Script parse(XmlPullParser parser) throws XmlPullParserException,
      IOException, AimlParserException {
    String name = parser.getName();
    content = new Block().parse(parser);
    if (parser.getEventType() == XmlPullParser.END_TAG &&
        parser.getName().equals(name)) {
      parser.next();
      return this;
    } else
      throw new AimlSyntaxException("Syntax error: expected end tag '" + name +
          "' " + parser.getPositionDescription());
  }

}
