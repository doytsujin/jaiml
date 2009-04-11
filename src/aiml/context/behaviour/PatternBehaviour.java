package aiml.context.behaviour;

import aiml.classifier.Classifier;
import aiml.classifier.ContextNode;
import aiml.classifier.PaternSequence;
import aiml.classifier.PatternContextNode;

public class PatternBehaviour implements MatchingBehaviour {
  @Override
  public ContextNode createClassifierNode(Classifier classifier,
      PaternSequence.PatternIterator patterns, ContextNode next, Object o) {
    return new PatternContextNode(classifier, patterns, next, o);
  }

}
