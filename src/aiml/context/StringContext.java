package aiml.context;

/**
 * A simple context type, associates a string with the context's value.
 * @author Kim Sullivan
 * @version 1.0
 */

public class StringContext
    extends Context {
  private String value;

  /**
   * Constructs a new String context with an empty string as its value.
   * @param name The name of the context
   */
  public StringContext(String name) {
    super(name);
    this.value = "";
  }

  /**
   * Constructs a new String context with a value.
   * @param name The name of the context
   * @param value The value of the context
   */
  public StringContext(String name, String value) {
    super(name);
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  /**
   * Set the value associated with this string context
   * @param value The value of the context
   */
  public void setValue(String value) {
    this.value = value;
  }

}
