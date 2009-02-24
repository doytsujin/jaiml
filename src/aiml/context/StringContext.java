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

package aiml.context;

import aiml.environment.Environment;

/**
 * A simple context type, associates a string with the context's value.
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public class StringContext extends Context {
  private String value;

  /**
   * Constructs a new String context with an empty string as its value.
   * 
   * @param name
   *                The name of the context
   */
  public StringContext(String name) {
    super(name);
    this.value = "";
  }

  /**
   * Constructs a new String context with a value.
   * 
   * @param name
   *                The name of the context
   * @param value
   *                The value of the context
   */
  public StringContext(String name, String value) {
    super(name);
    this.value = value;
  }

  public String getValue(Environment e) {
    return value;
  }

  /**
   * Set the value associated with this string context
   * 
   * @param value
   *                The value of the context
   */
  public void setValue(String value) {
    this.value = value;
  }

}
