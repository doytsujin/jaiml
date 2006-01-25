package aiml.bot;

import java.util.HashMap;

public class Bot {
  private String name;
  private HashMap<String,String> constants = new HashMap<String,String>();
  public Bot(String name) {
    super();
    this.name=name;
  }
  public void setConstant(String name,String value) {
    constants.put(name,value);
  }
  
  public String getConstant(String name) throws InvalidConstantException {
    if (constants.containsKey(name))
      return constants.get(name);
    else throw new InvalidConstantException("Bot constant '" + name + "' must be defined");
  }
  
  public boolean hasConstant(String name) {
    return constants.containsKey(name);
  }
  
  public String getName() {
    return name;
  }
  
}
