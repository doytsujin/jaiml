package aiml.node;

import java.util.*;
import aiml.*;

/**
 * A single character branch in the strings portion of the pattern tree. All by
 * themselves, StringBranchNodes implementan uncompressed trie. To make
 * storage effective, compressed string nodes have to be implemented.
 * @author Kim Sullivan
 * @version 1.0
 */

public class StringBranchNode
    extends PatternNode {

  /**
   * a map to store the branches
   */
  private HashMap map = new HashMap();

  /**
   * Create a new empty string branch node. The type is PatternNode.STRING
   */
  public StringBranchNode() {
    type = PatternNode.STRING;
  }

  public StringBranchNode(StringNode node) {
    type = PatternNode.STRING;
    char c = node.getPattern().charAt(0); //this is safe, because a stringnode always represents at least 1 character
    Character key = new Character(c);
    map.put(key, node.removePrefix(1));

  }

  /**
   * Add the pattern to itself. Since this already represents a branch node, no
   * optimizations/splitting are preformed, except when adding an end of a string, or a
   * wildcard.
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

    char c = Character.toUpperCase(pattern.charAt(depth));
    Character key = new Character(c);

    PatternNode node = (PatternNode) map.get(key);
    depth++;
    if (node == null) {
      node = PatternNodeFactory.getInstance(depth, pattern);
    }
    result = node.add(depth, pattern);
    map.put(key, result.root);
    result.root = this;
    return result;

  }

  public boolean match(MatchState match) {
    //Match
    char c;
    try {
      c = Character.toUpperCase(match.getContextValue().charAt(match.depth));
    }
    catch (StringIndexOutOfBoundsException e) {
      return false; //the current context is an empty string
    }
    PatternNode node = (PatternNode) map.get(new Character(c));
    //match is "done" check result:
    if (node != null) {
      match.depth++;
      if (node.match(match)) {
        return true;
      }
      else {
        //restore the previous match state
        match.depth--;
        return false;
      }
    }
    else {
      return false;
    }
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
        return new StringBranchNode();
      }

    });
  }

}
