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

package aiml.classifier;

import aiml.classifier.PatternSequence.PatternIterator;
import aiml.classifier.node.PatternNode;
import aiml.context.behaviour.AIMLPatternBehaviour;

/**
 * <p>
 * This context node implements the original AIML matching algorithm, by
 * combining all contexts into a single large pattern. This means that this
 * context node in fact represents a whole sequence of contexts.
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> While this class correctly creates a a matching tree where all
 * patterns are munged together, due to the way context values are handled, it
 * is currently not possible to match patterns using a tree created using this
 * class
 * </p>
 * 
 * @author Kim Sullivan
 * 
 */
public class AIMLPatternContextNode extends PatternContextNode {

  public AIMLPatternContextNode(Classifier classifier,
      PatternIterator patterns, ContextNode next, Object o) {
    super(classifier, patterns, next, o);
  }

  @Override
  public ContextNode add(PatternIterator patterns, Object o)
      throws DuplicatePathException {
    if (patterns.hasNext()) { //We're in the middle of the sequence

      PatternSequence.Pattern pattern = patterns.peek();
      if (context.compareTo(pattern.getContext()) < 0) {
        //add as next
        if (next == null) {
          next = pattern.getContext().createClassifierNode(classifier,
              patterns, null, o);
        } else {
          next = next.add(patterns, o);
        }
        return this;
      } else if (context.compareTo(pattern.getContext()) > 0) {
        //add instead of self
        return pattern.getContext().createClassifierNode(classifier, patterns,
            this, o);
      } else {
        //add the pattern into the current tree.
        StringBuilder combinedPatternString = new StringBuilder();
        combinedPatternString.append(pattern.getPattern());
        patterns.next();
        while (patterns.hasNext() &&
            patterns.peek().getContext().getBehaviour() instanceof AIMLPatternBehaviour) {
          pattern = patterns.peek();
          combinedPatternString.append(" <");
          combinedPatternString.append(pattern.getContext().getName());
          combinedPatternString.append("> ");
          combinedPatternString.append(pattern.getPattern());
          patterns.next();
        }
        PatternNode leaf = addPattern(new PatternSequence.Pattern(context,
            combinedPatternString.toString()));
        leaf.addContext(patterns, o);
        return this;
      }
    } else { //we're at the end of the sequence, so we can add a Leaf node
      if (next == null) {
        next = new LeafContextNode(classifier, o);
      } else {
        next = next.add(patterns, o);
      }
      return this;
    }
  }
}
