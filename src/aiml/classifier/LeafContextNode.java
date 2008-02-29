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

package aiml.classifier;

import java.util.ListIterator;

import aiml.classifier.node.PatternNode;

/**
 * This context encapsulates the data associated with each path.
 * 
 * Todo: Rename to LeafContextNode or TemplateContextNode, create superclass
 * ContextNode
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public class LeafContextNode extends ContextNode {
  private Object result;

  /**
   * Create a new leaf context, and associate an object with it.
   * 
   * @param o
   *                the stored object
   */
  public LeafContextNode(Object o) {
    context = Integer.MAX_VALUE;
    result = o;
  }

  /**
   * Set's the result in the matchstate, and returns true.
   * 
   * @param match
   *                the match state, used to store the resulting object
   * @return <code>true</code>
   */
  public boolean match(MatchState match) {
    match.setResult(result);
    return true;
  }

  /**
   * Adds a pattern to this context.
   * 
   * <i>Note:</i> This method will be reworked once generalized context types
   * are implemented (i.e. it will be replaced by an addGeneralContextType()
   * method.
   * 
   * @param pattern
   *                Pattern
   */
  public PatternNode addPattern(Path.Pattern pattern) {
    throw new UnsupportedOperationException(
        "Can't add a pattern to a leaf context");
  }

  /**
   * Add the path to itself. If there are no patterns in this path to add, throw
   * a DuplicatePathException.
   * 
   * @param path
   *                the path
   * @param o
   *                the object
   * @throws DuplicatePathException
   * @return the resulting context tree, with all modifications applied and the
   *         correct ordering
   */
  public ContextNode add(ListIterator path, Object o)
      throws DuplicatePathException {
    if (!path.hasNext()) {
      throw new DuplicatePathException();
    }
    return super.add(path, o);
  }

  public String toString() {
    return "<LEAF>" + result;
  }
}
