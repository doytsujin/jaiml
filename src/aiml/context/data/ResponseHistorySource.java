package aiml.context.data;

import java.security.InvalidParameterException;

import aiml.environment.Environment;

public class ResponseHistorySource implements DataSource<String> {

  private int interaction;
  private int sentence;

  public ResponseHistorySource() {
    this(0, 0);
  }

  public ResponseHistorySource(int interaction, int sentence) {
    if (interaction < 0 || sentence < 0) {
      throw new InvalidParameterException(
          "Interaction and sentences must be positive integers");
    }
    this.interaction = interaction;
    this.sentence = sentence;
  }

  @Override
  public String getValue(Environment e) {
    return e.getBot().preprocessInput(e.getBotResponse(interaction, sentence)).get(
        0);
  }

}
