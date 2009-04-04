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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import aiml.context.Context;
import aiml.context.ContextInfo;
import aiml.environment.Environment;

/**
 * <p>
 * The MatchState class stores complete information about the current match
 * state. It originally started only as a wrapper for certain variables that
 * would be passed down in the recursion tree, but currently it tries to store
 * as much information as possible. In future versions, it might even contain a
 * node traversal stack so that matching can be resumed even after exiting the
 * recursion tree.
 * </p>
 * 
 * @author Kim Sullivan
 * @version 1.0
 * @param <T extends Object> The result type
 */

public class MatchState<T extends Object> {

  private Environment e;

  /** The currently matched context */
  public Context context;

  /**
   * The contextStack list stores the history of context traversal, used for
   * backtracking.
   */
  private LinkedList<Context> contextStack = new LinkedList<Context>();

  /** The current depth in the context */
  public int depth;

  /**
   * An array of Strings that represent the individual state of the context
   * variables during matching.
   */
  String contextValues[];

  /**
   * An array of lists, each list represents wildcards from a context
   */
  List<Wildcard> wildcards[];

  /**
   * This contains the result of the matching.
   */
  private T result;

  /**
   * <p>
   * This inner class represents a single matched wildcard inside a context.
   * Normally, only the beginning and ending positions of the wildcard are
   * stored to save resources during matching. Once the actual wildcard value is
   * requested (this could even be during the process of matching), the wildcard
   * string is constructed.
   * <p>
   * 
   * <p>
   * To maintain consistency during template processing, a copy of the context
   * variable is stored in the MatchState, so that a wildcard request after a
   * set/get/srai (that can theoretically change the "live" context variable
   * still produces the same results). This essentially creates a "local scope"
   * for the match.
   * </p>
   * 
   * @author Kim Sullivan
   * @version 1.0
   */
  public class Wildcard {
    /**
     * The beginning index of a wildcard in a string
     */
    private int beginIndex;

    /**
     * The ending index of a wildcard in a string
     */
    private int endIndex;

    /**
     * The context that this wildcard has been matched in
     */
    private Context context;

    /**
     * Creates a new wildcard with length 0
     * 
     * @param context
     *          The context this wildcard applies to
     * @param beginIndex
     *          The starting position of this wildcard
     */
    public Wildcard(Context context, int beginIndex) {
      this.beginIndex = beginIndex;
      this.endIndex = beginIndex;
      this.context = context;
    }

    /**
     * Increases the size of the string matched by the wildcard by one
     */
    public void grow() {
      endIndex++;
    }

    /**
     * Increases the size of the string matched by the wildcard
     * 
     * @param length
     *          how much more characters are matched by the wildcard
     */
    public void grow(int length) {
      endIndex += length;
    }

    public void growRest() {
      endIndex = contextValues[context.getOrder()].length();
    }

    /**
     * Returns the length of this wildcard
     * 
     * @return the length of this wildcard
     */
    public int getLength() {
      return endIndex - beginIndex;
    }

    /**
     * Returns the starting index of the wildcard
     * 
     * @return the starting index of the wildcard
     */
    public int getBeginIndex() {
      return beginIndex;
    }

    /**
     * Produces the actual wildcard value.
     * 
     * @return the wildcard value
     */
    public String getValue() {
      return contextValues[context.getOrder()].substring(beginIndex, endIndex);
    }

    /**
     * Returns a string representation of this wildcard. Information also
     * includes the start, length and context of the wildcard
     * 
     * @return a string representation of this wildcard
     */
    public String toString() {
      return "WC{" + context.getOrder() + ":(" + getBeginIndex() + "," +
          getLength() + ")=\"" + getValue() + "\"}";
    }
  }

  /**
   * Creates a new MatchState object, makes a snapshot of the context variables.
   */
  public MatchState(Environment e) {
    this.e = e;
    contextValues = new String[getContextInfo().getCount()];
    wildcards = new List[getContextInfo().getCount()];
    if (getContextInfo().getCount() <= 0) {
      throw new NoContextPresentException();
    }
    initializeContexts(e);
  }

  /**
   * <p>
   * Initialize the contexts making a snapshot of the current values (because
   * these values might change during script evaluation, or even during
   * matching). This essentially creates a "local scope" for the current match.
   * </p>
   * 
   * @param e
   */
  private void initializeContexts(Environment e) {
    for (int i = 0; i < getContextInfo().getCount(); i++)
      contextValues[i] = getContextInfo().getContext(i).getValue(e);
  }

  /**
   * Adds a new context to the match state.
   * 
   * @param context
   *          The new context
   */
  public void addContext(Context context) {
    contextStack.addLast(this.context);
    this.context = context;
    depth = 0;
  }

