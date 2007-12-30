package aiml.script;

import aiml.classifier.MatchState;

public class DateElement extends EmptyElement {

  public String evaluate(MatchState m) {
    return m.getEnvironment().getDate();
  }

  public String toString() {
    return "date()";
  }

}
