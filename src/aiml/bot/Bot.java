package aiml.bot;

import java.util.HashMap;

public class Bot {
  private String name;
  private HashMap<String,String> properties = new HashMap<String,String>();
  public Bot(String name) {
    super();
    this.name=name;
  }
  public void setProperty(String name,String value) {
    properties.put(name,value);
  }
  
  public String getProperty(String name) throws InvalidPropertyException {
    if (properties.containsKey(name))
      return properties.get(name);
    else throw new InvalidPropertyException("Bot property '" + name + "' must be defined for bot "+this.name);
  }
  
  public boolean hasProperty(String name) {
    return properties.containsKey(name);
  }
  
  public String getName() {
    return name;
  }
  
}
