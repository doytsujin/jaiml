package aiml.script;

import aiml.classifier.MatchState;

public class VersionElement extends EmptyElement {

  public String evaluate(MatchState m) {
    return "$_version";
  }

  public String execute(MatchState m) {
    return "print($_version)";
  }

  public String toString() {
    return "$_version";
  }

}
