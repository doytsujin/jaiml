package aiml.context.data;

import aiml.environment.Environment;

public class EnvironmentInputSource implements DataSource<String> {

  @Override
  public String getValue(Environment e) {
    return e.getInput();
  }

}
