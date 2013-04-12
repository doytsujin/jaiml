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

package demo;

import graphviz.Graphviz;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParserException;

import aiml.bot.Bot;
import aiml.bot.BotSyntaxException;
import aiml.classifier.Classifier;
import aiml.classifier.MatchState;
import aiml.environment.Environment;
import aiml.parser.AimlParserException;
import aiml.script.Formatter;
import aiml.script.Script;

public class InterpreterDemo {
  private static final Runtime runtime = Runtime.getRuntime();

  private static void memStat() {
    System.out.print(runtime.maxMemory());
    System.out.print(" max, ");

    System.out.print(runtime.totalMemory());
    System.out.print(" total, ");

    System.out.print(runtime.freeMemory());
    System.out.print(" free, ");

    System.out.print(runtime.totalMemory() - runtime.freeMemory());
    System.out.print(" used");

    System.out.println();
  }

  public static void main(String[] args) throws XmlPullParserException,
      IOException, BotSyntaxException, AimlParserException {
    Logger.getLogger("aiml").setLevel(Level.WARNING);
    System.out.println("Demonstration program for the java aiml interpreter library");
    System.out.println("(C) Kim Sullivan, 2006, 2009");
    if (args.length == 0) {
      System.err.println("Error: you must specify a command line argument with the bot to load!");
      return;
    } else {
      File f = new File(args[0]);
      if (!f.exists()) {
        System.err.println("Error: file not found : " + f);
        return;
      }
    }

    Classifier classifier = new Classifier();

    Bot b = new Bot(classifier);
    System.out.println("Loading bot...");
    // b.setProperty("name","Really Complicated");
    b.load(args[0]);
    System.out.println("done, loaded " + classifier.getCount() +
        " categories, " + classifier.getNodeStats());
    memStat();
    System.out.println("Enter text to match, or /exit to quit");

    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    System.out.print("> ");
    String line = in.readLine();
    Environment e = b.createEnvironment();
    while (line != null && !line.equals("/exit")) {
      if (line.equals("/gv")) {
        System.out.println(classifier.gvGraph(new Graphviz()));
      } else if (line.equals("/stats")) {
        System.out.println("Classifier statistics: " +
            classifier.getNodeStats());
      } else {
        for (String input : b.preprocessInput(line)) {
          e.pushInput(input);
          MatchState<Script> m = e.match();
          System.out.println(m.getMatchStatistics());

          String response;
          if (m.isSuccess()) {
            response = Formatter.collapseWhitespace(m.getResult().evaluate(m));
          } else {
            response = "no match found";
          }
          System.out.println(response);
          e.addBotResponse(response);
          e.popInput();
        }
      }
      System.out.print("> ");
      line = in.readLine();
    }
    System.out.println("Terminanting.");
  }
}
