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

package aiml.context;

import java.awt.TextComponent;

import aiml.environment.Environment;

/**
 * This this context type takes it's data from a java.awt.TextComponent
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public class TextContext extends Context {
  /**
   * The text component that serves as a source of data
   */
  private TextComponent source;

  /**
   * Create a new named context, and set it's data source to a text component.
   * 
   * @param name
   *                the name of the context
   * @param source
   *                the data source
   */
  public TextContext(String name, TextComponent source) {
    super(name);
    this.source = source;
  }

  /**
   * Get the value from the associated text component's text property.
   * 
   * @return the value
   */
  public String getValue(Environment e) {
    return source.getText();
  }

}
