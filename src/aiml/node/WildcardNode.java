package aiml.node;

import aiml.*;

/**
 * <p>A wildcard node in the pattern tree. This node reluctantly matches one or
 * more characters from the current context, until a match is found or a match
 * fails.</p
 *
 * <p>The current implementation optimizes only trailing wildcards (wildcards
 * that have no child pattern nodes).</p>
 *
 * <p> Another very powerful optimization utilizes look-ahead, where the set of
 * characters that follow the wildcard is used to skip larger portions of the
 * input. This works only when there are no wildcards in the follow set. Look
 * ahead is currently not implemented.</p>
 * @author Kim Sullivan
 * @version 1.0
 */

public class WildcardNode
    extends PatternNode {
  /**
   * Sub patterns
   */
  private PatternNode next;

  /**
   * Create a new empty wildcard of the specified type. This constructor is
   * private, the only way to create a new wildcard node is via the
   * <code>PatternNodeFactory</code> class.
   * @param type the wildcard type
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
      if (subContext != null) {
        if (subContext.match(match)) {
          return true;
        }
        else {
          match.removeWildcard();
          return false;

        }
      } //if (subContext != null)
      else { //subContext==null
        match.depth -= w.getLength();
        match.removeWildcard();
        return false;
      }
    } // if (next!=null)
    else { //this wildcard is "trailing" - add the rest of the input to it
      if (subContext != null) {
        w.growRest();
        if (subContext.match(match)) {
          return true;
        }
        else {
          match.depth -= w.getLength();
          match.removeWildcard();
          return false;
        }

      }
      else {
        match.depth -= w.getLength();
        match.removeWildcard();
        return false;
      }
    }

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

}
