package aiml.node;

import aiml.*;

/**
 * A branch in the pattern matching tree. This pure branching node implements the
 * "pattern order" principle of AIML - first it triest to match an underscore,
 * then it tires to find an exact match and finally it tries to get a match on
 * the star wildcard. As with all pattern nodes, the branch node applies only
 * to the current depth.
 * @author Kim Sullivan
 * @version 1.0
 */

public class BranchNode
    extends PatternNode {
  /**
   * The star (*) wildcard subtree
   */
  private PatternNode star;

  /**
   * The exact string match subtree
   */
  private PatternNode string;

  /**
   * The underscore (_) wildcard subtree
   */
  private PatternNode underscore;

  /**
   * Creates a new, empty branch node
   */
  public BranchNode() {
  }

  /**
   * Creates a new branch node, with one subtree (wildcard or string). The type
   * of the subtree (star, string, underscore) is determined at run-time.
   * @param node the first subtree
   */
  public BranchNode(PatternNode node) {
    switch (node.getType()) {
      case PatternNode.UNDERSCORE:
        underscore = node;
        break;
      case PatternNode.STAR:
        star = node;
        break;
      case PatternNode.STRING:
        string = node;
        break;
      default:
        throw new UnsupportedOperationException(
            "Can't add unknown node types to a branch");
    }
  }

  /**
   * Add the pattern to itself. For the branch node, this means it must
   * determine the current pattern type, and place it into the appropriate
   * subtree.
   */
  public PatternNode.AddResult add(int depth, String pattern) {
    AddResult result;
    //this is a really ugly method... should I put the subnodes in an Array? is it OK to use
    //static final ints as hardcoded indexes?
    int t = Pattern.getType(depth, pattern);
    if (t == PatternNode.OTHER) {
      throw new UnsupportedOperationException(
          "Can't add nodes of type OTHER to a branch");
    }
    if (t == PatternNode.UNDERSCORE) {
      if (underscore == null) {
        underscore = PatternNodeFactory.getInstance(depth, pattern);
      }
      result = underscore.add(depth, pattern);
      underscore = result.root;
      result.root = this;
      return result;
    }
    if (t == PatternNode.STAR) {
      if (star == null) {
        star = PatternNodeFactory.getInstance(depth, pattern);
      }
      result = star.add(depth, pattern);
      star = result.root;
      result.root = this;
      return result;
    }
    if (t == PatternNode.STRING) {
      if (string == null) {
        string = PatternNodeFactory.getInstance(depth, pattern);
      }
      result = string.add(depth, pattern);
      string = result.root;
      result.root = this;
      return result;
    }
    throw new UnsupportedOperationException(
        "Can't add unknown node types to a branch");
  }

  /**
   * Try to match the current match state. This node implements the AIML pattern
   * ordering principle. First it tries to match the state to an underscore
   * wildcard pattern, then an exact string, and finally a star wildcard.
   */
  public boolean match(MatchState match) {
    if (underscore != null) {
      if (underscore.match(match)) {
        return true;
      }
    }
    if (string != null) {
      if (string.match(match)) {
        return true;
      }
    }
    if (star != null) {
      if (star.match(match)) {
        return true;
      }
    }
    return false;
  }

}
