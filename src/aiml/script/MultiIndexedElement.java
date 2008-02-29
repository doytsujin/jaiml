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

public abstract class MultiIndexedElement extends EmptyElement {

  protected int i1;
  protected int i2;

  public Script parse(XmlPullParser parser) throws XmlPullParserException,
      IOException, AimlParserException {
    String indexAttribute = parser.getAttributeValue(null, "index");
    if (indexAttribute == null) {
      i1 = 1;
      i2 = 1;
    } else {
      String[] indices = indexAttribute.split(",");
      try {
        i1 = Integer.parseInt(indices[0]);
      } catch (NumberFormatException e) {
        throw new AimlSyntaxException(
            "Syntax error: index attribute must be a number " +
                parser.getPositionDescription());
      }
      if (indices.length > 1) {
        try {
          i2 = Integer.parseInt(indices[1]);
        } catch (NumberFormatException e) {
          throw new AimlSyntaxException(
              "Syntax error: second index attribute must be a number " +
                  parser.getPositionDescription());
        }
      } else
        i2 = 1;
    }

    return super.parse(parser);
  }

}
