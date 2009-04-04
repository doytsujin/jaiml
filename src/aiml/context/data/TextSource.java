package aiml.context.data;

import java.awt.TextComponent;

import aiml.environment.Environment;

/**
 * @author Kim Sullivan
 * 
 */
public class TextSource implements DataSource<String> {
  private TextComponent source;

  public TextSource(TextComponent source) {
    this.source = source;
  }

  @Override
  public String getValue(Environment e) {
    return source.getText();
  }

}
