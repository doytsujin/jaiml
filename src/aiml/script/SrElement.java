package aiml.script;

import aiml.classifier.MatchState;
import aiml.context.ContextInfo;

public class SrElement extends EmptyElement {

  public String evaluate(MatchState m) {
    return "srai(star[input,1])";
  }

  public String execute(MatchState m) {
    return "print(srai(star[input,1]))";
  }

  public String toString() {
    return "srai(star[input,1])";
  }

}
