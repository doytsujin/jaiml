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

package aiml.node;

import aiml.*;

/**
 * A single string node. This represents a "compressed" part of the tree, with
 * no branches, in an attempt to speed up the matching process.
 * @author Kim Sullivan
 * @version 1.0
 */

public class StringNode
    extends PatternNode {

  /**
   * the next node in the tree
   */
  private PatternNode next;

  /**
   * The pattern this node represents
   */
  private String s;

  /**
   * Create a new, empty string node.
   */
  public StringNode() {
    type = PatternNode.STRING;
  }

  /**
   * Create a string node with the specified pattern and subtree. Contrary to
   * most other methods, this actually uses the whole pattern, not a part of the
   * pattern starting at depth.
   * @param pattern the pattern
   * @param next the subtree
   */
  public StringNode(String pattern, PatternNode next) {
    type = PatternNode.STRING;
    s = pattern;
    this.next = next;
  }

  /**
   * Add the pattern to itself. Preform any necessary tree splitting.
   * @param depth int
   * @param pattern String
   * @return AddResult
   */
  public AddResult add(int depth, String pattern) {
    AddResult result;
    if (depth == pattern.length()) {
      PatternNode node = new EndOfStringNode(this);
      result = node.add(depth, pattern);
      return result;
    }
    if (Pattern.isWildcard(depth, pattern)) {
      PatternNode node = new BranchNode(this);
      result = node.add(depth, pattern);
      return result;
    }

    //we know we're adding a string, not a wildcard

    //now determine the string we should be adding... damn, i NEED a tokenizer for the pattern...
    //one that also supports splits for the string...
    //no time to finish it though :-(
    int nextw = Pattern.nextWildcard(depth, pattern);
    String thissegment;
    if (nextw == -1) {
      thissegment = pattern.substring(depth).toUpperCase();
    }
    else {
      thissegment = pattern.substring(depth, nextw).toUpperCase();

    }

    int plength;
    if (s == null) { //could it be we're just initializing this node for the first time?
      s = thissegment;
      plength = s.length();
      /*now I could properly finish initialization, but this would only
       duplicate the code from the next case, so let's combine them.*/
    }
    else { //so, we have this segment to add, now check prefix length
      plength = Pattern.prefixLength(s, thissegment);
    }

    if (plength == s.length()) { //the most simple case, no splitting necessary, just add the rest.
      depth += plength;
      if (depth == pattern.length()) { //even better, we're practically done. there are no remaining characters in the pattern
        result = new AddResult(this, this, depth);
      }
      else { //we still have to add the rest of the pattern
        if (next == null) {
          next = PatternNodeFactory.getInstance(depth, pattern);
        }
        result = next.add(depth, pattern);
        next = result.root;
        result.root = this;
      }
      return result;
    } //(plength==s.length())

    if (plength == 0) { //another relatively simple case - no common prefix, substitute a string branch node
      PatternNode node = new StringBranchNode(this);
      result = node.add(depth, pattern);
      return result;
    }

    //assert(plength>0&&plength<s.length())
    //split prefix
    depth += plength;
    PatternNode node = new StringNode(thissegment.substring(0, plength),
                                      this.removePrefix(plength));
    if (depth == pattern.length()) { //the new node is the final one, don't create unnecessary EOS nodes...
      result = new AddResult(node, node, depth);
    }
    else {

      result = node.add(depth - plength, pattern);

    }
    return result;

  }

  public boolean match(MatchState match) {
    String cValue = match.getContextValue().toUpperCase();
    int plength = Pattern.prefixLength(cValue.substring(match.depth), s);
    //String thissegment = cValue.substring(match.depth, match.depth + s.length());
    if (plength == s.length()) { //match, so try it
      match.depth += plength;
      if (match.depth == cValue.length()) { //this context value is done, match subcontext
        if (subContext != null && subContext.match(match)) {
          return true;
        }
        else { //subcontext match fail
          match.depth -= plength;
          return false;
        }
      }
      else { //this context value is not done, match subtrees
        if (next != null && next.match(match)) {
          return true;
        }
        else {
          match.depth -= plength;
          return false;

        }
      }
    }
    else { //no match in the first place
      return false;
    }
  }

  /**
   * Returns the pattern this node represents. Actually, this might be replaced
   * by something like <code>Set getFirst()</code> in the future, because that's
   * what it is used for (and, for further optimizations of wildcard matching,
   * it will more closely resemble the FIRST-FOLLOW functions used in computer
   * linguistics.
   *
   * @return the pattern
   */
  public String getPattern() {
    return s;
  }

  /**
   * Removes the prefix of the specified length and returns the new modified node.
   * If, after removing the prefix nothing remains, changes the implementation
   * to EndOfStringNode.
   * @param length int
   * @return PatternNode
   */
  public PatternNode removePrefix(int length) {
    if (length > s.length()) {
      throw new IllegalArgumentException(
          "Can' remove prefix longer than this pattern!");
    }
    s = s.substring(length);
    if (s.length() > 0) {
      return this;
    }
    EndOfStringNode node = new EndOfStringNode(next);
    node.subContext = subContext;
    return node;
  }

  /**
   * Register this node type in PatternNodeFactory.
   */
  public static void register() {
    PatternNodeFactory.registerNode(new Creatable() {
      public boolean canCreate(int depth, String pattern) {

        return (depth != pattern.length()
                && pattern.length() > 0
                && !Pattern.isWildcard(depth, pattern));
      }

      public PatternNode getInstance() {
        return new StringNode();
      }

    });
  }

}
