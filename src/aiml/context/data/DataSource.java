package aiml.context.data;

import aiml.environment.Environment;

public interface DataSource<T> {
  public T getValue(Environment e);
}
