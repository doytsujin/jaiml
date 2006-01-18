package aiml.parser;

import java.io.StringReader;

import junit.framework.TestCase;

public class AIMLPullParserTest extends TestCase {
  private AIMLPullParser pp;
  public static void main(String[] args) {
    junit.textui.TestRunner.run(AIMLPullParserTest.class);
  }

  protected void setUp() throws Exception {
    super.setUp();
    pp=new AIMLPullParser();
    
  }
}
