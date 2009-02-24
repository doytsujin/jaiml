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

package aiml.classifier.node;

import graphviz.Graphviz;
import aiml.classifier.MatchState;
import aiml.classifier.Pattern;

/**
 * <p>
 * A wildcard node in the pattern tree. This node reluctantly matches one or
 * more characters from the current context, until a match is found or a match
 * fails.</p
 * 
 * <p>
 * The current implementation optimizes only trailing wildcards (wildcards that
 * have no child pattern nodes).
 * </p>
 * 
 * <p>
 * Another very powerful optimization utilizes look-ahead, where the set of
 * characters that follow the wildcard is used to skip larger portions of the
 * input. This works only when there are no wildcards in the follow set. Look
 * ahead is currently not implemented.
 * </p>
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public class WildcardNode extends PatternNode {
  /**
   * Sub patterns
   */
  private PatternNode next;

  /**
   * Create a new empty wildcard of the specified type. This constructor is
   * private, the only way to create a new wildcard node is via the
   * <code>PatternNodeFactory</code> class.
   * 
   * @param type
   *          the wildcard type
   */

  private WildcardNode(int type) {
    this.type = type;
  }

  /**
   * Add the pattern to the node. As a side effect, Branch or EOS nodes may be
   * inserted in the original place of this node.
   */
  public AddResult add(int depth, String pattern) {
    AddResult result;
    if (depth == pattern.length()) {
      PatternNode node = new EndOfStringNode(this);
      result = node.add(depth, pattern);
      return result;
    }
    if (Pattern.getType(depth, pattern) != type) {
      PatternNode node = new BranchNode(this);
      result = node.add(depth, pattern);
      return result;
    }

    depth++;

    //if we're at the end of the pattern, don't create unnecessary EOS nodes
    if (depth == pattern.length()) {
      return new AddResult(this, this, depth);
    }

    if (next == null) {
      next = PatternNodeFactory.getInstance(depth, pattern);
    }
    result = next.add(depth, pattern);
    next = result.root;
    result.root = this;
    return result;

  }

  /**
   * Match the curent state to the wildcard. Matching proceeds reluctantly
   * (non-greedily) one character at a time, except if this wildcard has no
   * child pattern nodes.
   */
  public boolean match(MatchState match) {
    MatchState.Wildcard w = match.addWildcard();
    if (next != null) { //there are subnodes we have to try
      while (match.depth < match.getContextValue().length()) {
        match.depth++;
        w.grow();
        if (next.match(match)) {
          return true;
        }
      }
    } // if (next!=null)
    else { //this wildcard is "trailing" - add the rest of the input to it
      w.growRest();
      match.depth += w.getLength();
    }

    if (subContext != null) {
      if (subContext.match(match)) {
        return true;
      }
    }

    match.depth -= w.getLength();
    match.removeWildcard();
    return false;

  }

  /**
   * Register this node type in PatternNodeFactory. This actually registers 2
   * node types, one for the * and one for the _ wildcard.
   */
  public static void register() {
    PatternNodeFactory.registerNode(new Creatable() {
      public boolean canCreate(int depth, String pattern) {
        return (Pattern.isStar(depth, pattern));
      }

      public PatternNode getInstance() {
        return new WildcardNode(PatternNode.STAR);
      }

    });
    PatternNodeFactory.registerNode(new Creatable() {
      public boolean canCreate(int depth, String pattern) {
        return (Pattern.isUnderscore(depth, pattern));
      }

      public PatternNode getInstance() {
        return new WildcardNode(PatternNode.UNDERSCORE);
      }

    });

  }

  @Override
  public String toString() {
    if (next != null) {
      return "WildCard" + next.toString() + super.toString();
    }
    return "WildCard" + super.toString();
  }

  public String gvNodeLabel() {
    switch (type) {
    case PatternNode.STAR:
      return "*";
    case PatternNode.UNDERSCORE:
      return "_";
    default:
      return "WC" + type;
    }
  }

  @Override
  public void gvInternalGraph(Graphviz graph) {
    graph.edge(gvNodeID(), gvNodeID(), "label", Graphviz.ALPHABET);
    graph.connectGraph(this, next, Graphviz.ALPHABET);
  }

  @Override
  public void gvExternalGraph(Graphviz graph) {
    graph.connectGraph(this, subContext, Graphviz.ALPHABET);
  }

}
