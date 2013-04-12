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
import aiml.classifier.PatternSequence.Pattern;
import aiml.classifier.PatternSequence.PatternIterator;
import aiml.context.Context;
import aiml.context.ContextInfo;
import aiml.context.InputContext;
import aiml.context.behaviour.AIMLPatternBehaviour;
import aiml.context.behaviour.PatternBehaviour;
import aiml.context.data.EnvironmentInputSource;
import aiml.context.data.StringSource;

public class PatternSequenceTest extends TestCase {
  public void testNormalIterator() throws MultipleContextsException {
    PatternBehaviour patternBehaviour = PatternBehaviour.getDefaultBehaviour();

    ContextInfo contextInfo = new ContextInfo();
    contextInfo.registerContext(new InputContext("input",
        new EnvironmentInputSource(), patternBehaviour));
    contextInfo.registerContext(new Context<String>("that", new StringSource(),
        patternBehaviour));
    contextInfo.registerContext(new Context<String>("topic",
        new StringSource(), patternBehaviour));

    PatternSequence s = new PatternSequence(contextInfo);

    s.add("input", "foo");
    Pattern p;
    PatternIterator iterator;

    iterator = s.iterator();
    p = iterator.next();
    assertEquals("input", p.getContext().getName());
    assertEquals("foo", p.getPattern());
    assertFalse(iterator.hasNext());

    s.add("topic", "bar");
    iterator = s.iterator();
    p = iterator.next();
    assertEquals("input", p.getContext().getName());
    assertEquals("foo", p.getPattern());
    assertTrue(iterator.hasNext());
    p = iterator.next();
    assertEquals("topic", p.getContext().getName());
    assertEquals("bar", p.getPattern());

    s = new PatternSequence(contextInfo);
    s.add("topic", "bar");
    iterator = s.iterator();
    p = iterator.next();
    assertEquals("topic", p.getContext().getName());
    assertEquals("bar", p.getPattern());
    assertFalse(iterator.hasNext());

  }

  public void testIteratorWithDefaults() throws MultipleContextsException {
    PatternBehaviour patternBehaviour = new AIMLPatternBehaviour();

    ContextInfo contextInfo = new ContextInfo();
    contextInfo.registerContext(new Context<String>("pre", new StringSource(),
        PatternBehaviour.getDefaultBehaviour()));

    contextInfo.registerContext(new InputContext("input",
        new EnvironmentInputSource(), patternBehaviour));
    contextInfo.registerContext(new Context<String>("that", new StringSource(),
        patternBehaviour));
    contextInfo.registerContext(new Context<String>("topic",
        new StringSource(), patternBehaviour));

    contextInfo.registerContext(new Context<String>("post", new StringSource(),
        PatternBehaviour.getDefaultBehaviour()));

    PatternSequence s = new PatternSequence(contextInfo);

    s.add("input", "foo");
    Pattern p;
    PatternIterator iterator;

    iterator = s.iterator();
    p = iterator.next();
    assertEquals("input", p.getContext().getName());
    assertEquals("foo", p.getPattern());
    assertTrue(iterator.hasNext());
    p = iterator.next();
    assertEquals("that", p.getContext().getName());
    assertEquals("*", p.getPattern());
    assertTrue(iterator.hasNext());
    p = iterator.next();
    assertEquals("topic", p.getContext().getName());
    assertEquals("*", p.getPattern());
    assertFalse(iterator.hasNext());

    s.add("topic", "bar");
    iterator = s.iterator();
    p = iterator.next();
    assertEquals("input", p.getContext().getName());
    assertEquals("foo", p.getPattern());
    assertTrue(iterator.hasNext());
    p = iterator.next();
    assertEquals("that", p.getContext().getName());
    assertEquals("*", p.getPattern());
    assertTrue(iterator.hasNext());
    p = iterator.next();
    assertEquals("topic", p.getContext().getName());
    assertEquals("bar", p.getPattern());
    assertFalse(iterator.hasNext());

    s = new PatternSequence(contextInfo);
    s.add("topic", "bar");
    iterator = s.iterator();
    p = iterator.next();
    assertEquals("input", p.getContext().getName());
    assertEquals("*", p.getPattern());
    assertTrue(iterator.hasNext());
    p = iterator.next();
    assertEquals("that", p.getContext().getName());
    assertEquals("*", p.getPattern());
    assertTrue(iterator.hasNext());
    p = iterator.next();
    assertEquals("topic", p.getContext().getName());
    assertEquals("bar", p.getPattern());
    assertFalse(iterator.hasNext());

    s = new PatternSequence(contextInfo);

    iterator = s.iterator();
    p = iterator.next();
    assertEquals("input", p.getContext().getName());
    assertEquals("*", p.getPattern());
    assertTrue(iterator.hasNext());
    p = iterator.next();
    assertEquals("that", p.getContext().getName());
    assertEquals("*", p.getPattern());
    assertTrue(iterator.hasNext());
    p = iterator.next();
    assertEquals("topic", p.getContext().getName());
    assertEquals("*", p.getPattern());
    assertFalse(iterator.hasNext());

    s.add("post", "X");
    iterator = s.iterator();
    p = iterator.next();
    assertEquals("input", p.getContext().getName());
    assertEquals("*", p.getPattern());
    assertTrue(iterator.hasNext());
    p = iterator.next();
    assertEquals("that", p.getContext().getName());
    assertEquals("*", p.getPattern());
    assertTrue(iterator.hasNext());
    p = iterator.next();
    assertEquals("topic", p.getContext().getName());
    assertEquals("*", p.getPattern());
    assertTrue(iterator.hasNext());
    p = iterator.next();
    assertEquals("post", p.getContext().getName());
    assertEquals("X", p.getPattern());
    assertFalse(iterator.hasNext());

  }

}
