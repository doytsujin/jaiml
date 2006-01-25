package aiml.bot;

import java.util.HashMap;

public class Bots {
  
  private static HashMap<String,Bot> bots=new HashMap<String,Bot>();
  
  private Bots() {
    super();
    // TODO Auto-generated constructor stub
  }
  
  public static void addBot(Bot bot) throws DuplicateBotException {
    if (bots.containsKey(bot.getName())) {
      throw new DuplicateBotException("Bot 'bot.getName()' already exists");
    }
    bots.put(bot.getName(),bot);
  }
  
  public static Bot getBot(String name) throws InvalidBotException {
    if (!bots.containsKey(name))
      throw new InvalidBotException("Bot 'name' does not exist");
    return bots.get(name);
  }
  
  public static boolean hasBot(String name) {
    return bots.containsKey(name);
  }
  
  public static int getCount() {
    return bots.size();
  }
  
  public static String getConst(String botname,String name) throws InvalidConstantException, InvalidBotException {
    return getBot(botname).getConstant(name);
  }
  
  public static boolean hasConstant(String botname,String name) throws InvalidBotException {
    return getBot(botname).hasConstant(name);
  }
  
  public static void setConstant(String botname,String name, String value) throws InvalidBotException {
    getBot(botname).setConstant(name, value);
  }
  
}
