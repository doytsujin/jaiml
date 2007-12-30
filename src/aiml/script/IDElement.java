package aiml.script;

import aiml.classifier.MatchState;

public class IDElement extends EmptyElement {

  public String evaluate(MatchState m) {
    return m.getEnvironment().getUserID().toString();
  }

  public String toString() {
    return "userID()";
  }

}
