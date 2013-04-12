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

package aiml.classifier;

import junit.framework.TestCase;

import org.xmlpull.v1.XmlPullParserException;

import aiml.bot.Bot;
import aiml.context.Context;
import aiml.context.ContextInfo;
import aiml.context.InputContext;
import aiml.context.behaviour.PatternBehaviour;
import aiml.context.data.EnvironmentInputSource;
import aiml.context.data.StringSource;
import aiml.context.data.VariableSource;
import aiml.environment.Environment;

public class ClassifierTest extends TestCase {

  public void testAdd() throws MultipleContextsException,
      DuplicatePathException {
    Classifier classifier = new Classifier();

    PatternBehaviour patternBehaviour = PatternBehaviour.getDefaultBehaviour();

    ContextInfo contextInfo = classifier.getContextInfo();
    contextInfo.registerContext(new InputContext("input",
        new EnvironmentInputSource(), patternBehaviour));
    contextInfo.registerContext(new Context<String>("that", new StringSource(),
        patternBehaviour));
    contextInfo.registerContext(new Context<String>("topic",
        new StringSource(), patternBehaviour));

    PatternSequence s = new PatternSequence(contextInfo);

    s.add("input", "foo");
    classifier.add(s, s.toString());
    s.add("that", "bar");
    classifier.add(s, s.toString());

  }

  private static NodeStatistics assertNodeStatsIncrease(
      NodeStatistics originalStats, int nodeIncrease, PatternSequence s,
      Classifier classifier) throws DuplicatePathException {

    classifier.add(s, s.toString());
    NodeStatistics newStats = classifier.getNodeStats();
    assertEquals(nodeIncrease, newStats.getNodeCount() -
        originalStats.getNodeCount());
    assertEquals(newStats.getNodeCount() - 1, newStats.getBranchCount());
    return newStats;
  }

  public void testGetNodeCount() throws MultipleContextsException,
      DuplicatePathException {
    Classifier classifier = new Classifier();

    PatternBehaviour patternBehaviour = PatternBehaviour.getDefaultBehaviour();

    ContextInfo contextInfo = classifier.getContextInfo();
    contextInfo.registerContext(new Context<String>("test", new StringSource(),
        patternBehaviour));

    NodeStatistics nodeStats = classifier.getNodeStats();
    assertEquals(0, nodeStats.getNodeCount());

    PatternSequence s;

    s = new PatternSequence(contextInfo);
    s.add("test", "foo");
    nodeStats = assertNodeStatsIncrease(nodeStats, 3, s, classifier); //context, string, leaf

    s = new PatternSequence(contextInfo);
    s.add("test", "bar");
    nodeStats = assertNodeStatsIncrease(nodeStats, 3, s, classifier);//branch,string, leaf 

    s = new PatternSequence(contextInfo);
    s.add("test", "foobar");
    nodeStats = assertNodeStatsIncrease(nodeStats, 2, s, classifier);//add suffix + leaf

    s = new PatternSequence(contextInfo);
    s.add("test", "");
    nodeStats = assertNodeStatsIncrease(nodeStats, 2, s, classifier);//prepend one EOS node + leaf

    s = new PatternSequence(contextInfo);
    s.add("test", "c");
    nodeStats = assertNodeStatsIncrease(nodeStats, 2, s, classifier);//add a branch pointing to an EOS node + leaf

    s = new PatternSequence(contextInfo);
    s.add("test", "cooooooool");
    nodeStats = assertNodeStatsIncrease(nodeStats, 2, s, classifier);//append the suffix to the eos node + leaf

  }

  public void testMatchFailed() throws MultipleContextsException,
      DuplicatePathException, XmlPullParserException {
    Classifier classifier = new Classifier();

    PatternBehaviour patternBehaviour = PatternBehaviour.getDefaultBehaviour();

    ContextInfo contextInfo = classifier.getContextInfo();
    contextInfo.registerContext(new InputContext("input",
        new EnvironmentInputSource(), patternBehaviour));
    contextInfo.registerContext(new Context<String>("that", new VariableSource(
        "that"), patternBehaviour));
    contextInfo.registerContext(new Context<String>("topic",
        new VariableSource("topic"), patternBehaviour));

    Bot b = new Bot(classifier);
    Environment e = b.createEnvironment();
    MatchState state;
    {
      e.pushInput("");
      state = e.match();
      assertFalse(state.isSuccess());
      e.popInput();
    }
    {
      e.pushInput("foo");

      state = e.match();
      assertFalse(state.isSuccess());

      PatternSequence s = new PatternSequence(contextInfo);
      s.add("input", "foo");
      classifier.add(s, s.toString());

      state = e.match();
      assertTrue(state.isSuccess());

      e.popInput();
    }
    {
      e.pushInput("bar");

      state = e.match();
      assertFalse(state.isSuccess());
    }
  }

}
