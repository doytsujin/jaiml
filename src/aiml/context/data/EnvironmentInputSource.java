package aiml.context.data;

import java.util.EmptyStackException;

import aiml.environment.Environment;

public class EnvironmentInputSource implements InputDataSource<String> {

  @Override
  public String getValue(Environment e) {
    return e.getInput();
  }

  @Override
  public void pop(Environment e) throws EmptyStackException {
    e.popInput();
  }

  @Override
  public void push(String input, Environment e) {
    e.pushInput(input);
  }

}
