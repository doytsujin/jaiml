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

package aiml.environment;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import aiml.bot.Bot;

/**
 * <p>
 * This class represents the current execution environment of an AIML program.
 * Basically, it knows how to get at variable data - but with respect to the
 * currently running bot or user (if they are supported).
 * </p>
 * 
 * <p>
 * This class will also probably hold information about the local context, and
 * handle persistence. For now, it only supports a stack for input.
 * </p>
 * 
 * <p>
 * Originally, this class was supposed to be called Context (because it roughly
 * corresponds to a context of a thread). The term "context" is already used in
 * AIML in a slightly different (but overlapping) sense, and this would cause
 * confusion in the code.
 * </p>
 * 
 * @author Kim Sullivan
 * 
 */
public class Environment {
  private static final String USER_ID = "GlobalUser";
  private Bot bot;
  private Map<String, String> variables = new HashMap<String, String>();
  private LinkedList<String> input = new LinkedList<String>();

  public Environment(Bot bot) {
    super();
    this.bot = bot;
  }

  public String getVar(String name) {
    String result = variables.get(name);
    if (result != null) {
      return result;
    }
    return "";
  }

  public void setVar(String name, String value) {
    variables.put(name, value);
  }

  public String getInput() {
    return input.getFirst();
  }

  public void pushInput(String text) {
    input.addFirst(text);
  }

  public String popInput() {
    return input.removeFirst();
  }

  public String getUserID() {
    return USER_ID;
  }

  public String getDate() {
    return DateFormat.getDateInstance().format(new Date());
  }

  public Bot getBot() {
    return bot;
  }

}
