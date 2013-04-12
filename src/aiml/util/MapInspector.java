/*
    jaiml - java AIML library
    Copyright (C) 2009  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package aiml.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a special class that bypasses access restrictions of the
 * java.util.HashMap class to get at some useful statistics, such as the actual
 * size of the array used to back the hashtable.
 * 
 * @author Kim Sullivan
 * 
 */
public class MapInspector {
  /** The HashMap.table package-private field */
  private static Field tableField;
  static {
    try {
      tableField = HashMap.class.getDeclaredField("table");
      tableField.setAccessible(true);
    } catch (Exception e) {
      tableField = null;
    }
  }

  private static Field hiddenField;
  static {
    try {
      Class<?> nodeMaster = Class.forName(
          "org.aitools.programd.graph.NonOptimalNodemaster", true,
          MapInspector.class.getClassLoader());
      hiddenField = nodeMaster.getDeclaredField("hidden");
      hiddenField.setAccessible(true);
    } catch (Exception e) {
      hiddenField = null;
    }
  }

  /**
   * Returns an estimation of overhead of a map. The overhead is the absolute
   * difference between the number of items the map holds and the number of
   * items it has allocated space for.
   * 
   * @param map
   *          a map
   * @return the overhead
   */
  public static int getOverhead(Map<?, ?> map) {
    if (map == null)
      return 0;
    if (map instanceof HashMap<?, ?>) {
      return getAllocatedSize((HashMap<?, ?>) map) - map.size();
    }
    if ("org.aitools.programd.graph.NonOptimalNodemaster".equals(map.getClass().getName())) {
      Map<?, ?> aMap = getHiddenMap(map);
      return getOverhead(aMap);
    }
    if ("org.aitools.programd.graph.OneOptimalNodemaster".equals(map.getClass().getName())) {
      if (map.size() <= 1) {
        return 2 - map.size();
      }
      Map<?, ?> aMap = getHiddenMap(map);
      return getOverhead(aMap) + 1;
    }
    if ("org.aitools.programd.graph.TwoOptimalNodemaster".equals(map.getClass().getName())) {
      if (map.size() <= 2) {
        return 3 - map.size();
      }
      Map<?, ?> aMap = getHiddenMap(map);
      return getOverhead(aMap) + 2;
    }
    if ("org.aitools.programd.graph.ThreeOptimalNodemaster".equals(map.getClass().getName())) {
      if (map.size() <= 3) {
        return 4 - map.size();
      }
      Map<?, ?> aMap = getHiddenMap(map);
      return getOverhead(aMap) + 3;
    }

    return 0;
  }

  private static Map<?, ?> getHiddenMap(Map<?, ?> map) {
    Map<?, ?> aMap;
    try {
      aMap = (Map<?, ?>) hiddenField.get(map);
    } catch (Exception e) {
      aMap = null;
    }
    return aMap;
  }

  /**
   * Get the actual number of buckets allocated for the hashtable
   * 
   * @param hashMap
   * @return
   */
  public static int getAllocatedSize(HashMap<?, ?> hashMap) {
    try {
      return ((Object[]) tableField.get(hashMap)).length;
    } catch (Exception e) {
      return hashMap.size(); //if all else fails, this is the minumum size the hashmap must have allocated
    }
  }
}
