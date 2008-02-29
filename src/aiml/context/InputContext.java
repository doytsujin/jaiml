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

import java.util.EmptyStackException;

import aiml.environment.Environment;

/**
 * <p>
 * A special context type, needed for functionality of the standard 'input'
 * context. To allow recursive calls to the classifier, this context must be
 * implemented as a stack of values.
 * </p>
 * <p>
 * A context with the name 'input' must always be present in the system, and it
 * must be an implementation of InputContext
 * </p>
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public abstract class InputContext extends Context {

  public InputContext(String name) {
    super(name);
  }

  /**
   * Pushes a new input on the stack.
   * 
   * @param input
   *                The value of the input.
   */
  public abstract void push(String input, Environment e);

  /**
   * Pops a value from the stack.
   * 
   * @throws EmptyStackException -
   *                 if the stack is empty
   */
  public abstract void pop(Environment e) throws EmptyStackException;

}
