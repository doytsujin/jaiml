package aiml.script;

import aiml.classifier.MatchState;

public class DateElement extends EmptyElement {

  public String evaluate(MatchState m) {
    return "date()";
  }

  public String toString() {
    return "date()";
  }

}
