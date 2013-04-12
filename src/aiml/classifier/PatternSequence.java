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
import aiml.util.PeekIterator;

/**
 * <p>
 * This class keeps an ordered collection of context definitions ("patterns")
 * that represent a single category. In AIML, this is often referred to as the
 * "context of the category" (in opposition to a single context like the input,
 * or topic).
 * </p>
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public class PatternSequence implements Iterable<PatternSequence.Pattern> {
  /** An internal linked list to represent the priority queue */
  private LinkedList<Pattern> contextQueue = new LinkedList<Pattern>();

  /** An internal stack of previous sequence states (used for loading) */
  private LinkedList<LinkedList<Pattern>> historyStack = new LinkedList<LinkedList<Pattern>>();

  private ContextInfo contextInfo;

  /**
   * A wrapper for a context pattern.
   * 
   * @author Kim Sullivan
   * @version 1.0
   */
  public static class Pattern {
    /** This pattern's context */
    private Context<? extends Object> context;

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
    public Pattern(Context<? extends Object> context, String pattern) {
      this.context = context;
      this.pattern = pattern;
    }

    /**
     * Gets the context of the pattern
     * 
     * @return the context of the pattern
     */
    public Context<? extends Object> getContext() {
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
      StringBuilder sb = new StringBuilder();
      sb.append('[').append(context.getName()).append(']');
      sb.append(pattern);
      return sb.toString();
    }
  }

  /**
   * Represents a <code>PeekIterator&lt;Pattern&gt;</code>.
   * 
   * @author Kim Sullivan
   * 
   */
  public static class PatternIterator extends PeekIterator<Pattern> {
    public PatternIterator(Iterable<Pattern> iterable) {
      super(iterable);
    }
  }

  /**
   * The default constructor that creates a new empty sequence. Other overloaded
   * constructors are not provided, because in real-world situations the
   * sequence will be built incrementally as the &lt;context&gt; tags will be
   * read.
   * 
   * @param contextInfo
   *          TODO
   */
  public PatternSequence(ContextInfo contextInfo) {
    this.contextInfo = contextInfo;
  }

  /**
   * Create a new PatternSequence as a copy from the given sequence. The new
   * sequence is the same, except the history stack is empty.
   * 
   * @param sequence
   *          the original sequence
   */
  public PatternSequence(PatternSequence sequence) {
    this.contextInfo = sequence.contextInfo;
    this.contextQueue = (LinkedList<Pattern>) sequence.contextQueue.clone();
  }

  /**
   * <p>
   * Adds a whole bunch of patterns to the sequence.
   * </p>
   * <p>
   * The array takes the form of:
   * <code>new String[][] {{"contextname","pattern"},{"othercontext","differentpattern"},...}</code>
   * </p>
   * 
   * @param contexts
   *          an array of name-pattern pairs
   * @throws MultipleContextsException
   */
  public void add(String[][] contexts) throws MultipleContextsException {
    for (int i = 0; i < contexts.length; i++) {
      add(contexts[i][0], contexts[i][1]);
    }
  }

  /**
   * Adds a single context pattern to the sequence.
   * 
   * @param context
   *          the name of a context
   * @param pattern
   *          the pattern
   * @throws MultipleContextsException
   * 
   * @throws MultipleContextsException
   */
  public void add(String context, String pattern)
      throws MultipleContextsException {
    add((Context<? extends Object>) contextInfo.getContext(context), pattern);

  }

  /**
   * Adds a single context pattern to the sequence.
   * 
   * @param context
   *          the context
   * @param pattern
   *          the pattern
   * @throws MultipleContextsException
   * 
   * @throws MultipleContextsException
   */
  public void add(Context<? extends Object> context, String pattern)
      throws MultipleContextsException {
    Pattern p = new Pattern(context, pattern);
    add(p);
  }

  /**
   * Adds a single context pattern to the sequence.
   * 
   * @param pattern
   *          the pattern
   * @throws MultipleContextsException
   * 
   * @throws MultipleContextsException
   */

  public void add(Pattern pattern) throws MultipleContextsException {
    ListIterator<Pattern> i = contextQueue.listIterator();
    if (!i.hasNext()) {
      i.add(pattern);
    } else {
      while (i.hasNext()) {
        Pattern pi = i.next();
        if (pi.context.compareTo(pattern.context) == 0) {
          throw new MultipleContextsException(pattern.context.getName());
        }
        if (pi.context.compareTo(pattern.context) > 0) {
          i.previous();
          i.add(pattern);
          return;
        }
      }
      i.add(pattern);
    }
  }

  /**
   * Saves the current pattern sequence onto a stack (used for loading nested
   * context groups)
   */
  @SuppressWarnings("unchecked")
  public void save() {
    historyStack.addFirst((LinkedList<Pattern>) contextQueue.clone());
  }

  /**
   * Restores the previous pattern sequence.
   * 
   * @throws NoSuchElementException
   */
  public void restore() {
    contextQueue = historyStack.removeFirst();
  }

  /**
   * Returns a string representation of this sequence.
   * 
   * @return a string representation of this sequence
   */
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (Pattern p : contextQueue) {
      result.append(p);
    }
    return result.toString();
  }

  /**
   * Returns an unmodifiable iterator of the priority queue.
   * 
   * @return the unmodifiable iterator
   */
  public PatternIterator iterator() {
    LinkedList<Pattern> filledList = new LinkedList<Pattern>();
    PatternIterator patterns = new PatternIterator(contextQueue);

    for (int order = 0; order < contextInfo.getCount(); order++) {
      Context context = contextInfo.getContext(order);
      if (!patterns.hasNext() ||
          context.compareTo(patterns.peek().getContext()) < 0) {
        if (context.getBehaviour().hasDefaultPattern()) {
          filledList.add(new Pattern(context,
              context.getBehaviour().getDefaultPattern()));
        }
      } else {
        filledList.add(patterns.next());
      }
    }
    return new PatternIterator(Collections.unmodifiableList(filledList));
  }

  /**
   * Returns the length of the sequence - the number of patterns.
   * 
   * @return the length of the sequence
   */
  public int getLength() {
    return contextQueue.size();
  }
}
