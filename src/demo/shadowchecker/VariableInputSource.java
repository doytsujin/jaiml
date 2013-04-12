package demo.shadowchecker;

import java.util.EmptyStackException;

import aiml.context.data.InputDataSource;
import aiml.context.data.VariableSource;
import aiml.environment.Environment;

public class VariableInputSource extends VariableSource implements
    InputDataSource<String> {

  public VariableInputSource(String name) {
    super(name);
  }

  @Override
  public void pop(Environment e) throws EmptyStackException {
    //don't do anything
  }

  @Override
  public void push(String input, Environment e) {
    //don't do anything    
  }

}
