package aiml.script;

import aiml.classifier.MatchState;

public class SraiElement extends SimpleScriptElement {

  public String evaluate(MatchState m) {
    return "srai(" + content.evaluate(m) + ")";
  }

  public String execute(MatchState m) {
    return "print(srai(" + content.evaluate(m) + "));";
  }
  
  public String toString(){
    return "srai(" + content + ")";
  }
}
