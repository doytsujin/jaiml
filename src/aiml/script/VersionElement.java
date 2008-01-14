package aiml.script;

import aiml.classifier.MatchState;

public class VersionElement extends EmptyElement {

  public String evaluate(MatchState m) {
    return m.getEnvironment().getVersion();
  }

  public String toString() {
    return "$_version";
  }

}
