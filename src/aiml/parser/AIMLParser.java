package aiml.parser;
/**
 * <p>Title: AIML Pull Parser</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author Kim Sullivan
 * @version 1.0
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.xmlpull.v1.XmlPullParserException;

import junit.framework.*;
import junit.textui.TestRunner;


public class AIMLParser {
  
  public AIMLParser() {
    
  }
  
  private boolean isEvent(XmlParser parser, int eventType, String name) throws XmlPullParserException {
    if (parser==null)
      throw new IllegalArgumentException();
    return (parser.getEventType()==eventType && (name==null || parser.getName().equals(name)));         
  }
  
  private void doAiml(XmlParser parser) throws IOException, XmlPullParserException, AimlParserException {
    parser.nextTag();
    try {
      parser.require(XmlParser.START_TAG,null,"aiml");
      String version = parser.getAttributeValue(null,"version");
      if (version==null)
        throw new AimlSyntaxException("Syntax error, version attribute of root element must be present");
      if (!version.equals("1.0") && !version.equals("1.0.1"))
        throw new InvalidAimlVersionException("Unsupported AIML version, refusing forward compatible processing mode");
      
    } catch(XmlPullParserException e) {
      throw new AimlSyntaxException("Syntax error, root element must be 'aiml'",e);
    }
    parser.nextTag();
    doCategoryList(parser);
    parser.require(XmlParser.END_TAG,null,"aiml");
    parser.next();
    assert (isEvent(parser,XmlParser.END_DOCUMENT,null)) : "Syntax error, no markup allowed after the root element";
  }
  
  
  
  private void doCategoryList(XmlParser parser) throws IOException, XmlPullParserException, AimlParserException  {
    while (!isEvent(parser,XmlParser.END_TAG,"aiml")) {
      switch (parser.getEventType()) {
        case XmlParser.START_TAG:
          if (parser.getName().equals("category")) {
            doCategory(parser);
          } else if (parser.getName().equals("contextgroup")) {
            doContextGroup(parser);
          } else if (parser.getName().equals("topic")) {
            doTopic(parser);
          } else
            throw new AimlSyntaxException("Expected category, contextgroup or topic");
          break;
        case XmlParser.END_TAG:
          throw new AimlSyntaxException("Syntax error, end tag '" + parser.getName() + "' without opening tag");
        default:
          assert (false) :"Something very unexpected happened";          
      }
      parser.nextTag();
    }
  }

  private void doTopic(XmlParser parser) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("doTopic");
    
  }

  private void doContextGroup(XmlParser parser) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("doContextGroup");
    
  }

  private void doCategory(XmlParser parser) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("doCategory");
  }

  public void load(Reader in) throws IOException, XmlPullParserException, AimlParserException {
    XmlParser parser = new XmlParser();
    parser.setInput(in);
    doAiml(parser);
  }

  public void load(InputStream in, String encoding) throws IOException, XmlPullParserException, AimlParserException {
    XmlParser parser = new XmlParser();
    parser.setInput(in,encoding);
    doAiml(parser);
  }

  public class AIMLParserTest extends TestCase {
    public AIMLParserTest(String s){
      super(s);
    }
    public void testAimlRoot() throws Exception {
      load(new StringReader("<aiml version='1.0'/>"));
      
      try {
        load(new StringReader("<aiml></aiml>"));
        fail("Expected AimlSyntaxException");
      } catch (AimlSyntaxException e) {};

      try {
        load(new StringReader("<AIML></AIML>"));
        fail("Expected AimlSyntaxException");
      } catch (AimlSyntaxException e) {};
      
      try {
        load(new StringReader("<aiml version='1.0p'></aiml>"));
        fail("Expected InvalidAimlVersionException");
      } catch (InvalidAimlVersionException e) {};
      
      try {
        load(new StringReader("<aiml version='1.0'></aiml><foo></foo>"));
        fail("Expected XmlPullParserException");
      } catch (XmlPullParserException e) {};
      
    }

    public void testCategoryList() throws Exception {
      load(new FileInputStream("tests/categoryList-ok.aiml"),"UTF-8");

      try {
        load(new FileInputStream("tests/categoryList-badstart.aiml"),"UTF-8");
        fail("Expected AimlSyntaxException");
      } catch (AimlSyntaxException e) {};

      try {
        load(new FileInputStream("tests/categoryList-badstart2.aiml"),"UTF-8");
        fail("Expected AimlSyntaxException");
      } catch (AimlSyntaxException e) {};

      try {
        load(new FileInputStream("tests/categoryList-badend.aiml"),"UTF-8");
        fail("Expected AimlSyntaxException");
      } catch (AimlSyntaxException e) {};
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
