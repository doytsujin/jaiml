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

package aiml.classifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import aiml.context.Context;
import aiml.context.ContextInfo;

/**
 * <p>
 * This class keeps a collection of context definitions that represent a single
 * category. In AIML, this is often referred to as the "context of the category"
 * (in opposition to a single context like the input, or topic). It uses the
 * "deprecated" name Path instead of something like CategoryContext, mainly
 * because I feel there are already too much classes containing the name
 * "context".
 * </p>
 * 
 * <p>
 * This class keeps a collection of the patterns of a category. Due to the
 * ordered nature of contexts, I think it is best to keep them in a priority
 * queue, which is exactly what this class does - provides acess to an ordered
 * list of context patterns
 * </p>
 * <p>
 * <i>Note to self:</i> Will have to implement the cloneable interface and make
 * deep copies of paths, because when reading AIML files, individual contexts
 * can come in- and out of scope. Also, the feature of having multiple contexts
 * of the same type for a category will have to be solved somehow.
 * </p>
 * <p>
 * <i>Note to self II:</i> This class will have to be reworked some time, for
 * more general contexts than just pattern strings... maybe replace "pattern"
 * with "condition" or "constraint" or something like that
 * </p>
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public class Path {
  /** An internal linked list to represent the priority queue */
  private LinkedList<Pattern> contextQueue = new LinkedList<Pattern>();

  /** An internal stack of previous path states (used for loading) */
  private LinkedList<LinkedList<Pattern>> historyStack = new LinkedList<LinkedList<Pattern>>();

  private ContextInfo contextInfo;

  /**
   * A wrapper for a context pattern.
   * 
   * @author Kim Sullivan
   * @version 1.0
   */
  public class Pattern {
    /** This pattern's context */
    private Context context;

    /** The actual pattern */
    private String pattern;

    /**
     * Creates a new Pattern and sets it's context and value
     * 
     * @param context
     *          the context
     * @param pattern
     *          the pattern
     */
    public Pattern(Context context, String pattern) {
      this.context = context;
      this.pattern = pattern;
    }

    /**
     * Gets the context of the pattern
     * 
     * @return the context of the pattern
     */
    public Context getContext() {
      return context;
    }

    /**
     * Gets the pattern
     * 
     * @return the pattern
     */
    public String getPattern() {
      return pattern;
    }

    /**
     * Returns a string representation of the Pattern object. The resulting
     * string contains info about the pattern, and the context.
     * 
     * @return a string representation of this Pattern object
     */
    public String toString() {
      return context.getName() + "=\"" + pattern + "\"";
    }
  }

  /**
   * The default constructor that creates a new empty path. Other overloaded
   * constructors are not provided, because in real-world situations the path
   * will be built incrementally as the <context> tags will be read.
   * 
   * @param contextInfo
   *          TODO
   */
  public Path(ContextInfo contextInfo) {
    this.contextInfo = contextInfo;
  }

  /**
   * <p>
   * Adds a whole bunch of patterns to the path.
   * </p>
   * <p>
   * The array takes the form of:
   * <code>new String[][] {{"contextname","pattern"},{"othercontext","differentpattern"},...}</code>
   * </p>
   * 
   * @param path
   *          an array of name-pattern pairs
   * @throws MultipleContextsException
   */
  public void add(String[][] path) throws MultipleContextsException {
    for (int i = 0; i < path.length; i++) {
      add(path[i][0], path[i][1]);
    }
  }

  /**
   * Adds a single context pattern to the path.
   * 
   * @param context
   *          the context
   * @param pattern
   *          the pattern
   * @throws MultipleContextsException
   * 
   * @throws MultipleContextsException
   */
  public void add(String context, String pattern)
      throws MultipleContextsException {
    //if (context.equals("input") && pattern.equals("64 F *"))
    //  System.out.println("["+context+"]\""+pattern+"\"");
    Context c = contextInfo.getContext(context);
    Pattern p = new Pattern(c, pattern);
    ListIterator<Pattern> i = contextQueue.listIterator();
    if (!i.hasNext()) {
      i.add(p);
    } else {
      while (i.hasNext()) {
        Pattern pi = i.next();
        if (pi.context.getOrder() == p.context.getOrder()) {
          throw new MultipleContextsException(c.getName());
        }
        if (pi.context.getOrder() > p.context.getOrder()) {
          i.previous();
          i.add(p);
          return;
        }
      }
      i.add(p);
    }

  }

  /**
   * Saves the current path onto a stack (used for loading nested context
   * groups)
   */
  @SuppressWarnings("unchecked")
  public void save() {
    historyStack.addFirst((LinkedList<Pattern>) contextQueue.clone());
  }

  /**
   * Restores the last path.
   * 
   * @throws NoSuchElementException
   */
  public void restore() {
    contextQueue = historyStack.removeFirst();
  }

  /**
   * Returns a string representation of this path.
   * 
   * @return a string representation of this path
   */
  public String toString() {
    return contextQueue.toString();
  }

  /**
   * Returns an unmodifiable iterator of the priority queue.
   * 
   * @return the unmodifiable iterator
   */
  public ListIterator iterator() {
    return Collections.unmodifiableList(contextQueue).listIterator();
  }

  /**
   * Returns the length of the path - the number of patterns.
   * 
   * @return the length of the path
   */
  public int getLength() {
    return contextQueue.size();
  }
}
