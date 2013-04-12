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

import java.util.Map;

import org.aitools.programd.graph.OneOptimalNodemaster;

import aiml.classifier.node.AIMLNode;
import aiml.classifier.node.PatternNodeFactory;

/**
 * This behaviour mimics the original graphmaster algorithm, in that it doesn't
 * allow skipping of contexts and matching is word-based.
 * 
 * @author Kim Sullivan
 * 
 */
public class AIMLPatternBehaviour extends PatternBehaviour {

  /**
   * Create a new AIMLPatternBehaviour using a custom class for internal node
   * storage.
   * 
   * @param mapPrototype
   *          a cloneable empty map
   */
  public AIMLPatternBehaviour(Map<?, ?> mapPrototype) {
    super(mapPrototype);
    pnf = new PatternNodeFactory();
    AIMLNode.register(pnf);
  }

  /**
   * Create a new AIMLPAtternBehaviour using the default internal storage.
   */
  public AIMLPatternBehaviour() {
    this(new OneOptimalNodemaster());
  }

  @Override
  public boolean hasDefaultPattern() {
    return true;
  }

  @Override
  public String getDefaultPattern() {
    return "*";
  }
}
