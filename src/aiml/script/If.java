package aiml.script;

import aiml.classifier.MatchState;

/**
 * This class handles a simple AIML condition:
 * 
 * <pre>
 *   &lt;condition name=&quot;variable&quot;&gt;evaluate this if true&lt;/condition&gt;
 * </pre>
 * 
 * @author Kim Sullivan
 * 
 */
public class If extends SimpleScriptElement {

  private String name;
  private String value;

  If(String name, String value) {
    super();
    this.name = name;
    this.value = value;
  }

  If(String name, String value, Script content) {
    this(name, value);
    this.content = content;
  }

  public String evaluate(MatchState m) {
    if (m.getEnvironment().getVar(name).equalsIgnoreCase(value)) {
      return content.evaluate(m);
    } else {
      return "";
    }
  }

  public String toString() {
    return "(($" + name + "==\"" + value + "\") ? " + content + ": \"\")";

  }

}
