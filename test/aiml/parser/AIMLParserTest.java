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
import aiml.classifier.node.PatternNodeFactory;
import aiml.context.ContextInfo;
import aiml.context.EnvironmentInputContext;
import aiml.context.StringContext;

public class AIMLParserTest extends TestCase {

  private AIMLParser ap;
  private Bot b;

  public AIMLParserTest(String s) {
    super(s);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    b = new Bot("foobar");
    b.setProperty("name", "foobar");
    b.setProperty("baz", "bar");

    ap = new AIMLParser(b);
    ContextInfo.registerContext(new EnvironmentInputContext("input"));
    ContextInfo.registerContext(new StringContext("that"));
    ContextInfo.registerContext(new StringContext("topic"));

    ContextInfo.registerContext(new StringContext("alpha"));
    ContextInfo.registerContext(new StringContext("beta"));
    ContextInfo.registerContext(new StringContext("gama"));
    ContextInfo.registerContext(new StringContext("delta"));

    ContextInfo.registerContext(new StringContext("foo"));
    ContextInfo.registerContext(new StringContext("bar"));

    ContextInfo.registerContext(new StringContext("ichi"));
    ContextInfo.registerContext(new StringContext("ni"));
    ContextInfo.registerContext(new StringContext("san"));

    Classifier.registerDefaultNodeHandlers();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    ContextInfo.reset();
    PatternNodeFactory.reset();
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