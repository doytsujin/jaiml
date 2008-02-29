/*
    jaiml - java AIML library
    Copyright (C) 2004-2008  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package aiml.bot;

import java.util.HashMap;

public class Bots {

  private static HashMap<String, Bot> bots = new HashMap<String, Bot>();

  private Bots() {
    super();
    // TODO Auto-generated constructor stub
  }

  public static void addBot(Bot bot) throws DuplicateBotException {
    if (bots.containsKey(bot.getName())) {
      throw new DuplicateBotException("Bot 'bot.getName()' already exists");
    }
    bots.put(bot.getName(), bot);
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

  public static String getConst(String botname, String name)
      throws InvalidPropertyException, InvalidBotException {
    return getBot(botname).getProperty(name);
  }

  public static boolean hasConstant(String botname, String name)
      throws InvalidBotException {
    return getBot(botname).hasProperty(name);
  }

  public static void setConstant(String botname, String name, String value)
      throws InvalidBotException {
    getBot(botname).setProperty(name, value);
  }

}
