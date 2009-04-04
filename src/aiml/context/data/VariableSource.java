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
 * This class retrieves the data from a variable defined in the environment.
 * 
 * @author Kim Sullivan
 * 
 */
public class VariableSource implements DataSource<String> {
  private String name;

  /**
   * Creates a new data source that retrieves its data from the variable
   * <code>name</code> defined in an environment.
   * 
   * @param name
   */
  public VariableSource(String name) {
    this.name = name;
  }

  @Override
  public String getValue(Environment e) {
    return e.getVar(name);
  }

}
