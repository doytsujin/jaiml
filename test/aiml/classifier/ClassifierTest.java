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
import aiml.context.Context;
import aiml.context.ContextInfo;
import aiml.context.InputContext;
import aiml.context.behaviour.PatternBehaviour;
import aiml.context.data.EnvironmentInputSource;
import aiml.context.data.StringSource;

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

    PaternSequence s = new PaternSequence(contextInfo);

    s.add("input", "foo");
    classifier.add(s, s.toString());
    s.add("that", "bar");
    classifier.add(s, s.toString());

  }

}
