package aiml.context.data;

import aiml.environment.Environment;

public class StringSource implements DataSource<String> {

  private String value;

  public StringSource() {
    this("");
  }

  public StringSource(String value) {
    this.value = value;
  }

  @Override
  public String getValue(Environment e) {
    return value;
  }
}
