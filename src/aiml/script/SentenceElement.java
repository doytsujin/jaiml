package aiml.script;

import aiml.classifier.MatchState;

public class SentenceElement extends NonEmptyElement {

  public String evaluate(MatchState m) {
    return Formatter.sentence(content.evaluate(m));
  }

  public String toString() {
    return "sentence(" + content + ")";
  }

}
