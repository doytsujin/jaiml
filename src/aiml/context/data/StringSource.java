/*
    jaiml - java AIML library
    Copyright (C) 2009  Kim Sullivan
    
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.
    
    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package aiml.context.data;

import aiml.environment.Environment;

/**
 * A simple data source that is backed by an immutable string.
 * 
 * @author Kim Sullivan
 * 
 */
public class StringSource implements DataSource<String> {

  private String value;

  /**
   * Create a new datasource that returns an empty string
   */
  public StringSource() {
    this("");
  }

  /**
   * Create a new datasource that returns the string <code>value</code>.
   * 
   * @param value
   *          the value this data source returns
   */
  public StringSource(String value) {
    this.value = value;
  }

  @Override
  public String getValue(Environment e) {
    return value;
  }
}
