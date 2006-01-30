package aiml.script;

import aiml.classifier.MatchState;

public class SizeElement extends EmptyElement {

  public String evaluate(MatchState m) {
    return "size()";
  }

  public String execute(MatchState m, int depth) {
    return Formatter.tab(depth) + "print(size());";
  }

  public String toString() {
    return "size()";
  }

}
