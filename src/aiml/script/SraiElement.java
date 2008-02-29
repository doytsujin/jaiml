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

import java.util.logging.Logger;

import aiml.classifier.Classifier;
import aiml.classifier.MatchState;

public class SraiElement extends SimpleScriptElement {

  public String evaluate(MatchState m) {

    String newInput = content.evaluate(m);
    m.getEnvironment().pushInput(newInput);
    MatchState result = Classifier.match(m.getEnvironment());
    m.getEnvironment().popInput();
    if (result != null) {
      return ((aiml.script.Script) result.getResult()).evaluate(result);
    } else {
      Logger.getLogger(this.getClass().getName()).warning(
          "No match found for input \"" + newInput + "\"");
      return "";
    }

  }

  public String toString() {
    return "srai(" + content + ")";
  }
}
