package aiml.script;

import java.io.IOException;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.bot.Bot;
import aiml.bot.InvalidPropertyException;
import aiml.classifier.MatchState;
import aiml.parser.AimlParserException;
import aiml.parser.AimlSyntaxException;

public class BotElement extends EmptyElement {
  private String name;

  public Script parse(XmlPullParser parser) throws XmlPullParserException,
      IOException, AimlParserException {
    name = parser.getAttributeValue(null, "name");
    if (name == null)
      throw new AimlSyntaxException(
          "Syntax error: mandatory attribute 'name' missing from element '" +
              parser.getName() + "' " + parser.getPositionDescription());
    return super.parse(parser);
  }

  public String evaluate(MatchState m) {
    try {
      return m.getEnvironment().getBot().getProperty(name);
    } catch (InvalidPropertyException e) {
      Logger.getLogger(BotElement.class.getName()).warning(
          "bot element referencing unknown property " + name);
      return Bot.UNKNOWN_PROPERTY;
    }
  }

  public String toString() {
    return "$_bot['" + name + "']";
  }

}
