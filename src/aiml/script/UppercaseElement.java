package aiml.script;

import aiml.classifier.MatchState;

public class UppercaseElement extends NonEmptyElement {

  public String evaluate(MatchState m) {
    return "uppercase(" + content.evaluate(m) + ")";
  }

  public String execute(MatchState m, int depth) {
    return Formatter.tab(depth) + "print(uppercase(" + content.evaluate(m) +
        "))";
  }

  public String toString() {
    return "uppercase(" + content + ")";
  }

}
