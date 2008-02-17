package aiml.script;

import java.util.logging.Logger;

import aiml.classifier.Classifier;
import aiml.classifier.MatchState;

public class SraiElement extends SimpleScriptElement {

  public String evaluate(MatchState m) {

    String newInput = content.evaluate(m);
    m.getEnvironment().pushInput(newInput);
    MatchState result = Classifier.match(m.getEnvironment());
    m.getEnvironment().popInput();
    if (result != null) {
      return ((aiml.script.Script) result.getResult()).evaluate(result);
    } else {
      Logger.getLogger(this.getClass().getName()).warning(
          "No match found for input \"" + newInput + "\"");
      return "";
    }

  }

  public String toString() {
    return "srai(" + content + ")";
  }
}