  /**
   * <p>
   * Drops the current context and restores the last. The reason why this isn't
   * called removeContext() is that the context's cached value is retained.
   * </p>
   * 
   * <p>
   * <i>Note to self III:</i> More a side note, really...a Context classes
   * backed by an array might be interesting in some cases.
   * </p>
   */
  public void dropContext() {
    // shouldn't this be error-checked? The default NoSuchElementException is
    // probably enough though...
    this.context = contextStack.removeLast();
    depth = getContextValue().length();
  }

  /**
   * Add a new wildcard to the current context at the current depth
   * 
   * @return Wildcard
   */
  public Wildcard addWildcard() {
    return addWildcard(context, depth);
  }

  /**
   * Add a new wildcard to s context at a certain depth
   * 
   * @param context
   *          the context
   * @param depth
   *          the depth (the starting index of the wildcard)
   * @return Wildcard
   */
  public Wildcard addWildcard(Context context, int depth) {
    Wildcard wc = new Wildcard(context, depth);
    if (wildcards[context.getOrder()] == null) {
      wildcards[context.getOrder()] = new ArrayList<Wildcard>();
    }
    wildcards[context.getOrder()].add(wc);
    return wc;
  }

  /**
   * <p>
   * Return a wildcard. After matching has finished (and in some special
   * contexts, even when it hasn't), this method provides a way to access all
   * wildcards that have been (explicitly or implicitly) bound during matching.
   * </p>
   * 
   * <p>
   * This method throws an {@link InvalidWildcardReferenceException} if an
   * invalid context or wildcard index has been specified.
   * </p>
   * 
   * @param context
   *          The context
   * @param index
   *          The index of the wildcard (1 based)
   * @return The wildcard
   * @throws InvalidWildcardReferenceException
   */
  public Wildcard getWildcard(Context context, int index)
      throws InvalidWildcardReferenceException {
    if (isValidWildcard(context, index)) {
      return wildcards[context.getOrder()].get(index - 1);
    }
    if (wildcards[context.getOrder()] == null &&
        isDontCareWildcard(context, index)) {
      //lazily bind implicit wildcards (from don't care contexts)
      Wildcard wc = addWildcard(context, 0);
      wc.growRest();
      return wc;
    }
    throw new InvalidWildcardReferenceException(context, index);
  }

  /**
   * <p>
   * Returns {@code true} if the specific wildcard comes from a "don't care"
   * context. This means, that a particular context wasn't explicitly specified
   * for this category and therefore isn't included in the wildcards. In this
   * case, using &lt;star&gt; returns the whole context.
   * </p>
   * 
   * @param context
   *          - the context ID
   * @param index
   *          - the wildcard index (0 based)
   * @return <code>true</code> if the wildcard is bound to a "don't care"
   *         context.
   */
  private boolean isDontCareWildcard(Context context, int index) {
    return !contextStack.contains(context) && index == 1;
  }

  /**
   * Checks if the context id and the wildard index are valid.
   * 
   * @param context
   *          - the context ID
   * @param index
   *          - the wildcard index (0 based)
   * @return <code>true</code> if the index and context are valid
   */
  private boolean isValidWildcard(Context context, int index) {
    return wildcards[context.getOrder()] != null && index >= 1 &&
        index <= wildcards[context.getOrder()].size();
  }

  /**
   * Removes the last wildcard during matching.
   */
  public void removeWildcard() {
    wildcards[context.getOrder()].remove(wildcards[context.getOrder()].size() - 1);
  }

  /**
   * Returns the value of the current context.
   * 
   * @return the value of the current context
   */
  public String getContextValue() {
    return contextValues[context.getOrder()];
  }

  /**
   * Set the result object.
   * 
   * @param o
   *          the result object
   */
  public void setResult(T o) {
    result = o;
  }

  /**
   * Get the result object
   * 
   * @return the result object
   */
  public T getResult() {
    return result;
  }

  /**
   * Return a string representation of the match state. If a match was found,
   * return the objects' toString() value, otherwise details about the match
   * state. Also includes a list of current wildcards.
   * 
   * @return a string representation of the match state.
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    if (result == null) {
      sb.append("[CONTEXT]" + context + "[DEPTH]" + depth + "\n" + "[CVALUE]" +
          getContextValue() + "\n");
    } else {
      sb.append("[RESULT]" + result + "\n");
    }
    sb.append("[WILDCARDS]\n");
    for (int i = 0; i < wildcards.length; i++) {
      if ((wildcards[i] != null) && (wildcards[i].size() > 0)) {
        sb.append("<" + i + ">" + wildcards[i] + "\n");
      }
    }

    return sb.toString();
  }

  public Environment getEnvironment() {
    return e;
  }

  private ContextInfo getContextInfo() {
    return e.getBot().getClassifier().getContextInfo();
  }

}
