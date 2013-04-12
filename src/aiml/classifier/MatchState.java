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
  private LinkedList<Integer> depthStack = new LinkedList<Integer>();

  /** The current depth in the context */
  public int depth;

  /**
   * An array of Strings that represent the individual state of the context
   * variables during matching.
   */
  String contextValues[];
  /** A lazily initialized chache of normalized context values */
  private String normalizedContextValues[];

  /**
   * An array of lists, each list represents wildcards from a context
   */
  List<Wildcard> wildcards[];

  /**
   * This contains the result of the matching.
   */
  private T result;

  /** Contains information about wether the match was succesfull */
  private boolean matchSuccesful;

  private MatchStatistics matchStatistics = new MatchStatistics();

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

    /**
     * Make the wildcard match the rest of the context
     */
    public void growRest() {
      endIndex = contextValues[context.getOrder()].length();
    }

    /**
     * Specify the new end position of the wildcard.
     * 
     * @param depth
     *          the new end
     */
    public void growTo(int depth) {
      assert (depth > endIndex) : "Growing a wildcard mustn't shrink it (new end index must be larger than the current)";
      endIndex = depth;

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
      return "WC{" + context + ":(" + getBeginIndex() + "," + getLength() +
          ")=\"" + getValue() + "\"}";
    }
  }

  /**
   * Creates a new MatchState object, makes a snapshot of the context variables.
   */
  public MatchState(Environment e) {
    this.e = e;
    contextValues = new String[getContextInfo().getCount()];
    normalizedContextValues = new String[getContextInfo().getCount()];
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
      contextValues[i] = getContextInfo().getContext(i).getValue(e).toString();
  }

  /**
   * This method is called whenever a new context is entered.
   * 
   * @param context
   *          The new context
   */
  public void enterContext(Context context) {
    matchStatistics.addContextEnter();
    contextStack.addLast(this.context);
    depthStack.addLast(this.depth);
    this.context = context;
    depth = 0;
  }

  /**
   * Called whenever the current context is left when backtracking. Drops the
   * current context and restores the last.
   */
  public void leaveContext() {
    // shouldn't this be error-checked? The default NoSuchElementException is
    // probably enough though...
    matchStatistics.addContextLeave();
    this.context = contextStack.removeLast();
    this.depth = depthStack.removeLast();
  }

  /**
   * This method is called whenever a node is entered for a first time.
   */
  public void enterNode() {

    matchStatistics.addNodeEnter();
  }

  /**
   * This method is called whenever a node is re-entered (due to a self loop)
   */
  public void reenterNode() {
    matchStatistics.addNodeLoop();
  }

  /**
   * This method is called whenever a node is left. The <code>success</code> is
   * <b>true</b> if the node is left as a result of a sucesful match and
   * <code>false</code> if it is left during backtracking.
   * 
   * @param success
   *          signifies if the node has been left during backtracking or after a
   *          succesfull match
   * @return returns the input parameter
   */
  public boolean leaveNode(boolean success) {
    if (!success) {
      matchStatistics.addNodeLeave();
    }
    return success;
  }

  /**
   * Returns statistical information about the current match
   * 
   * @return match statistics
   */
  public MatchStatistics getMatchStatistics() {
    return matchStatistics;
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
    if (isDontCareWildcard(context, index)) {
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
    if (normalizedContextValues[context.getOrder()] == null) {
      normalizedContextValues[context.getOrder()] = Pattern.normalize(contextValues[context.getOrder()]);
    }
    return normalizedContextValues[context.getOrder()];
  }

  /**
   * Set the result object.
   * 
   * @param o
   *          the result object
   */
  public void setResult(T o) {
    matchSuccesful = true;
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
   * Returns information about a successful match
   * 
   * @return <code>true</code> if the match was successful and the result object
   *         is valid
   */
  public boolean isSuccess() {
    return matchSuccesful;
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
