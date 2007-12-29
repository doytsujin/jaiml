package aiml.script;

import aiml.classifier.MatchState;

public class ThatElement extends MultiIndexedElement {

  public String evaluate(MatchState m) {
    return "$_that[" + i1 + "," + i2 + "]";
  }

  public String execute(MatchState m, int depth) {
    return Formatter.tab(depth) + "print($_that[" + i1 + "," + i2 + "]);";
  }

  public String toString() {
    return "$_that[" + i1 + "," + i2 + "]";
  }

}
