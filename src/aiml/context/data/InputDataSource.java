package aiml.context.data;

import java.util.EmptyStackException;

import aiml.environment.Environment;

public interface InputDataSource<T> extends DataSource<T> {
  /**
   * Pushes a new input on the stack.
   * 
   * @param input
   *          The value of the input.
   */
  public void push(String input, Environment e);

  /**
   * Pops a value from the stack.
   * 
   * @throws EmptyStackException
   *           - if the stack is empty
   */
  public void pop(Environment e) throws EmptyStackException;
}
