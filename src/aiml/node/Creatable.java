package aiml.node;

/**
 * <p>Implementing this interface means that a node can be created as a base node,
 * in it's most basic, un-optimized state. Examples of this are a StringNode or
 * a WildcardNode. All other node types are created while splitting/branching.
 * </p>
 * <p><i>Note to self:</i> Find a better name</p>
 * @author Kim Sullivan
 * @version 1.0
 */
public interface Creatable {
  /**
   * Returns true if this node type can handle the current pattern at position depth.
   * @param depth the depth
   * @param pattern the pattern
   * @return <code>true</code> if this node can handle the pattern; <code>false</code> otherwise
   */
  boolean canCreate(int depth, String pattern);

  /**
   * Returns an empty instance of this PatternNode.
   * @return PatternNode
   */
  PatternNode getInstance();
}
