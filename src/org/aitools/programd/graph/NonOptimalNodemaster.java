/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.aitools.programd.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This is a basic Nodemaster containing all the things that are common to the
 * various optimization strategies.
 * 
 * @author <a href="mailto:noel@aitools.org">Noel Bush</a>
 * @version 4.6
 */
public class NonOptimalNodemaster<V> implements Nodemapper<V>, Cloneable {
  /**
   * The hidden hashmap where some (or all) mappings may be stored, depending
   * upon optimization strategies.
   */
  protected Map<String, V> hidden;

  /**
   * The minimum number of words needed to reach a leaf node from here. Defaults
   * to zero.
   */
  protected int height = 0;

  /** The parent of this Nodemaster. */
  protected Nodemapper<V> parent;

  private final Class<? extends Map<String, V>> mapClass;

  public NonOptimalNodemaster(Class<? extends Map<String, V>> mapClass) {
    this.mapClass = mapClass;
  }

  public NonOptimalNodemaster() {
    this((Class<? extends Map<String, V>>) HashMap.class);
  }

  /**
   * Sets the parent of the Nodemaster.
   * 
   * @param parentToSet
   *          the parent to set
   */
  public void setParent(Nodemapper parentToSet) {
    this.parent = parentToSet;
  }

  /**
   * @return the parent of the Nodemaster
   */
  public Nodemapper getParent() {
    return this.parent;
  }

  /**
   * @return the height of the Nodemaster
   */
  public int getHeight() {
    return this.height;
  }

  /**
   * Sets the Nodemaster as being at the top.
   */
  public void setTop() {
    this.fillInHeight(0);
  }

  /**
   * Sets the <code>height</code> of this <code>Nodemaster</code> to
   * <code>height</code>, and calls <code>fillInHeight()</code> on its parent
   * (if not null) with a height <code>height + 1</code>.
   * 
   * @param heightToFillIn
   *          the height for this node
   */
  protected void fillInHeight(int heightToFillIn) {
    if (this.height > heightToFillIn) {
      this.height = heightToFillIn;
    }
    if (this.parent != null) {
      ((NonOptimalNodemaster) this.parent).fillInHeight(heightToFillIn + 1);
    }
  }

  @Override
  public void removeValue(Object valueToRemove) {
    // Find the key for this value.
    Object keyToRemove = null;
    if (this.hidden != null) {
      for (Map.Entry<String, V> item : this.hidden.entrySet()) {
        if (item.getValue().equals(valueToRemove)) {
          // Found it.
          keyToRemove = item.getKey();
          break;
        }
      }
    }
    if (keyToRemove == null) {
      // We didn't find a key.
      Logger.getLogger("programd.graphmaster").severe(
          String.format(
              "Key was not found for value when trying to remove \"%s\".",
              valueToRemove));
      return;
    }
    // Remove the value from the HashMap (ignore the primary
    // value/key pair).
    this.hidden.remove(keyToRemove);
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsKey(Object key) {
    if (this.hidden == null || !(key instanceof String)) {
      return false;
    }
    return this.hidden.containsKey(((String) key).toUpperCase());
  }

  @Override
  public boolean containsValue(Object value) {
    if (this.hidden == null || !(value instanceof String)) {
      return false;
    }
    return this.hidden.containsKey(((String) value).toUpperCase());
  }

  @Override
  public Set<java.util.Map.Entry<String, V>> entrySet() {
    if (this.hidden == null) {
      return Collections.emptySet();
    }
    return this.hidden.entrySet();
  }

  @Override
  public boolean isEmpty() {
    return (this.hidden == null) || (this.hidden.isEmpty());
  }

  @Override
  public Set<String> keySet() {
    if (this.hidden == null) {
      return null;
    }
    return this.hidden.keySet();
  }

  @Override
  public void putAll(Map<? extends String, ? extends V> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V remove(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<V> values() {
    throw new UnsupportedOperationException();
  }

  @Override
  public V put(String keyToUse, V valueToPut) {
    if (this.hidden == null) {
      newMap();
    }
    if (valueToPut instanceof String) {
      return this.hidden.put(keyToUse.toUpperCase().intern(),
          (V) ((String) valueToPut).intern());
    }
    // otherwise...
    return this.hidden.put(keyToUse.toUpperCase().intern(), valueToPut);
  }

  private Map<String, V> createMap() {
    try {
      return mapClass.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected void newMap() {
    this.hidden = createMap();
  }

  @Override
  public V get(Object keyToGet) {
    if (this.hidden == null || !(keyToGet instanceof String)) {
      return null;
    }
    return this.hidden.get(((String) keyToGet).toUpperCase());
  }

  @Override
  public int size() {
    if (this.hidden == null) {
      return 0;
    }
    return this.hidden.size();
  }

  public double getAverageSize() {
    double total = 0d;
    if (this.hidden != null) {
      for (Object object : this.hidden.values()) {
        if (object instanceof Nodemapper) {
          total += ((Nodemapper) object).getAverageSize();
        }
      }
    }
    if (this.parent != null) {
      int size = this.hidden.size();
      return (size + (total / size)) / 2d;
    }
    // otherwise...
    return total / this.hidden.size();
  }

  @Override
  public NonOptimalNodemaster<V> clone() throws CloneNotSupportedException {
    if (size() != 0) {
      throw new CloneNotSupportedException("Can't clone a non-empty Nodemaster");
    }
    NonOptimalNodemaster<V> clone = (NonOptimalNodemaster<V>) super.clone();
    if (clone.hidden != null) {
      clone.hidden = createMap();
    }
    return clone;
  }
}