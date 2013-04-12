/*
    jaiml - java AIML library
    Copyright (C) 2009  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package demo.shadowchecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.aitools.programd.graph.NonOptimalNodemaster;
import org.aitools.programd.graph.OneOptimalNodemaster;
import org.aitools.programd.graph.ThreeOptimalNodemaster;
import org.aitools.programd.graph.TwoOptimalNodemaster;
import org.xmlpull.v1.XmlPullParserException;

import aiml.bot.Bot;
import aiml.bot.BotSyntaxException;
import aiml.category.CategoryEvent;
import aiml.category.CategoryListener;
import aiml.classifier.Classifier;
import aiml.classifier.DuplicatePathException;
import aiml.classifier.MatchState;
import aiml.classifier.MatchStatistics;
import aiml.classifier.MultipleContextsException;
import aiml.classifier.NodeStatistics;
import aiml.classifier.Pattern;
import aiml.classifier.PatternSequence;
import aiml.classifier.PatternSequence.PatternIterator;
import aiml.classifier.node.PatternNode;
import aiml.context.Context;
import aiml.context.ContextInfo;
import aiml.context.behaviour.AIMLPatternBehaviour;
import aiml.context.behaviour.CompactPatternBehaviour;
import aiml.context.behaviour.PatternBehaviour;
import aiml.context.data.DataSource;
import aiml.context.data.InputDataSource;
import aiml.context.data.ResponseHistorySource;
import aiml.context.data.VariableSource;
import aiml.environment.Environment;
import aiml.parser.AimlParserException;
import demo.shadowchecker.dummynodes.NonOptimalNode;

public class ShadowChecker {

  private static final Runtime runtime = Runtime.getRuntime();

  private static boolean csvMode;
  private static boolean silentMode;
  private static long timeStamp = System.nanoTime();
  private static final int EXPECTED_CATEGORIES = 50000;
  private ArrayList<TestCase> allPatterns = new ArrayList<TestCase>(
      EXPECTED_CATEGORIES);
  private RandomWords rng = new RandomWords();
  private Classifier classifier;

  private static String behaviour;
  private static String mapType;

  private static int nCount = 10000;

  public ShadowChecker() {
  }

  private enum Mode {
    USAGE, EXTRACT, TRANSFORM, CHECK, MEM
  }

  private static PatternBehaviour customBehaviour() {
    Map<?, ?> mapPrototype;
    if (mapType == null) {
      if ("default".equals(behaviour)) {
        mapType = "HashMap";
      } else if ("classic".equals(behaviour)) {
        mapType = "1HashMap";
      } else {
        mapType = "Unknown";
      }
    }
    if ("0".equals(mapType)) {
      mapPrototype = new NonOptimalNodemaster();
    } else if ("1".equals(mapType)) {
      mapPrototype = new OneOptimalNodemaster();
    } else if ("2".equals(mapType)) {
      mapPrototype = new TwoOptimalNodemaster();
    } else if ("3".equals(mapType)) {
      mapPrototype = new ThreeOptimalNodemaster();

    } else if ("HashMap".equals(mapType)) {
      mapPrototype = new HashMap();
    } else if ("TreeMap".equals(mapType)) {
      mapPrototype = new TreeMap();
    } else if ("LinkedHashMap".equals(mapType)) {
      mapPrototype = new LinkedHashMap();

    } else if ("0HashMap".equals(mapType)) {
      mapPrototype = new NonOptimalNodemaster(HashMap.class);
    } else if ("1HashMap".equals(mapType)) {
      mapPrototype = new OneOptimalNodemaster(HashMap.class);
    } else if ("2HashMap".equals(mapType)) {
      mapPrototype = new TwoOptimalNodemaster(HashMap.class);
    } else if ("3HashMap".equals(mapType)) {
      mapPrototype = new ThreeOptimalNodemaster(HashMap.class);

    } else if ("0TreeMap".equals(mapType)) {
      mapPrototype = new NonOptimalNodemaster(TreeMap.class);
    } else if ("1TreeMap".equals(mapType)) {
      mapPrototype = new OneOptimalNodemaster(TreeMap.class);
    } else if ("2TreeMap".equals(mapType)) {
      mapPrototype = new TwoOptimalNodemaster(TreeMap.class);
    } else if ("3TreeMap".equals(mapType)) {
      mapPrototype = new ThreeOptimalNodemaster(TreeMap.class);

    } else if ("0LinkedHashMap".equals(mapType)) {
      mapPrototype = new NonOptimalNodemaster(LinkedHashMap.class);
    } else if ("1LinkedHashMap".equals(mapType)) {
      mapPrototype = new OneOptimalNodemaster(LinkedHashMap.class);
    } else if ("2LinkedHashMap".equals(mapType)) {
      mapPrototype = new TwoOptimalNodemaster(LinkedHashMap.class);
    } else if ("3LinkedHashMap".equals(mapType)) {
      mapPrototype = new ThreeOptimalNodemaster(LinkedHashMap.class);

    } else {
      mapPrototype = null;
    }

    if (mapPrototype == null) {
      if (behaviour == null) {
        return PatternBehaviour.getDefaultBehaviour();
      }
      if ("default".equals(behaviour)) {
        return new CompactPatternBehaviour();
      }

      if ("classic".equals(behaviour)) {
        return new AIMLPatternBehaviour();
      }
    } else {
      if (behaviour == null) {
        return new CompactPatternBehaviour(mapPrototype);
      }
      if ("default".equals(behaviour)) {
        return new CompactPatternBehaviour(mapPrototype);
      }

      if ("classic".equals(behaviour)) {
        return new AIMLPatternBehaviour(mapPrototype);
      }

    }
    return PatternBehaviour.getDefaultBehaviour();

  }

  public static void main(String[] args) throws XmlPullParserException,
      IOException, BotSyntaxException, AimlParserException {
    Logger.getLogger("aiml").setLevel(Level.WARNING);
    System.out.println("Benchmark / shadow checking program");
    System.out.println("(C) Kim Sullivan, 2009");

    String inputFilename = null;
    String patternFilename = null;
    PrintStream out = System.out;
    Mode mode = Mode.USAGE;

    if (args.length >= 1) {
      if (args[0].charAt(0) != '-' || args[0].length() != 2) {
        usage();
      }

      switch (args[0].charAt(1)) {
      case 'e':
        mode = Mode.EXTRACT;
        if (args.length == 1) {
          usage();
        }
        if (args.length >= 2) {
          inputFilename = args[1];
        }
        if (args.length == 3) {
          out = new PrintStream(args[2]);
        }
        break;
      case 't':
        mode = Mode.TRANSFORM;
        if (args.length == 1) {
          usage();
        }
        if (args.length >= 2) {
          patternFilename = args[1];
        }
        if (args.length >= 3) {
          out = new PrintStream(args[2]);
        }
      case 'c':
        mode = Mode.CHECK;
        if (args.length == 1) {
          usage();
        }
        if (args.length >= 2) {
          patternFilename = args[1];
        }
        if (args.length >= 3) {
          if ("default".equals(args[2])) {
            behaviour = args[2];
          } else if ("classic".equals(args[2])) {
            behaviour = args[2];
          } else {
            usage();
          }
        }
        if (args.length >= 4) {
          if ("-f".equals(args[3])) {
            out = new PrintStream(patternFilename + '-' + timeStamp + '.' +
                args[2] + '.');
          } else if ("-c".equals(args[3])) {
            csvMode = true;
            out = new PrintStream(patternFilename + '-' + timeStamp + '.' +
                args[2] + ".csv");
          } else if ("-s".equals(args[3])) {
            csvMode = true;
            silentMode = true;
            out = new PrintStream(patternFilename + '-' + timeStamp + '.' +
                args[2] + ".csv");
          } else {
            usage();
          }
        }

        if (args.length >= 5) {
          mapType = args[4];
        }
        break;
      case 'm':
        mode = Mode.MEM;
        if (args.length >= 2) {
          nCount = Integer.valueOf(args[1]);
        }

        break;
      default:
        mode = Mode.USAGE;
      }
    } else {
      usage();
    }

    ShadowChecker sc;
    switch (mode) {
    case EXTRACT:
      if (!new File(inputFilename).exists()) {
        System.err.println("Error: file not found : " + inputFilename);
        return;
      }
      sc = new ShadowChecker();
      sc.extractPatterns(inputFilename, out);
      break;
    case TRANSFORM:
      if (!new File(patternFilename).exists()) {
        System.err.println("Error: file not found : " + patternFilename);
        return;
      }
      sc = new ShadowChecker();

      break;
    case CHECK:
      if (!new File(patternFilename).exists()) {
        System.err.println("Error: file not found : " + patternFilename);
        return;
      }
      sc = new ShadowChecker();
      sc.runPatterns(patternFilename, customBehaviour(), out);
      break;
    case MEM:
      PatternNode[] nList = new PatternNode[nCount];
      for (int i = 0; i < nList.length; i++) {
        nList[i] = new NonOptimalNode();
        if (i % 1000 == 0) {
          memStat();
        }
      }
      break;
    case USAGE:
      usage();
    }

    memStat();
    //AIMLPatternBehaviour behaviour = new AIMLPatternBehaviour();

    //sc.runBot(botFilename, PatternBehaviour.getDefaultBehaviour());
  }

  private static void usage() {
    System.out.println("Bad arguments. Possible options:");
    System.out.println("-e <bot.xml> [output file]\t\tExtract patterns from the bot definition file to a separate file (or stdout)");
    System.out.println("-c <patterns.txt> [(default|classic) [(-f|-c) [nodeType]]\t\tCheck patterns using the specified behaviour");
    System.out.println("\t\t\t-f output to patterns.txt.behaviour");
    System.out.println("\t\t\t-c csv mode, output to patterns.txt.behaviour.csv");
    System.out.println("\t\t\t-s silent csv mode, don't print results of each testcase");
    System.out.println("\t\t\tnodeType [n](HashMap|TreeMap|LinkedHashMap), n=0..3 for NodeMapper");
    System.exit(1);
  }

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

  private PatternSequence.Pattern makeInput(Context context) {
    StringBuilder inputString = new StringBuilder();
    rng.appendRandomWords(inputString);
    return new PatternSequence.Pattern(context, inputString.toString());
  }

  private PatternSequence.Pattern makeInput(PatternSequence.Pattern pattern) {
    String patternString = pattern.getPattern();
    StringBuilder inputString = new StringBuilder(patternString.length());
    for (int depth = 0; depth < patternString.length(); depth++) {
      if (Pattern.isWildcard(depth, patternString)) {
        rng.appendRandomWords(inputString);
      } else {
        inputString.append(patternString.charAt(depth));
      }
    }

    PatternSequence.Pattern result = new PatternSequence.Pattern(
        pattern.getContext(), inputString.toString());

    return result;
  }

  private void setEnvironment(PatternSequence sequence, Environment e) {
    for (PatternSequence.Pattern p : sequence) {

      DataSource<? extends Object> dataSource = p.getContext().getDataSource();
      if (dataSource instanceof VariableSource) {
        e.setVar(p.getContext().getName(), p.getPattern());
      } else if (dataSource instanceof InputDataSource<?>) {
        InputDataSource source = (InputDataSource) p.getContext().getDataSource();
        source.push(p.getPattern(), e);
      } else if (dataSource instanceof ResponseHistorySource) {
        e.addBotResponse(p.getPattern());
      } else {
        throw new RuntimeException(
            "Don't know how to set up the environment for a " +
                dataSource.getClass().getName());
      }

    }
  }

  private PatternSequence makeInput(PatternSequence sequence) {
    PatternSequence inputSequence = new PatternSequence(
        classifier.getContextInfo());
    PatternIterator patterns = sequence.iterator();
    try {
      for (int order = 0; order < classifier.getContextInfo().getCount(); order++) {
        Context context = classifier.getContextInfo().getContext(order);
        if (!patterns.hasNext() ||
            context.compareTo(patterns.peek().getContext()) < 0) {
          inputSequence.add(makeInput(context));
        } else {
          inputSequence.add(makeInput(patterns.next()));
        }
      }
    } catch (MultipleContextsException e) {
      throw new RuntimeException(e);
    }

    return inputSequence;
  }

  /**
   * Extracts all patterns from a bot so they can be later easily loaded.
   * 
   * @param botFilename
   * @param out
   * @throws XmlPullParserException
   * @throws BotSyntaxException
   * @throws IOException
   * @throws AimlParserException
   */
  private void extractPatterns(String botFilename, final PrintStream out)
      throws XmlPullParserException, BotSyntaxException, IOException,
      AimlParserException {
    System.out.println("Extracting patterns from " + botFilename);
    Logger.getLogger("aiml").setLevel(Level.SEVERE);

    classifier = new Classifier();
    classifier.addCategoryListener(new CategoryListener() {
      private boolean first = true;

      @Override
      public void categoryAdded(CategoryEvent categoryEvent) {
        if (first) {
          ContextInfo ci = classifier.getContextInfo();
          for (int i = 0; i < ci.getCount(); i++) {
            out.print('[');
            out.print(ci.getContext(i).getName());
            out.print("][/]");
          }
          out.println();
          first = false;
        }
        for (PatternSequence.Pattern p : categoryEvent.getSequence()) {
          out.print(p);
          out.print("[/]");
        }
        out.println();
        if (classifier.getCount() % 1000 == 0) {
          memStat();
        }
      }
    });

    Bot b = new Bot(classifier);
    b.load(botFilename);

    System.out.println("Done.");
    System.out.println("Using " +
        PatternBehaviour.getDefaultBehaviour().getClass().getName());
    System.out.println("Loaded " + classifier.getCount() + " categories, " +
        classifier.getNodeStats());
    memStat();

  }

  private void runPatterns(String filename, PatternBehaviour patternBehaviour,
      PrintStream out) throws XmlPullParserException, IOException,
      BotSyntaxException, AimlParserException {
    if (patternBehaviour != null) {
      PatternBehaviour.setDefaultBehaviour(patternBehaviour);
    }
    System.out.println("Using " +
        PatternBehaviour.getDefaultBehaviour().getClass().getName());

    classifier = new Classifier();

    PatternLoader loader = new PatternLoader(classifier.getContextInfo());

    BufferedReader in = new BufferedReader(new FileReader(filename));

    System.out.println("Loading patterns...");
    String line = in.readLine();
    loader.addContexts(line);
    line = in.readLine();
    long charcount = 0;
    int wordcount = 0;
    while (line != null) {
      PatternSequence sequence = loader.fromString(line);
      PatternSequence input = makeInput(sequence);
      if (csvMode) {
        for (PatternSequence.Pattern p : input) {
          int depth = 0;
          String pattern = p.getPattern();
          charcount += pattern.length();
          while ((depth = Pattern.nextWord(depth, pattern)) < pattern.length()) {
            wordcount++;
          }
        }
      }
      TestCase test = new TestCase(sequence, input, null);
      try {
        classifier.add(sequence, test);
        allPatterns.add(test);
      } catch (DuplicatePathException e) {
        //ignore
      }
      if (classifier.getCount() % 1000 == 0) {
        System.out.println(classifier.getCount());
        memStat();
      }
      line = in.readLine();
    }

    in.close();

    Bot b = new Bot(classifier);
    MatchStatistics totalMatchStatistics = new MatchStatistics();

    System.out.println("done, loaded " + classifier.getCount() +
        " categories, " + classifier.getNodeStats());

    if (csvMode && !silentMode) {
      out.println(MatchStatistics.toCSVHeader());
    }

    int progress = 0;
    long startTime = System.nanoTime();
    for (TestCase test : allPatterns) {
      Environment e = b.createEnvironment();
      setEnvironment(test.input, e);
      //System.out.println(test.pattern);
      MatchState<TestCase> m = e.match();
      progress++;
      totalMatchStatistics.add(m.getMatchStatistics());
      if (!silentMode) {
        if (csvMode) {
          out.print(m.getMatchStatistics().toCSV());
          if (m.isSuccess()) {
            if (m.getResult() == test) {
              out.println(",Ok");
            } else {
              out.print(",Shadow,");
              out.print(m.getResult().pattern);
              out.print(',');
              out.println(test.pattern);
            }
          } else {
            out.print(",Fail,");
            out.println(test.pattern);
          }
        } else {
          if (m.isSuccess()) {
            if (m.getResult() == test) {
            } else {
              out.println("Shadow");
              out.println(test.input);
              out.println(test.pattern);
              out.println(m.getResult().pattern);
            }
          } else {
            out.println("No match");
            out.println(test.input);
            out.println(test.pattern);
            out.println(m.getResult().pattern);
          }
        }
      }
      if (progress % 1000 == 0) {
        System.out.println("" + progress + "/" + classifier.getCount());
      }
    }
    long endTime = System.nanoTime();
    if (!silentMode) {
      out.println();
    } else {
      out.println(MatchStatistics.toCSVHeader());
      out.println(totalMatchStatistics.toCSV());
      out.println(NodeStatistics.toCSVHeader());
      out.println(classifier.getNodeStats().toCSV());

    }
    out.print("patterns,");
    out.println(progress);

    out.print("words,");
    out.println(wordcount);

    out.print("characters,");
    out.println(charcount);

    out.print("time,");
    out.println(endTime - startTime);

    out.print("behaviour,");
    out.println(PatternBehaviour.getDefaultBehaviour().getClass().getName());

    out.print("map,");
    out.println(PatternBehaviour.getDefaultBehaviour().newMap().getClass().getName());

    out.print("mapType,");
    out.println(mapType);

    System.out.println("Done.");

  }
}
