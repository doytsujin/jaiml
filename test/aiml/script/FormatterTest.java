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
package aiml.script;

import junit.framework.TestCase;

public class FormatterTest extends TestCase {

  public void testFormal() {
    assertEquals("Abc Def", Formatter.formal("abc def"));
    assertEquals("Abc Def.ghi", Formatter.formal("abc def.ghi"));
  }

  public void testSentence() {
    assertEquals("This is a sentence.",
        Formatter.sentence("this is a sentence."));
  }

  public void testSingleSentenceWithoutPunctuation() {
    assertEquals("This is another sentence",
        Formatter.sentence("this is another sentence"));
  }

  public void testTwoSentencesWithPunctuation() {
    assertEquals("First sentence. Second sentence.",
        Formatter.sentence("first sentence. second sentence."));
  }

  public void testTwoSentencesWithoutPunctuation() {
    assertEquals("First sentence. Second sentence",
        Formatter.sentence("first sentence. second sentence"));
  }

  public void testCollapseWhitespace() {
    assertEquals("abcdef", Formatter.collapseWhitespace("abcdef"));
    assertEquals("abcdef", Formatter.collapseWhitespace("  abcdef"));
    assertEquals("abcdef", Formatter.collapseWhitespace("abcdef  "));
    assertEquals("abcdef",
        Formatter.collapseWhitespace("    abcdef   \n\n\t   "));
    assertEquals("abcdef g", Formatter.collapseWhitespace("  abcdef  g"));
    assertEquals("g abcdef", Formatter.collapseWhitespace("g abcdef"));
    assertEquals("ab cd e f",
        Formatter.collapseWhitespace("    ab  cd    e f   "));
  }

  public void testTrimPunctuation() {
    assertEquals("Hello Robert", Formatter.trimPunctiation("Hello Robert!"));
    assertEquals("Hello Robert",
        Formatter.trimPunctiation("!@$#!@#$Hello Robert!!@#$!!!"));
    assertEquals("Hello !!! Robert",
        Formatter.trimPunctiation("!@$#!@#$Hello !!! Robert!!@#$!!!"));
  }

}
