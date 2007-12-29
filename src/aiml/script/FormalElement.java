package aiml.script;

import aiml.classifier.MatchState;

public class FormalElement extends NonEmptyElement {

  public String evaluate(MatchState m) {
    return "formal(" + content.evaluate(m) + ")";
  }

  public String execute(MatchState m, int depth) {
    return Formatter.tab(depth) + "print(formal(" + content.evaluate(m) + "))";
  }

  public String toString() {
    return "formal(" + content + ")";
  }

}
