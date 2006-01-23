/**
 * <p>Provides classes of pattern tree nodes. Except for explicitly registering
 * node types so they can be used in the matching process, the end user should
 * never create instances of nodes, all node creation is handled by the adding
 * mechanism.</p>
 * <p>Many nodes implement a static <code>register()</code> method that registers
 * them in the <code>PatternNodeFactory</code> factory class. You may note that
 * this method is not part of the base class, neither that it belongs to any kind
 * of interface. This is done in purpose, because many nodes are conflicting in
 * nature, and should theybe used together the matching system may behave in
 * unpredictable ways. This way, the programmer is forced to hand-pick and
 * register the basic nodes he wants to use.</p>
 */
package aiml.classifier.node;