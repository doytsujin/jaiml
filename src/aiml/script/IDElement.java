package aiml.script;

import aiml.classifier.MatchState;

public class IDElement extends EmptyElement {

  public String evaluate(MatchState m) {
    return "userID()";
  }

  public String execute(MatchState m, int depth) {
    return Formatter.tab(depth) + "print(userID());";
  }

  public String toString() {
    return "userID()";
  }

}
