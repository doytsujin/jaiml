/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.aitools.programd.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This is an optimization of {@link NodemapperFactory} that avoids creating the
 * internal {@link java.util.Map Map} until the number of mappings exceeds three
 * (3).
 * 
 * @author <a href="mailto:noel@aitools.org">Noel Bush</a>
 * @version 4.6
 */
public class ThreeOptimalNodemaster<V> extends NonOptimalNodemaster<V> {
  public ThreeOptimalNodemaster(Class<? extends Map<String, V>> mapClass) {
    super(mapClass);
  }

  public ThreeOptimalNodemaster() {
    this((Class<? extends Map<String, V>>) HashMap.class);
  }

  protected int size = 0;

  protected String key_0;
  protected String key_1;
  protected String key_2;

  protected V value_0;
  protected V value_1;
  protected V value_2;

  /**
   * Puts the given object into the Nodemaster, associated with the given key.
   * 
   * @param keyToUse
   *          the key to use
   * @param valueToPut
   *          the value to put
   * @return the same object that was put into the Nodemaster
   */
  @Override
  public V put(String keyToUse, V valueToPut) {
    V result;
    if (this.size == 0) {
      this.key_0 = keyToUse.toUpperCase().intern();
      if (valueToPut instanceof String) {
        this.value_0 = (V) ((String) valueToPut).intern();
      } else {
        this.value_0 = valueToPut;
      }
      this.size = 1;
      return this.value_0;
    } else if (this.size == 1) {

      if (this.key_0.equals(keyToUse.toUpperCase())) {
        if (valueToPut instanceof String) {
          this.value_0 = (V) ((String) valueToPut).intern();
        } else {
          this.value_0 = valueToPut;
        }
        return value_0;
      }

      this.key_1 = keyToUse.toUpperCase().intern();
      if (valueToPut instanceof String) {
        this.value_1 = (V) ((String) valueToPut).intern();
      } else {
        this.value_1 = valueToPut;
      }
      this.size = 2;
      return this.value_1;

    } else if (this.size == 2) {
      if (this.key_0.equals(keyToUse.toUpperCase())) {
        if (valueToPut instanceof String) {
          this.value_0 = (V) ((String) valueToPut).intern();
        } else {
          this.value_0 = valueToPut;
        }
        return value_0;
      }
      if (this.key_1.equals(keyToUse.toUpperCase())) {
        if (valueToPut instanceof String) {
          this.value_1 = (V) ((String) valueToPut).intern();
        } else {
          this.value_1 = valueToPut;
        }
        return value_1;
      }

      this.key_2 = keyToUse.toUpperCase().intern();
      if (valueToPut instanceof String) {
        this.value_2 = (V) ((String) valueToPut).intern();
      } else {
        this.value_2 = valueToPut;
      }
      this.size = 3;
      return this.value_2;

    } else if (this.size == 3) {
      if (this.key_0.equals(keyToUse.toUpperCase())) {
        if (valueToPut instanceof String) {
          this.value_0 = (V) ((String) valueToPut).intern();
        } else {
          this.value_0 = valueToPut;
        }
        return value_0;
      }
      if (this.key_1.equals(keyToUse.toUpperCase())) {
        if (valueToPut instanceof String) {
          this.value_1 = (V) ((String) valueToPut).intern();
        } else {
          this.value_1 = valueToPut;
        }
        return value_1;
      }
      if (this.key_2.equals(keyToUse.toUpperCase())) {
        if (valueToPut instanceof String) {
          this.value_2 = (V) ((String) valueToPut).intern();
        } else {
          this.value_2 = valueToPut;
        }
        return value_2;
      }

      newMap();
      this.hidden.put(this.key_0, this.value_0);
      this.hidden.put(this.key_1, this.value_1);
      this.hidden.put(this.key_2, this.value_2);
      this.key_0 = null;
      this.key_1 = null;
      this.key_2 = null;
      this.value_0 = null;
      this.value_1 = null;
      this.value_2 = null;
      this.size = 4;
    }

    if (valueToPut instanceof String) {
      valueToPut = (V) ((String) valueToPut).intern();
    }
    result = this.hidden.put(keyToUse.toUpperCase().intern(), valueToPut);
    size = this.hidden.size();
    return result;

  }

