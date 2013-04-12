/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.aitools.programd.graph;

import java.util.Map;

/**
 * A <code>Nodemapper</code> maps the branches in a {@link Graphmaster
 * Graphmaster} structure.
 * 
 * @author Richard Wallace
 * @author <a href="mailto:noel@aitools.org">Noel Bush</a>
 */
public interface Nodemapper<V> extends Map<String, V> {
  /**
   * Removes a node from the <code>Nodemapper</code>.
   * 
   * @param value
   *          the value to remove
   */
  public void removeValue(Object value);

  /**
   * Sets the parent of the <code>Nodemapper</code>
   * 
   * @param parent
   *          the parent of the <code>Nodemapper</code>
   */
  public void setParent(Nodemapper<V> parent);

  /**
   * Returns the parent of the <code>Nodemapper</code>
   * 
   * @return the parent of the <code>Nodemapper</code>
   */
  public Nodemapper<V> getParent();

  /**
   * Returns the height of the <code>Nodemapper</code>. The height is the
   * minimum number of words needed to reach a leaf node from here.
   * 
   * @return the height of the <code>Nodemapper</code>
   */
  public int getHeight();

  /**
   * Sets the height of this <code>Nodemapper</code> to &quot;top&quot;, i.e.
   * <code>0</code> (zero), causing each ancestor <code>n</code> to have a
   * minimum height of <code>n</code>, unless the ancestor is the root node. Not
   * sure if this is correct.
   */
  public void setTop();

  /**
   * Returns a weighted average of the sizes of this Nodemapper and its
   * children. The average is &quot;weighted&quot; by giving this Nodemapper's
   * size and the average size of its children equal weight. If this Nodemapper
   * does not have a parent (i.e., is the root), then its size is excluded from
   * the calculation.
   * 
   * @return the sizes of this Nodemapper and all its children.
   */
  public double getAverageSize();
}