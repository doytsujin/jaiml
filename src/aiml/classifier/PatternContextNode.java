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

import graphviz.Graphviz;

import java.util.ListIterator;

import aiml.classifier.node.PatternNode;
import aiml.classifier.node.PatternNodeFactory;

/**
 * <p>
 * This class represents a node in the tree of contexts. Each context imposes
 * another constraint on the state of the matching system.
 * </p>
 * <p>
 * Currently, the only types of contexts in AIML are pattern type contexts, and
 * even though the design of the context tree tries to be as general as
 * possible, there are some places that will need redesigning once more types of
 * contexts are to be supported (this applies mainly to the add() method and the
 * way paths are currently handled - jumping back and forth with using a
 * ListIterator doesn't seem too elegant)
 * </p>
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public class PatternContextNode extends ContextNode {
  /** The subtree of pattern nodes */
  private PatternNode tree;

  /**
   * Create a new context tree with no subcontexts.
   * 
   * @param context
   *          the context ID
   */
  public PatternContextNode(int context) {
    this.context = context;
  }

  /**
   * Create a new context tree from the current pattern in the path. Adds all
   * it's substructures (subcontexts and pattern trees), together with the final
   * object.
   * 
   * @param path
   *          the path
   * @param o
   *          Object
   */
  public PatternContextNode(ListIterator path, Object o) {
    if (!path.hasNext()) {
      throw new UnsupportedOperationException(
          "Can't add an empty path to a PatternContextNode");
    }
    Path.Pattern pattern = (Path.Pattern) path.next();
    context = pattern.getContext();
    path.previous();
    try {
      add(path, o);
    } catch (DuplicatePathException e) {
      assert false : "Duplicate path exception after adding a single path to a newly created empty tree - should never happen";
    }
  }

  /**
   * <p>
   * Create a new context tree with a subcontext
   * </p>
   * 
   * @param context
   *          the context ID
   * @param subcontext
   *          the subcontext tree
   */
  public PatternContextNode(int context, ContextNode subcontext) {
    this.context = context;
    this.next = subcontext;
  }

  /**
   * Adds a pattern to this context.
   * 
   * <i>Note:</i> This method will be reworked once generalized context types
   * are implemented (i.e. it will be replaced by an addGeneralContextType()
   * method.
   * 
   * @param pattern
   *          Pattern
   */
  public PatternNode addPattern(Path.Pattern pattern) {
    //add the pattern into the current tree.
    String s = pattern.getPattern();
    if (tree == null) {
      tree = PatternNodeFactory.getInstance(0, s);
    }
    PatternNode.AddResult result = tree.add(0, s);
    assert (result.newDepth == s.length()) : "A pattern node tree has failed to add a pattern completely";
    tree = result.root; //update the pattern subtree
    return result.leaf;

  }

  /**
   * Try to match the current match state.
   * 
   * @param match
   *          MatchState
   * @return <code>true</code> if a match was found; <code>false</code> if the
   *         match failed
   */
  public boolean match(MatchState match) {
    match.addContext(context);

    if (!tree.match(match)) {
      match.dropContext();
      if (next != null) {
        return next.match(match);
      } else {
        return false;
      }
    } else {
      return true;
    }

  }

  /** Returns a string representation of this context node */
  public String toString() {
    return "<" + context + ">" + tree + "\n" + "[" + context + ".NEXT]: " +
        next;
  }

  public Graphviz gvGraph(Graphviz graph) {
    graph.start(("subgraph cluster_" + context + "_" + hashCode()));
    super.gvGraph(graph);
    graph.end();
    return graph;
  }

  public Graphviz gvNodes(Graphviz graph) {
    return graph.node(gvNodeID(), "label", "<" + context + ">", "shape",
        "diamond");
  }

  public Graphviz gvInternalGraph(Graphviz graph) {
    if (tree != null) {
      graph.edge(gvNodeID(), tree.gvNodeID());
      tree.gvGraph(graph);
    }
    return graph;
  }

}
