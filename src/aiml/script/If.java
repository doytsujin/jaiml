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

import aiml.classifier.MatchState;

/**
 * This class handles a simple AIML condition:
 * 
 * <pre>
 *   &lt;condition name=&quot;variable&quot;&gt;evaluate this if true&lt;/condition&gt;
 * </pre>
 * 
 * @author Kim Sullivan
 * 
 */
public class If extends SimpleScriptElement {

  private String name;
  private String value;

  If(String name, String value) {
    super();
    this.name = name;
    this.value = value;
  }

  If(String name, String value, Script content) {
    this(name, value);
    this.content = content;
  }

  public String evaluate(MatchState m) {
    if (m.getEnvironment().getVar(name).equalsIgnoreCase(value)) {
      return content.evaluate(m);
    } else {
      return "";
    }
  }

  public String toString() {
    return "(($" + name + "==\"" + value + "\") ? " + content + ": \"\")";

  }

}
