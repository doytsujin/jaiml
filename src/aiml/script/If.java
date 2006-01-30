package aiml.script;

import aiml.classifier.MatchState;

public class If extends SimpleScriptElement {
  
  private String name;
  private String value;
  
  If(String name, String value) {
    super();
    this.name=name;
    this.value=value;
  }
  
  If(String name, String value, Script content) {
    this (name,value);
    this.content = content;
  }
  
  public String evaluate(MatchState m) {
    return "(($" + name + "==\"" + value + "\") ? " + content.evaluate(m) + ": \"\")";
  }

  public String execute(MatchState m, int depth) {
    return Formatter.tab(depth) + "if ($" + name + "==" + value + ") {\n" +
           content.execute(m, depth+1) +
           Formatter.tab(depth) + "\n}\n";
  }
  
  public String toString() {
    return "(($" + name + "==\"" + value + "\") ? " + content + ": \"\")";
    
  }
  
}
