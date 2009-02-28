/*
    jaiml - java AIML library
    Copyright (C) 2004, 2009  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package aiml.classifier.node;

import java.util.ArrayList;

import aiml.classifier.ContextNode;

/**
 * A factory class for pattern nodes. A call to <code>getInstance()</code> will
 * return an empty instance of a particular node type, able to handle the
 * pattern at it's current depth. Node classes have to register themselves using
 * the <code>registerNode()</code> method and the <code>Creatable</code>
 * interface.
 * 
 * @author Kim Sullivan
 * @version 1.0
 */
public class PatternNodeFactory {
  /**
   * A dynamic array of known node types. More precisely, a dynamic array of
   * Creatable instances that know how to create a specific node type
   */
  private ArrayList<Creatable> nodeTypes = new ArrayList<Creatable>();

  /**
   * Do not create instances of PatternNodeFactory
   */
  public PatternNodeFactory() {
  }

  /**
   * Register a node type via the Creatable interface.
   * 
   * @param nodeType
   *          an implementation of the Creatable interface
   */
  public void registerNode(Creatable nodeType) {
    nodeTypes.add(nodeType);
  }

  /**
   * Get the number of registered node types
   * 
   * @return the number of registered node types
   */
  public int getCount() {
    return nodeTypes.size();
  }

  /**
   * Return an instance of a node class most appropriate for the pattern.
   * 
   * @param parentContext
   *          TODO
   * @param depth
   *          the depth
   * @param pattern
   *          the pattern
   * 
   * @return an appropriate instance of PatternNode
   */
  public PatternNode getInstance(ContextNode parentContext, int depth,
      String pattern) {
    for (Creatable nodeType : nodeTypes) {
      if (nodeType.canCreate(depth, pattern)) {
        return nodeType.getInstance(parentContext);
      }
    }
    return null;
  }

}
