package aiml.script;

import aiml.classifier.MatchState;

public class LowercaseElement extends NonEmptyElement {

  public String evaluate(MatchState m) {
    return Formatter.lowerCase(content.evaluate(m));
  }

  public String toString() {
    return "lowercase(" + content + ")";
  }

}
