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
/**
 * 
 */
package aiml.parser;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.xmlpull.v1.XmlPullParserException;

import aiml.bot.Bot;
import aiml.classifier.Classifier;
import aiml.context.ContextInfo;
import aiml.context.EnvironmentInputContext;
import aiml.context.StringContext;

public class AIMLParserTest extends TestCase {

  private Classifier classifier;
  private AIMLParser ap;
  private Bot b;
  private ContextInfo contextInfo;

  public AIMLParserTest(String s) {
    super(s);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    classifier = new Classifier();

    b = new Bot(classifier, "foobar");
    b.setProperty("name", "foobar");
    b.setProperty("baz", "bar");

    ap = new AIMLParser(b);
    contextInfo = ContextInfo.getInstance();
    contextInfo.registerContext(new EnvironmentInputContext("input"));
    contextInfo.registerContext(new StringContext("that"));
    contextInfo.registerContext(new StringContext("topic"));

    contextInfo.registerContext(new StringContext("alpha"));
    contextInfo.registerContext(new StringContext("beta"));
    contextInfo.registerContext(new StringContext("gama"));
    contextInfo.registerContext(new StringContext("delta"));

    contextInfo.registerContext(new StringContext("foo"));
    contextInfo.registerContext(new StringContext("bar"));

    contextInfo.registerContext(new StringContext("ichi"));
    contextInfo.registerContext(new StringContext("ni"));
    contextInfo.registerContext(new StringContext("san"));

    classifier.registerDefaultNodeHandlers();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    contextInfo.reset();
  }

  private void loadFail(Reader in, Class<? extends Exception> exception)
      throws Exception {
    try {
      ap.load(in);
      fail("Expected AimlSyntaxException");
    } catch (Exception e) {
      if (exception.isAssignableFrom(e.getClass()))
        return;
      else
        throw e;
    }
    fail("Expected exception " + exception);
  }

  private void loadFail(InputStream in, String encoding,
      Class<? extends Exception> exception) throws Exception {
    try {
      ap.load(in, encoding);
      fail("Expected AimlSyntaxException");
    } catch (Exception e) {
      if (exception.isAssignableFrom(e.getClass()))
        return;
      else
        throw e;
    }
    fail("Expected exception " + exception);
  }

  public void testAimlRoot() throws Exception {
    ap.load(new StringReader("<aiml version='1.0'/>"));
    loadFail(new StringReader("<aiml></aiml>"), AimlSyntaxException.class);
    loadFail(new StringReader("<AIML></AIML>"), AimlSyntaxException.class);
    loadFail(new StringReader("<aiml version='1.0p'></aiml>"),
        InvalidAimlVersionException.class);
    loadFail(new StringReader("<aiml version='1.0'></aiml><foo></foo>"),
        XmlPullParserException.class);

  }

  public void testCategoryList() throws Exception {
    ap.load(new FileInputStream("tests/categoryList-ok.aiml"), "UTF-8");
  }

  public void testCategoryListBadStart() throws Exception {
    loadFail(new FileInputStream("tests/categoryList-badstart.aiml"), "UTF-8",
        AimlSyntaxException.class);
  }

  public void testCategoryListBadStart2() throws Exception {
    loadFail(new FileInputStream("tests/categoryList-badstart2.aiml"), "UTF-8",
        AimlSyntaxException.class);
  }

  public void testCategoryListBadEnd() throws Exception {
    loadFail(new FileInputStream("tests/categoryList-badend.aiml"), "UTF-8",
        AimlSyntaxException.class);
  }

  public void testLoadPatterns() throws Exception {
    ap.load(new FileInputStream("tests/patterns.aiml"), "UTF-8");
  }

  public void testLoadPatternsBad1() throws Exception {
    loadFail(new FileInputStream("tests/patterns-bad1.aiml"), "UTF-8",
        AimlSyntaxException.class);
  }

  public void testLoadPatternsBad2() throws Exception {
    loadFail(new FileInputStream("tests/patterns-bad1.aiml"), "UTF-8",
        AimlSyntaxException.class);
  }

  public void testLoadPatternsBad3() throws Exception {
    loadFail(new FileInputStream("tests/patterns-bad1.aiml"), "UTF-8",
        AimlSyntaxException.class);
  }

  public void testLoadPatternsBad4() throws Exception {
    loadFail(new FileInputStream("tests/patterns-bad1.aiml"), "UTF-8",
        AimlSyntaxException.class);
  }

  public void testLoadTemplate() throws Exception {
    ap.load(new FileInputStream("tests/templates.aiml"), "UTF-8");
  }
}