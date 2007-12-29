package aiml.script;

import aiml.classifier.MatchState;

public class LowercaseElement extends NonEmptyElement {

  public String evaluate(MatchState m) {
    return "lowercase(" + content.evaluate(m) + ")";
  }

  public String execute(MatchState m, int depth) {
    return Formatter.tab(depth) + "print(lowercase(" + content.evaluate(m) +
        "))";
  }

  public String toString() {
    return "lowercase(" + content + ")";
  }

}
