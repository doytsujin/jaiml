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

package aiml.classifier.node;

import graphviz.Graphviz;
import graphviz.GraphvizNode;

import java.util.ListIterator;

import aiml.classifier.ContextNode;
import aiml.classifier.DuplicatePathException;
import aiml.classifier.LeafContextNode;
import aiml.classifier.MatchState;
import aiml.classifier.PatternContextNode;

/**
 * <p>
 * This class represents the base class of all the nodes in the Pattern tree.
 * </p>
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public abstract class PatternNode implements GraphvizNode {

  /**
   * All nodes that represent an exact match
   */
  public static final int STRING = 1;

  /**
   * All wildcard nodes created from an underscore in the pattern
   */
  public static final int UNDERSCORE = 0;

  /**
   * All wildcard nodes created from a star in the pattern
   */
  public static final int STAR = 2;

  /**
   * All other nodes, such as branch nodes
   */
  public static final int OTHER = -1;

  /**
   * The type of this node. I feel it is better to express the type with numbers
   * instead of the class hierarchy, because star and underscore are implemented
   * by the same set of classes.
   */
  protected int type = PatternNode.OTHER;

  /**
   * Points to the next subcontext. Applicalble if this node terminates a
   * pattern, otherwise <code>null</code>
   */
  protected ContextNode subContext;

  /**
   * A unique node identifier, used for node output 
   */
  private static int gvMaxId=0;
  private int gvId;
  
  public PatternNode() {
    gvId = gvMaxId++;
  }
  
  /**
   * <p>
   * This inner class is a wrapper for the
   * {@link aiml.classifier.node.PatternNode#add(int depth, String pattern) add(int, String)}
   * result type, which needs to return several values, and Java doesn't support
   * "pass by reference" method parameters.
   * </p>
   * 
   * <p>
   * The classes' fields are currently public (probably not a good OOP
   * practice), I believe that having proper get methods for private fields is
   * in this case superfluous (it's only a wrapper for return values).
   * </p>
   */
  public class AddResult {
    /**
     * The root node of the resulting tree after adding the <code>pattern</code>
     */
    public PatternNode root;

    /**
     * The "leaf" node, the deepest node that was covered/created by this add()
     * call.
     */
    public PatternNode leaf;

    /**
     * The new depth after adding a part of the pattern to the current node.
     */
    public int newDepth;

    public AddResult(PatternNode root, PatternNode leaf, int newDepth) {
      this.root = root;
      this.leaf = leaf;
      this.newDepth = newDepth;
    }
  }

  /**
   * <p>
   * Adds the <code>pattern</code> to itself. Implementations should preform
   * any necessary node splitting. It is assumed that each node class knows of
   * itself and the other more specific node classes, and returns the
   * appropriate type. In the future, this decision logic might be moved into a
   * separate helper class that takes a node type and a pattern and returns the
   * most optimal type.
   * </p>
   * 
   * @param depth
   *                the current depth, or position in the pattern
   * @param pattern
   *                the whole currently added pattern
   * @return the resulting root and leaf nodes after adding part of the pattern
   */

  public abstract AddResult add(int depth, String pattern);

  /**
   * <p>
   * Matches the current context value starting at the depth specified in the
   * match state.
   * </p>
   * 
   * @param match
   *                the match state
   * @return <code>true</code> if the match was successful; <code>false</code>
   *         if not
   */

  public abstract boolean match(MatchState match);

  /**
   * <p>
   * Adds a new subcontext.
   * </p>
   * 
   * @param path
   *                ListIterator
   * @param o
   *                Object
   */
  public void addContext(ListIterator path, Object o)
      throws DuplicatePathException {
    if (subContext != null) {
      subContext = subContext.add(path, o);
    } else {
      if (path.hasNext()) {
        subContext = new PatternContextNode(path, o);
      } else {
        subContext = new LeafContextNode(o);
      }
    }
  }

  /**
   * Returns the type of this pattern node
   * 
   * @return the type of this pattern node
   */
  public int getType() {
    return type;
  }

  /**
   * Return a string representation of this node.
   * 
   * @return the string representation of this node
   */

  public String toString() {
    return "" + subContext;
  }
  
  /* (non-Javadoc)
   * @see aiml.classifier.node.GraphvizProducer#gvName()
   */
  public String gvNodeID() {
    return getClass().getSimpleName()+"_"+gvId;
  }
  
  /* (non-Javadoc)
   * @see aiml.classifier.node.GraphvizProducer#gvLabel()
   */
  public String gvNodeLabel() {
     return getClass().getSimpleName();
  }
  
  /* (non-Javadoc)
   * @see aiml.classifier.node.GraphvizProducer#gvGraph(java.lang.StringBuilder)
   */
  public StringBuilder gvGraph(StringBuilder sb) {
    gvNodes(sb);
    gvInternalGraph(sb);
    gvExternalGraph(sb);
    return sb;
  }

  /* (non-Javadoc)
   * @see aiml.classifier.node.GraphvizProducer#gvNodes(java.lang.StringBuilder)
   */
  public StringBuilder gvNodes(StringBuilder sb) {
    Graphviz.node(sb, gvNodeID(), "label",gvNodeLabel());
    return sb;
  }

  /* (non-Javadoc)
   * @see aiml.classifier.node.GraphvizProducer#internalGraph(java.lang.StringBuilder)
   */
  public StringBuilder gvInternalGraph(StringBuilder sb) {
    return sb;
  }
  
  /* (non-Javadoc)
   * @see aiml.classifier.node.GraphvizProducer#externalGraph(java.lang.StringBuilder)
   */
  public StringBuilder gvExternalGraph(StringBuilder sb) {
    return Graphviz.connectGraph(sb, this, subContext, "");
  }
}
