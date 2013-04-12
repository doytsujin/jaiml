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

package aiml.category;

import java.util.EventObject;

import aiml.classifier.PatternSequence;

/**
 * An event which indicates that a change relating to a category has occured.
 * 
 * @author Kim Sullivan
 * 
 */
public class CategoryEvent extends EventObject {
  private PatternSequence sequence;
  private Object value;

  public CategoryEvent(Object source, PatternSequence sequence, Object o) {
    super(source);
    this.sequence = sequence;
    this.value = o;
  }

  /**
   * Returns the pattern sequence of the category related to this event.
   * 
   * @return a pattern sequence
   */
  public PatternSequence getSequence() {
    return sequence;
  }

  /**
   * Returns the value associated with the category related to this event.
   * 
   * @return an object
   */
  public Object getValue() {
    return value;
  }

}