package aiml.script;

import aiml.classifier.MatchState;

public class FormalElement extends NonEmptyElement {

  public String evaluate(MatchState m) {
    return Formatter.formal(content.evaluate(m));
  }

  public String toString() {
    return "formal(" + content + ")";
  }

}
