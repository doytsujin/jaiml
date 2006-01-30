package aiml.script;

import aiml.classifier.MatchState;

public class DateElement extends EmptyElement {

  public String evaluate(MatchState m) {
    return "date()";
  }

  public String execute(MatchState m, int depth) {
    return Formatter.tab(depth) + "print(date());";
  }

  public String toString() {
    return "date()";
  }

}
