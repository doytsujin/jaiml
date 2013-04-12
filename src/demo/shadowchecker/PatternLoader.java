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

import java.util.regex.Matcher;

import aiml.classifier.MultipleContextsException;
import aiml.classifier.PatternSequence;
import aiml.context.Context;
import aiml.context.ContextInfo;
import aiml.context.InputContext;
import aiml.context.behaviour.PatternBehaviour;
import aiml.context.data.VariableSource;

public class PatternLoader {
  private java.util.regex.Pattern pathRegex = java.util.regex.Pattern.compile("\\[(.+?)\\](.*?)\\[/\\]");
  private ContextInfo contextInfo;

  public PatternLoader(ContextInfo contextInfo) {
    this.contextInfo = contextInfo;
  }

  public void addContexts(String s) {
    Matcher m = pathRegex.matcher(s);
    while (m.find()) {
      if (m.group(2).length() != 0) {
        throw new RuntimeException("Bad context description " + m.group());
      }
      String contextName = m.group(1);
      if ("input".equals(contextName)) {
        contextInfo.registerContext(new InputContext("input",
            new VariableInputSource("input"),
            PatternBehaviour.getDefaultBehaviour()));
      } else {
        contextInfo.registerContext(new Context<String>(contextName,
            new VariableSource(contextName),
            PatternBehaviour.getDefaultBehaviour()));
      }
    }
  }

  public PatternSequence fromString(String s) {
    Matcher m = pathRegex.matcher(s);
    PatternSequence sequence = new PatternSequence(contextInfo);
    while (m.find()) {
      try {
        sequence.add(m.group(1), m.group(2));
      } catch (MultipleContextsException e) {
        throw new RuntimeException(e);
      }
    }
    if ((sequence.getLength() == 0) && (s.length() > 0)) {
      throw new RuntimeException("Couldn't load pattern from " + s);
    }
    return sequence;
  }

}
