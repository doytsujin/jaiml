package aiml.script;

import aiml.classifier.MatchState;

public class SrElement extends EmptyElement {

  public String evaluate(MatchState m) {
    return "srai(star[input,1])";
  }

  public String execute(MatchState m, int depth) {
    return Formatter.tab(depth) + "print(srai(star[input,1]))";
  }

  public String toString() {
    return "srai(star[input,1])";
  }

}
