package aiml.node;

import java.util.*;
import aiml.*;

/**
 * <p>This very simple implementation of a Pattern node is backed by a map where
 * all the patterns are stored. It does not perform any path compression or
 * wildcard matching (apart from being case-insensitive).</p>
 * <p>It consumes the whole rest of the pattern, and so
 * has to be used in conjunction with a "terminal" node like EndOfStringNode.</p>
 * @author Kim Sullivan
 * @version 1.0
 */

public class HashMapNode
    extends PatternNode {
  private HashMap map = new HashMap();

  /**
   * Creates a new hash map pattern node
   */
  public HashMapNode() {
  }

  /**
   * Adds the rest of the pattern to a hashmap.
   * @param depth int
   * @param pattern String
   * @return AddResult
   */
  public AddResult add(int depth, String pattern) {
    AddResult result;
    pattern = pattern.substring(depth).toUpperCase();

    PatternNode node = (PatternNode) map.get(pattern);
    depth += pattern.length();
    if (node == null) {
      node = PatternNodeFactory.getInstance(depth, pattern);
    }
    result = node.add(depth, pattern);
    map.put(pattern, result.root);

    result.root = this;
    return result;

  }

  /**
   * Matches the current context value starting at the depth specified in the match state.
   * This method preforms a simple map lookup to determine a match, no wildcards are processed.
   * @param match the match state
   * @return  <code>true</code> if the match was successful; <code>false</code> if not
   */
  public boolean match(MatchState match) {
    //Match
    String s = match.getContextValue().substring(match.depth).toUpperCase();
    PatternNode node = (PatternNode) map.get(s);
    //match is "done" check result:
    if (node != null) {
      match.depth += s.length();
      if (node.match(match)) {
        return true;
      }
      else {
        //restore the previous match state
        match.depth -= s.length();
        return false;
      }
    }
    else {
      return false;
    }
  }

  public String toString() {
    Iterator i = map.entrySet().iterator();
    StringBuffer result = new StringBuffer();
    while (i.hasNext()) {
      Map.Entry me = (Map.Entry) i.next();
      PatternNode subnode = (PatternNode) me.getValue();
      result.append("[" + me.getKey() + "]" + subnode + "\n");

    }
    return result.toString();
  }

  /**
   * Register this node type in PatternNodeFactory.
   */
  public static void register() {
    PatternNodeFactory.registerNode(new Creatable() {
      public boolean canCreate(int depth, String pattern) {
        return (depth != pattern.length());
      }

      public PatternNode getInstance() {
        return new HashMapNode();
      }

    });
  }
}
