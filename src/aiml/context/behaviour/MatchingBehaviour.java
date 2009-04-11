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

import aiml.classifier.Classifier;
import aiml.classifier.ContextNode;
import aiml.classifier.PaternSequence.PatternIterator;

/**
 * Describes a matching behaviour of a context.
 * 
 * @author Kim Sullivan
 * 
 */
public interface MatchingBehaviour {
  
  
  /**
   * Returns a new ContextNode specific to this Context type.
   * 
   * @param classifier
   *          the classifier where the node is to be added
   * @param patterns
   *          a pattern sequence
   * @param o
   *          the object to add to the leaf node
   * @param next
   *          the next context node to try if this newly created context fails
   *          to match (if present, otherwise may be <code>null</code>)
   * @return a new ContextNode instance
   */
  public ContextNode createClassifierNode(Classifier classifier,
      PatternIterator patterns, ContextNode next, Object o);

}
