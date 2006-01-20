package aiml.parser;
/**
 * <p>Title: AIML Pull Parser</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author Kim Sullivan
 * @version 1.0
 */

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import junit.framework.*;
import junit.textui.TestRunner;


public class AIMLParser {

  public AIMLParser() {
    
  }
  
  public class AIMLParserTest extends TestCase {
    public AIMLParserTest(String s){
      super(s);
    }
  }
  
  private AIMLParserTest getTest(String name) {
    return new AIMLParserTest(name);
  }
  
  public static Test suite() {
    AIMLParser ap= new AIMLParser();
    TestSuite t = new TestSuite();
    t.setName("AIMLParser.AIMLParserTest");
    Method[] methods = AIMLParserTest.class.getMethods();
    for (int i = 0; i < methods.length; i++) {
      if (methods[i].getName().startsWith("test") && Modifier.isPublic(methods[i].getModifiers())) {
        t.addTest(ap.getTest(methods[i].getName()));
      }
    }
    return t;
  }
  public static void main(String[] args) {
    TestRunner.run(AIMLParser.suite());    
  }

}
