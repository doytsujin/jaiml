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
import aiml.parser.AimlParserException;

public class SrElement extends EmptyElement {

  public Script parse(XmlPullParser parser, Classifier classifier)
      throws XmlPullParserException, IOException, AimlParserException {

    super.parse(parser, classifier);
    StarElement star = new StarElement(classifier.getContextInfo().getContext(
        "input"), 1);
    SraiElement srai = new SraiElement();
    srai.content = star;

    return srai;
  }

  public String evaluate(MatchState m) {
    return "srai(star[input,1])";
  }

  public String toString() {
    return "srai(star[input,1])";
  }

}
