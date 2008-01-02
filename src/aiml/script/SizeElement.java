package aiml.script;

import aiml.classifier.Classifier;
import aiml.classifier.MatchState;

public class SizeElement extends EmptyElement {

  public String evaluate(MatchState m) {
    return String.valueOf(Classifier.getCount());
  }

  public String toString() {
    return "size()";
  }

}
