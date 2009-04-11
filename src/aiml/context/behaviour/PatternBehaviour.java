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
import aiml.classifier.PaternSequence;
import aiml.classifier.PatternContextNode;
import aiml.classifier.node.PatternNodeFactory;

/**
 * The standard AIML matching behaviour. This class returns a PatternContextNode
 * instance when asked to provide a classifier node.
 * 
 * @author Kim Sullivan
 * 
 */
public class PatternBehaviour implements MatchingBehaviour {
  private PatternNodeFactory pnf;
  
  public PatternBehaviour(PatternNodeFactory pnf) {
    this.pnf = pnf;
  }
  
  public PatternNodeFactory getPNF() {
    return pnf;
  }
  
  @Override
  public PatternContextNode createClassifierNode(Classifier classifier,
      PaternSequence.PatternIterator patterns, ContextNode next, Object o) {
    return new PatternContextNode(classifier, patterns, next, o);
  }

}