  /**
   * Removes the given object from the Nodemaster.
   * 
   * @param valueToRemove
   *          the object to remove
   */
  public void removeValue(Object valueToRemove) {
    if (this.size == 3 || this.size == 2 || this.size == 1) {
      // ugly but optimal (see above)
      if (valueToRemove.equals(this.value_0)) {
        this.value_0 = null;
        this.key_0 = null;
      } else if (valueToRemove.equals(this.value_1)) {
        this.value_1 = null;
        this.key_1 = null;
      } else if (valueToRemove.equals(this.value_2)) {
        this.value_2 = null;
        this.key_2 = null;
      } else {
        // We didn't find a key.
        Logger.getLogger("programd.graphmaster").severe(
            String.format(
                "Key was not found for value when trying to remove \"%s\".",
                valueToRemove));
        return;
      }
      this.size--;
    } else if (this.size > 3) {
      // Find the key for this value.
      Object keyToRemove = null;
      for (Map.Entry<String, V> item : this.hidden.entrySet()) {
        if (item.getValue().equals(valueToRemove)) {
          // Found it.
          keyToRemove = item.getKey();
          break;
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
      if (this.size > 4) {
        // Remove the value from the HashMap (ignore the primary
        // value/key pair).
        this.hidden.remove(keyToRemove);
        this.size--;
      }
      // otherwise it is exactly 4...
      else {
        // Remove this item from the HashMap.
        this.hidden.remove(keyToRemove);
        // Set the last three items in the HashMap to be the primary value/key
        // pairs for this Nodemapper.
        this.key_2 = this.hidden.keySet().iterator().next();
        this.value_2 = this.hidden.remove(this.key_1);
        this.key_1 = this.hidden.keySet().iterator().next();
        this.value_1 = this.hidden.remove(this.key_1);
        this.key_0 = this.hidden.keySet().iterator().next();
        this.value_0 = this.hidden.remove(this.key_0);
        // Remove the empty HashMap to save space.
        this.hidden = null;
        this.size = 3;
      }
    } else if (this.size == 0) {
      // We didn't find a key.
      Logger.getLogger("programd.graphmaster").severe(
          String.format("No keys in Nodemapper when trying to remove \"%s\".",
              valueToRemove));
    }
  }

  /**
   * Gets the object associated with the specified key.
   * 
   * @param keyToGet
   *          the key to use
   * @return the object associated with the given key
   */
  public V get(Object keyToGet) {
    if (this.size == 0 || !(keyToGet instanceof String)) {
      return null;
    }

    String stringKey = (String) keyToGet;
    if (this.size == 3 || this.size == 2 || this.size == 1) {
      if (stringKey.equalsIgnoreCase(this.key_0)) {
        return this.value_0;
      }
      if (stringKey.equalsIgnoreCase(this.key_1)) {
        return this.value_1;
      }
      if (stringKey.equalsIgnoreCase(this.key_2)) {
        return this.value_2;
      }
      // (otherwise...)
      return null;
    } else {
      return this.hidden.get(stringKey.toUpperCase());
    }
  }

  /**
   * @return the keyset of the Nodemaster
   */
  public Set<String> keySet() {
    if (this.size < 4) {
      Set<String> result = new HashSet<String>();
      if (this.key_0 != null) {
        result.add(this.key_0);
      }
      if (this.key_1 != null) {
        result.add(this.key_1);
      }
      if (this.key_2 != null) {
        result.add(this.key_2);
      }
      return result;
    }
    // (otherwise...)
    return this.hidden.keySet();
  }

  @Override
  public Set<java.util.Map.Entry<String, V>> entrySet() {
    if (this.size == 0) {
      return Collections.emptySet();
    }
    if (this.size <= 3) {
      Set<java.util.Map.Entry<String, V>> result = new HashSet<Entry<String, V>>();

      result.add(new Entry<String, V>() {

        @Override
        public String getKey() {
          return ThreeOptimalNodemaster.this.key_0;
        }

        @Override
        public V getValue() {
          return ThreeOptimalNodemaster.this.value_0;
        }

        @Override
        public V setValue(V value) {
          V oldValue = ThreeOptimalNodemaster.this.value_0;
          ThreeOptimalNodemaster.this.value_0 = value;
          return oldValue;
        }

      });
      if (size >= 2) {
        result.add(new Entry<String, V>() {

          @Override
          public String getKey() {
            return ThreeOptimalNodemaster.this.key_1;
          }

          @Override
          public V getValue() {
            return ThreeOptimalNodemaster.this.value_1;
          }

          @Override
          public V setValue(V value) {
            V oldValue = ThreeOptimalNodemaster.this.value_1;
            ThreeOptimalNodemaster.this.value_1 = value;
            return oldValue;
          }

        });

      }
      if (size >= 3) {
        result.add(new Entry<String, V>() {

          @Override
          public String getKey() {
            return ThreeOptimalNodemaster.this.key_2;
          }

          @Override
          public V getValue() {
            return ThreeOptimalNodemaster.this.value_2;
          }

          @Override
          public V setValue(V value) {
            V oldValue = ThreeOptimalNodemaster.this.value_2;
            ThreeOptimalNodemaster.this.value_2 = value;
            return oldValue;
          }

        });

      }
      return result;
    }
    return super.entrySet();
  }

  /**
   * @param keyToCheck
   *          the key to check
   * @return whether or not the Nodemaster contains the given key
   */
  public boolean containsKey(Object keyToCheck) {
    if (this.size == 0 || !(keyToCheck instanceof String)) {
      return false;
    }
    String stringKey = (String) keyToCheck;
    if (this.size == 3 || this.size == 2 || this.size == 1) {
      return (stringKey.equalsIgnoreCase(this.key_0) ||
          stringKey.equalsIgnoreCase(this.key_1) || stringKey.equalsIgnoreCase(this.key_2));
    }
    return this.hidden.containsKey(stringKey.toUpperCase());
  }

  /**
   * @return the size of the Nodemaster
   */
  public int size() {
    return this.size;
  }

  public double getAverageSize() {
    double total = 0d;
    if (this.size < 4) {
      if (this.value_0 != null && this.value_0 instanceof Nodemapper) {
        total += ((Nodemapper) this.value_0).getAverageSize();
      }
      if (this.value_1 != null && this.value_1 instanceof Nodemapper) {
        total += ((Nodemapper) this.value_1).getAverageSize();
      }
      if (this.value_2 != null && this.value_2 instanceof Nodemapper) {
        total += ((Nodemapper) this.value_2).getAverageSize();
      }
    } else {
      for (Object object : this.hidden.values()) {
        if (object instanceof Nodemapper) {
          total += ((Nodemapper) object).getAverageSize();
        }
      }
    }
    if (this.parent != null) {
      return (this.size + (total / this.size)) / 2d;
    }
    // otherwise...
    return total / this.size;
  }
}