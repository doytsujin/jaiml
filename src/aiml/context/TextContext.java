package aiml.context;

import java.awt.TextComponent;

/**
 * This this context type takes it's data from a java.awt.TextComponent
 * @author Kim Sullivan
 * @version 1.0
 */

public class TextContext
    extends Context {
  /**
   * The text component that serves as a source of data
   */
  private TextComponent source;

  /**
   * Create a new named context, and set it's data source to a text component.
   * @param name the name of the context
   * @param source the data source
   */
  public TextContext(String name, TextComponent source) {
    super(name);
    this.source = source;
  }

  /**
   * Get the value from the associated text component's text property.
   * @return the value
   */
  public String getValue() {
    return source.getText();
  }

}
