package aiml.script;

import aiml.classifier.MatchState;

public class UppercaseElement extends NonEmptyElement {

  public String evaluate(MatchState m) {
    return Formatter.upperCase(content.evaluate(m));
  }

  public String toString() {
    return "uppercase(" + content + ")";
  }

}
