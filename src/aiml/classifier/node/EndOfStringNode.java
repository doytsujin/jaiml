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

import aiml.classifier.MatchState;

/**
 * <p>
 * The "End of string" node matches an end of a string. It can be used in
 * special circumstances, for example when a previous branch node consumed the
 * rest of the context value.
 * </p>
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public class EndOfStringNode extends PatternNode {
  PatternNode next;

  /** Create a new end of string node */
  public EndOfStringNode() {
    type = PatternNode.STRING;
  }

  /**
   * Create a new end of string node, with a subtree. In this case, the EOS node
   * works like a break in longer sequences of patterns, or works like a
   * terminator after a consuming branch.
   * 
   * @param next
   *                the subtree
   */
  public EndOfStringNode(PatternNode next) {
    this.next = next;
    type = PatternNode.STRING;
  }

  /**
   * Adds the pattern to itself.
   * 
   * @param depth
   *                the current depth
   * @param pattern
   *                the pattern to add
   * @return AddResult
   */
  public AddResult add(int depth, String pattern) {
    if (depth == pattern.length()) {
      //OK, we can "add" the end of the string;
      //System.out.println("AddEOS");
      return new AddResult(this, this, depth);

    } else {
      //System.out.println("AddEOS.next");
      if (next == null) {
        next = PatternNodeFactory.getInstance(depth, pattern);
      }
      AddResult result = next.add(depth, pattern);
      next = result.root;
      result.root = this;
      return result;
    }
  }

  /**
   * Matches the current context value starting at the depth specified in the
   * match state.
   * 
   * @param match
   *                MatchState
   * @return boolean
   */
  public boolean match(MatchState match) {
    if (match.depth == match.getContextValue().length()) {
      //we have a winner, at least for now
      return subContext.match(match);
    } else {
      if (next != null) {
        return next.match(match);
      } else {
        return false;
      }
    }
  }

  public String toString() {
    return "[EOS]" + super.toString() +
        (next != null ? "[EOS.NEXT]" + next.toString() : "");
  }

  /**
   * Register this node type in PatternNodeFactory.
   */
  public static void register() {
    PatternNodeFactory.registerNode(new Creatable() {
      public boolean canCreate(int depth, String pattern) {
        return (depth == pattern.length());
      }

      public PatternNode getInstance() {
        return new EndOfStringNode();
      }

    });
  }

}
