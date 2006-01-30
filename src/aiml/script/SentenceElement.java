package aiml.script;

import aiml.classifier.MatchState;

public class SentenceElement extends NonEmptyElement {

  public String evaluate(MatchState m) {
    return "sentence("+content.evaluate(m)+")";
  }

  public String execute(MatchState m, int depth) {
    return Formatter.tab(depth) + "print(sentence("+content.evaluate(m)+"))";
  }
  
  public String toString() {
    return "sentence("+content+")";
  }

}
