/*
    jaiml - java AIML library
    Copyright (C) 2004-2005  Kim Sullivan

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
 * This class represents information about a single context - it's name, order
 * and most importantly it provides a method to query the context's current
 * value. Classes that inherit from Context must implement the getValue() method
 * so that it returns meaningful values.
 * 
 * To do: Rename to ContextDeclaration Add a type Add multiuser functionality
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public abstract class Context implements Comparable {
  /** The name of this context */
  private String name;

  /**
   * The order (or position) of this context. This is a WORM value (write once
   * read many).
   */
  private int order = -1;

  /**
   * Creates a new context. The order can't be specified in the constructor,
   * because it is dependent on the order of insertion in the ContextInfo class.
   * 
   * <p>
   * <i>Note to self:</i>Would it be better if the order was set automatically
   * at creation time with the use of a static counter?
   * </p>
   * 
   * @param name
   *                The name of this context.
   */
  public Context(String name) {
    this.name = name;
  }

  /**
   * Gets the name of the context.
   * 
   * @return the name of this context
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the order (position) of the context.
   * 
   * @return the order of this context
   */
  public int getOrder() {
    // Maybe this should throw some kind of exception if the irder is -1, and
    // the Context hasn't been registered with ContextInfo yet...
    return order;
  }

  /**
   * Set the order of a context (non-negative value). Works only once, further
   * calls to setOrder() do not have an effect.
   * 
   * @param value
   *                the order
   */
  public void setOrder(int value) {
    if (value >= 0) {
      if (order == -1) {
        order = value;
      }
    }
  }

  /**
   * Compares Context objects according to their order.
   * 
   * @param c
   *                Object
   * @return -1 if this context has a lower order; 0 if they have the same
   *         order; 1 if this context has a bigger order
   */
  public int compareTo(Object c) {
    int corder = ((Context) c).getOrder();
    return (order < corder ? -1 : (order == corder ? 0 : 1));
  }

  /**
   * Indicates wether two contexts are equal
   * 
   * @param c
   *                Object
   * @return <code>true</code> if both contexts have the same name and order;
   *         <code>false</code> otherwise
   */
  public boolean equals(Object c) {
    return (c instanceof Context) && (((Context) c).getName() == name) &&
        (((Context) c).getOrder() == order);
  }

  /**
   * Get the value currently associated with the context in the specified
   * environment. This method must be overriden in inherited classes to provide
   * meaningfull behaviour.
   * 
   * @return The value currently associated with the context.
   */
  public abstract String getValue(Environment e);
}
