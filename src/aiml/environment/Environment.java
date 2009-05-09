/*
    jaiml - java AIML library
    Copyright (C) 2004, 2009  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package aiml.environment;

import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import aiml.bot.Bot;
import aiml.classifier.MatchState;

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
  /**
   * Contains a list of previous bot responses, split to sentences, in reverse
   * order.
   */
  private LinkedList<LinkedList<String>> responseHistory = new LinkedList<LinkedList<String>>();
  public static final String UNDEFINED_VARIABLE = "";

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

  public boolean isSetVar(String name) {
    return variables.containsKey(name);
  }

  public String getInput(int i, int j) {
    if (i == 1 && j == 1) {
      return getInput();
    }
    return "";
  }

  /*"fake" method, the environment doesn't support input history */
  public String getInput(int i) {
    if (i == 1) {
      return getInput();
    }
    return "";
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

  /**
   * Add a whole bot response to the history
   * 
   * @param response
   *          the response of the bot
   */
  public void addBotResponse(String response) {
    LinkedList<String> sentences = new LinkedList<String>();
    for (String sentence : bot.getSentenceSplitter().split(response)) {
      sentences.addFirst(sentence);
    }
    responseHistory.addFirst(sentences);
  }

  /**
   * Retrieve a sentence from conversation history. The <code>interaction</code>
   * specifies which interaction (counting backwards, 1 being the most recent
   * response) and <code>sentence</code> the sentence from that interaction
   * (also counting backwards, 1 being the most recent).
   * 
   * @param interaction
   * @param sentence
   * @return
   */
  public String getBotResponse(int interaction, int sentence) {
    if (interaction < 1 || sentence < 1) {
      throw new InvalidParameterException(
          "Interaction and sentences must be positive integers");
    }
    if (responseHistory.size() >= interaction &&
        responseHistory.get(interaction).size() >= sentence) {
      return responseHistory.get(interaction - 1).get(sentence - 1);
    }
    return "";
  }

  /**
   * Retrieve all sentences of a particular interaction. The
   * <code>interaction</code> specifies which interaction (counting backwards, 1
   * being the most recent response).
   * 
   * @param interaction
   * @return
   */
  public String getBotResponse(int interaction) {
    if (interaction < 1) {
      throw new InvalidParameterException(
          "Interaction must be a positive integers");
    }
    if (responseHistory.size() >= interaction) {
      StringBuilder result = new StringBuilder();
      LinkedList<String> sentenceList = responseHistory.get(interaction - 1);
      for (Iterator<String> iterator = sentenceList.descendingIterator(); iterator.hasNext();) {
        String sentence = iterator.next();
        result.append(sentence);

      }
      return result.toString();
    }
    return "";
  }

  public String getVersion() {
    return "0.1.0";
  }

  /**
   * @return
   */
  public MatchState match() {
    return getBot().getClassifier().match(this);
  }

}
