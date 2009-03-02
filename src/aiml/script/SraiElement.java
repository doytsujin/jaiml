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

import java.util.logging.Logger;

import aiml.classifier.MatchState;
import aiml.environment.Environment;

public class SraiElement extends SimpleScriptElement {

  public String evaluate(MatchState m) {

    String newInput = content.evaluate(m);
    Environment environment = m.getEnvironment();
    environment.pushInput(newInput);
    MatchState result = environment.match();
    environment.popInput();
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
