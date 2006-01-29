package demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParserException;

import aiml.bot.Bot;
import aiml.bot.BotSyntaxException;
import aiml.classifier.Classifier;
import aiml.classifier.MatchState;
import aiml.context.ContextInfo;
import aiml.context.StringContext;
import aiml.parser.AimlParserException;

public class InterpreterDemo {
  public static void main(String[] args) throws XmlPullParserException, IOException, BotSyntaxException, AimlParserException {  
    Logger.getLogger("aiml").setLevel(Level.WARNING);
    Classifier.registerDefaultNodeHandlers();
    Bot b = new Bot();
    System.out.println("Loading bot...");
    b.setProperty("name","scrapbook");
    b.load("bot.xml");
    System.out.println("done, loaded "+Classifier.getCount() + " categories.");
    System.out.println("Enter text to match, or /exit to quit");
    
    StringContext input = (StringContext)ContextInfo.getContext("input");
    
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String line = in.readLine();
    while (!line.equals("/exit")) {
      line = in.readLine();
      input.setValue(line);      
      MatchState m = Classifier.match();
      if (m!=null)
        System.out.println(((aiml.script.Script)m.getResult()).execute(m));
      else
        System.out.println("no match found");
    }
    System.out.println("bye bye");
  }
}
