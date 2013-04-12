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
package aiml.context.behaviour;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import aiml.classifier.Classifier;
import aiml.classifier.ContextNode;
import aiml.classifier.PatternContextNode;
import aiml.classifier.PatternSequence;
import aiml.classifier.node.PatternNodeFactory;

/**
 * <p>
 * The standard AIML matching behaviour. This class returns a PatternContextNode
 * instance when asked to provide a classifier node. It also holds a reference
 * to a PatternNodeFactory that knows how to create basic PatternNode instances.
 * </p>
 * 
 * <p>
 * This class also provides a way to customize the underlying map that is used
 * for PatternNodes.
 * </p>
 * 
 * @author Kim Sullivan
 * 
 */
public class PatternBehaviour implements MatchingBehaviour {
  protected PatternNodeFactory pnf;
  protected Map<?, ?> mapPrototype;

  /**
   * Acces Object.clone() via reflection
   */
  private static Method clone;
  static {
    try {
      clone = Object.class.getDeclaredMethod("clone");
      clone.setAccessible(true);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static class Holder {
    static PatternBehaviour defaultPatternBehaviour = new CompactPatternBehaviour();
  }

  /**
   * Returns an instance with the default node handler classes registered in its
   * pattern node factory.
   * 
   * @see aiml.classifier.node
   * 
   * @return the default pattern behaviour
   */
  public static PatternBehaviour getDefaultBehaviour() {
    return Holder.defaultPatternBehaviour;
  }

  /**
   * Override the default pattern behaviour.
   * 
   * @param behaviour
   *          the new default behaviour
   */
  public static void setDefaultBehaviour(PatternBehaviour behaviour) {
    Holder.defaultPatternBehaviour = behaviour;
  }

  /**
   * Create a clone of a class implementing the Map interface.
   * 
   * @param map
   * @return
   */
  private Map<?, ?> cloneMap(Map<?, ?> map) {
    try {
      return (Map<?, ?>) clone.invoke(map);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Create a new behaviour using a custom prototype.
   * 
   * @param map
   */
  protected PatternBehaviour(Map<?, ?> map) {
    if (!(map instanceof Cloneable)) {
      throw new IllegalArgumentException("The map prototype must be cloneable");
    }
    if (map.size() != 0) {
      throw new IllegalArgumentException("The map prototype must be empty");
    }
    mapPrototype = cloneMap(map);
  }

  /**
   * Create a new bejaviour using a default map prototype.
   */
  protected PatternBehaviour() {
    this(new HashMap<Object, Object>());
  }

  public PatternBehaviour(PatternNodeFactory pnf) {
    this();
    assert (pnf.getCount() > 0) : "You have to register node types";
    this.pnf = pnf;
  }

  public PatternNodeFactory getPNF() {
    return pnf;
  }

  /**
   * Create return a new instance of a map using this behaviours prototype.
   * 
   * @return
   */
  public Map<?, ?> newMap() {
    return cloneMap(mapPrototype);
  }

  @Override
  public PatternContextNode createClassifierNode(Classifier classifier,
      PatternSequence.PatternIterator patterns, ContextNode next, Object o) {
    return new PatternContextNode(classifier, patterns, next, o);
  }

  @Override
  public boolean hasDefaultPattern() {
    return false;
  }

  @Override
  public String getDefaultPattern() {
    return null;
  }

}
