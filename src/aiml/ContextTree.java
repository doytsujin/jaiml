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

package aiml;

import java.util.*;
import aiml.node.*;

/**
 * <p>This class represents a node in the tree of contexts. Each context imposes
 * another constraint on the state of the matching system. </p>
 * <p>Currently, the only
 * types of contexts in AIML are pattern type contexts, and even though the
 * design of the context tree tries to be as general as possible, there are
 * some places that will need redesigning once more types of contexts are to
 * be supported (this applies mainly to the add() method and the way paths are
 * currently handled - jumping back and forth with using a ListIterator doesn't
 * seem too elegant)</p>
 *
 * Todo: Rename to PatternContextNode, create superclass ContextNode
 * @author Kim Sullivan
 * @version 1.0
 */

public class ContextTree {
  /** The context this tree applies to */
  private int context;

  /** The subtree of pattern nodes */
  private PatternNode tree;

  /**
   * the next lower context (subcontext), used when we fail matching to this
   * context's pattern tree or if there wasn't any pattern tree in the first place
   * (the latter shouldn't happen - if there are no patterns, there should not
   * be a context tree for them)
   */
  private ContextTree next;

  /**
   * Create a new context tree with no subcontexts.
   * @param context the context ID
   */
  public ContextTree(int context) {
    this.context = context;
  }

  /**
   * Create a new context tree from the current pattern in the path. Adds all
   * it's substructures (subcontexts and pattern trees), together
   * wit the final object.
   * @param path the path
   * @param o Object
   */
  public ContextTree(ListIterator path, Object o) {
    if (!path.hasNext()) {
      throw new UnsupportedOperationException(
          "Can't add an empty path to a regular ContextTree");
    }
    Path.Pattern pattern = (Path.Pattern) path.next();
    this.context = pattern.getContext();
    path.previous();
    try {
      add(path, o);
    }
    catch (DuplicatePathException e) {}
    ; //since we just created a new subtree, there's no way this exception can occur
  }

  /**
   * <p>Create a new context tree with a subcontext</p>
   *
   * @param context the context ID
   * @param subcontext the subcontext tree
   */
  public ContextTree(int context, ContextTree subcontext) {
    this.context = context;
    this.next = subcontext;
  }

  /**
   * Adds the path (from the current position) to itself. Creates all necessary
   * pattern subtrees and subcontexts, ensures that the proper order of contexts
   * is maintained.
   * @param path the path
   * @param o the object to be added to the tree
   * @throws DuplicatePathException
   * @return the resulting context tree, with all modifications applied and the correct ordering.
   */
  public ContextTree add(ListIterator path, Object o) throws
      DuplicatePathException {
    /*check the context order, if it's OK to add the current path to this context
     *or if we need to add a new context somewhere.
     *if we're adding it here, run the rootnode's add() method and
     * recursively construct the pattern tree for this context.
     */
    if (path.hasNext()) { //We're in the middle of the path
      Path.Pattern pattern = (Path.Pattern) path.next();
      if (pattern.getContext() > context) {
        //add as next
        path.previous();
        if (next == null) {
          next = new ContextTree(pattern.getContext());
        }
        next = next.add(path, o);
        return this;
      }
      else if (pattern.getContext() < context) {
        //add instead of self
        ContextTree ct = new ContextTree(pattern.getContext(), this);
        path.previous();
        return ct.add(path, o); //actually, just ct should be sufficient, but let's not make any assumptions
      }
      else {
        //add the pattern into the current tree.
        String s = pattern.getPattern();
        if (tree == null) {
          tree = PatternNodeFactory.getInstance(0, s);
        }
        PatternNode.AddResult result = tree.add(0, s);
        //sanity check:
        while (result.newDepth < s.length()) {
          //this should actually never happen, add() should recursively process the whole pattern
          result = result.leaf.add(result.newDepth, s);
          throw new RuntimeException("Damn...");
        }
        /*still continuing sanity check, due to the above loop, can never be true, but
          I might remove the loop later*/
        if (result.newDepth != s.length()) {
          throw new RuntimeException("Failure when adding pattern \"" + s +
                                     "\" to context " + context);
        }
        tree = result.root; //update the pattern subtree
        result.leaf.addContext(path, o);
        return this;
      }
    }
    else { //we're at the end of the path, so we can add a Leaf node
      if (next == null) {
        next = new LeafContextTree(o);
      }
      else {
        next = next.add(path, o);
      }
      return this;
    }
  }

  /**
   * Try to match the current match state.
   * @param match MatchState
   * @return <code>true</code> if a match was found; <code>false</code> if the match failed
   */
  public boolean match(MatchState match) {
    match.addContext(context);

    if (!tree.match(match)) {
      match.dropContext();
      if (next != null) {
        return next.match(match);
      }
      else {
        return false;
      }
    }
    else {
      return true;
    }

  }

  /**Returns a string representation of this context node*/
  public String toString() {
    return
        "<" + context + ">" + tree + "\n" +
        "[" + context + ".NEXT]: " + next;
  }

}
