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

package aiml.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import junit.framework.TestCase;

public class SentenceSplitterTest extends TestCase {

  private SentenceSplitter splitter;

  private static String getContents(InputStream in) {
    //...checks on aFile are elided
    StringBuilder contents = new StringBuilder();

    try {
      //use buffering, reading one line at a time
      //FileReader always assumes default encoding is OK!
      BufferedReader input = new BufferedReader(new InputStreamReader(in));
      try {
        String line = null; //not declared within while loop
        /*
        * readLine is a bit quirky :
        * it returns the content of a line MINUS the newline.
        * it returns null only for the END of the stream.
        * it returns an empty String if two newlines appear in a row.
        */
        while ((line = input.readLine()) != null) {
          contents.append(line);
          contents.append(System.getProperty("line.separator"));
        }
      } finally {
        input.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return contents.toString();
  }

  protected void setUp() throws Exception {
    InputStream in = this.getClass().getResourceAsStream("/aiml/text/rules.txt");
    String rules = getContents(in);
    splitter = new SentenceSplitter(rules);
  }

  public void testSplit() {
    Iterator<String> sentence;
    sentence = splitter.split(
        "she stopped. she said, \"hello there,\" and then went on.").iterator();
    assertEquals("she stopped. ", sentence.next());
    assertEquals("she said, \"hello there,\" and then went on.",
        sentence.next());
    assertFalse(sentence.hasNext());
  }

  public void testPunctuation() {
    Iterator<String> sentence;
    sentence = splitter.split("he's vanished! what will we do?  it's up to us.").iterator();
    assertEquals("he's vanished! ", sentence.next());
    assertEquals("what will we do?  ", sentence.next());
    assertEquals("it's up to us.", sentence.next());
    assertFalse(sentence.hasNext());
  }

  public void testSplitNumbers() {
    Iterator<String> sentence;
    sentence = splitter.split("Please add 1.5 liters to the tank.").iterator();
    assertEquals("Please add 1.5 liters to the tank.", sentence.next());
    assertFalse(sentence.hasNext());
  }

  /*
   * This test fails because AIML can't rely on proper capitalization to mark
   * sentence boundaries, and the rules aren't smart enough (yet) to recognize
   * that whitespace separated punctuation probably doesn't constitute a proper
   * sentence.
   * 
   * TODO: Fix this testcase.
   */
  public void testSplitFakePunctuation() {
    Iterator<String> sentence;
    sentence = splitter.split("\"No man is an island . . . every man . . . \"").iterator();
    assertEquals("\"No man is an island . . . every man . . . \"",
        sentence.next());
    assertFalse(sentence.hasNext());
  }

  /*
   * This test fails, because the simple sentence splitter doesn't handle
   * abbreviations. In AIML, this isn't a problem, because abbreviations will be
   * removed by input preprocessing before sentence splitting, so this case
   * won't happen, but for completeness sake, the test for it is here.
   */
  public void testSplitAbbreviations() {
    Iterator<String> sentence;
    sentence = splitter.split(
        "My friend, Mr. Jones, has a new dog.  The dog's name is Spot.").iterator();

    assertEquals("My friend, Mr. Jones, has a new dog.  ", sentence.next());
    assertEquals("The dog's name is Spot.", sentence.next());
    assertFalse(sentence.hasNext());

  }
}
