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

public class TextElement implements Script {
  private String text = null;

  public TextElement() {
    super();
  }

  public TextElement(String text) {
    super();
    this.text = text;
  }

  public Script parse(XmlPullParser parser, Classifier classifier) throws XmlPullParserException,
      IOException {
    text = parser.getText();
    parser.next();
    return this;
  }

  public String evaluate(MatchState m) {
    return text;
  }

  protected String printable(char ch) {
    if (ch == '\n') {
      return "\\n";
    } else if (ch == '\r') {
      return "\\r";
    } else if (ch == '\t') {
      return "\\t";
    } else if (ch == '"') {
      return "\\\"";
    }
    if (ch > 127 || ch < 32) {
      StringBuffer buf = new StringBuffer("\\u");
      String hex = Integer.toHexString(ch);
      for (int i = 0; i < 4 - hex.length(); i++) {
        buf.append('0');
      }
      buf.append(hex);
      return buf.toString();
    }
    return "" + ch;
  }

  protected String printable(String s) {
    if (s == null)
      return null;
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < s.length(); ++i) {
      buf.append(printable(s.charAt(i)));
    }
    s = buf.toString();
    return s;
  }

  public String toString() {
    return "\"" + printable(text) + "\"";
  }
}
