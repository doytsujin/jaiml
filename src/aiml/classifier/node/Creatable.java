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

package aiml.classifier.node;

/**
 * <p>
 * Implementing this interface means that a node can be created as a base node,
 * in it's most basic, un-optimized state. Examples of this are a StringNode or
 * a WildcardNode. All other node types are created while splitting/branching.
 * </p>
 * <p>
 * <i>Note to self:</i> Find a better name
 * </p>
 * 
 * @author Kim Sullivan
 * @version 1.0
 */
public interface Creatable {
  /**
   * Returns true if this node type can handle the current pattern at position
   * depth.
   * 
   * @param depth
   *                the depth
   * @param pattern
   *                the pattern
   * @return <code>true</code> if this node can handle the pattern;
   *         <code>false</code> otherwise
   */
  boolean canCreate(int depth, String pattern);

  /**
   * Returns an empty instance of this PatternNode.
   * 
   * @return PatternNode
   */
  PatternNode getInstance();
}
