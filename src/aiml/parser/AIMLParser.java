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
  
  private void require(XmlParser parser, int eventType, String name, String failMessage) throws AimlSyntaxException, XmlPullParserException {
    if (parser==null)
      throw new IllegalArgumentException();
    if ((parser.getEventType()==eventType && (name==null || parser.getName().equals(name))))
      return;
    else
      throw new AimlSyntaxException("Syntax error: " + failMessage + " " + parser.getPositionDescription());
  }

  private void require(XmlParser parser, int eventType, String name) throws AimlSyntaxException, XmlPullParserException {
    if (parser==null)
      throw new IllegalArgumentException();
    if ((parser.getEventType()==eventType && (name==null || parser.getName().equals(name))))
      return;
    else
      throw new AimlSyntaxException("Syntax error: expected " + XmlParser.TYPES[eventType] + " '" + name + "' " + parser.getPositionDescription());
  }

  private void doAiml(XmlParser parser) throws IOException, XmlPullParserException, AimlParserException {
    parser.nextTag();
    require(parser,XmlParser.START_TAG,"aiml","root element must be 'aiml'");
    String version = parser.getAttributeValue(null,"version");
    if (version==null)
      throw new AimlSyntaxException("Syntax error, version attribute of root element must be present");
    if (!version.equals("1.0") && !version.equals("1.0.1"))
      throw new InvalidAimlVersionException("Unsupported AIML version, refusing forward compatible processing mode");
      
    parser.nextTag();
    doCategoryList(parser);
    require(parser, XmlParser.END_TAG,"aiml");
    parser.next();
    assert (isEvent(parser,XmlParser.END_DOCUMENT,null)) : "Syntax error, no markup allowed after the root element";
  }
  
  
  
  private void doCategoryList(XmlParser parser) throws IOException, XmlPullParserException, AimlParserException  {
    do {
      switch (parser.getEventType()) {
        case XmlParser.START_TAG:
          if (parser.getName().equals("category")) {
            doCategory(parser);
          } else if (parser.getName().equals("contextgroup")) {
            doContextGroup(parser);
          } else if (parser.getName().equals("topic")) {
            doTopic(parser);
          } else
            throw new AimlSyntaxException("Syntax error: expected category, contextgroup or topic "+parser.getPositionDescription());
          break;
        case XmlParser.END_TAG:
          if (parser.getName().equals("aiml")
              || parser.getName().equals("contextgroup")
              || parser.getName().equals("topic"))
            return;
          throw new AimlSyntaxException("Syntax error: end tag '" + parser.getName() + "' without opening tag "+parser.getPositionDescription());
        default:
          assert (false) :"Something very unexpected happened";          
      }
      //parser.nextTag();
    } while (true);
  }

  private void doTopic(XmlParser parser) throws IOException, XmlPullParserException, AimlParserException {
    require(parser,XmlParser.START_TAG,"topic");
    if (parser.getAttributeValue(null,"name")==null)
      throw new AimlSyntaxException("Topic must have mandatory name attribute ");
    parser.nextTag();
    doCategoryList(parser);
    require(parser,XmlParser.END_TAG,"topic");
    parser.nextTag();    
  }

  private void doContextGroup(XmlParser parser) throws IOException, XmlPullParserException, AimlParserException {
    require(parser,XmlParser.START_TAG,"contextgroup");
    parser.nextTag();
    doContextList(parser);
    doCategoryList(parser);
    require(parser,XmlParser.END_TAG,"contextgroup");
    parser.nextTag();
  }

  private void doContextList(XmlParser parser) throws XmlPullParserException, AimlSyntaxException, IOException {
    do {
      doContextDef(parser);
    } while (isEvent(parser,XmlParser.START_TAG,"context"));
  }

  private void doCategory(XmlParser parser) throws IOException, XmlPullParserException, AimlSyntaxException {
    require(parser,XmlParser.START_TAG,"category");
    parser.nextTag();
    if (isEvent(parser,XmlParser.START_TAG,"pattern")) {
      doPatternC(parser);
    }
    if (isEvent(parser,XmlParser.START_TAG,"that")) {
      doThatC(parser);
    }
    if (isEvent(parser,XmlParser.START_TAG,"context"))
      doContextList(parser);
    require(parser,XmlParser.START_TAG,"template","expected 'template' element in category");
    doTemplate(parser);
    require(parser,XmlParser.END_TAG,"category");
    parser.nextTag();    
  }

  private void doTemplate(XmlParser parser) throws IOException, XmlPullParserException, AimlSyntaxException {
    require(parser,XmlParser.START_TAG,"template");
    parser.next();
    doAIMLScript(parser);
    require(parser,XmlParser.END_TAG,"template");
    parser.nextTag();
    
  }

  private void doAIMLScript(XmlParser parser) throws XmlPullParserException {
    // TODO Auto-generated method stub
    do {
      switch(parser.getEventType()) {
        case XmlParser.END_TAG:
          return;
        default:
          throw new UnsupportedOperationException("doAIMLScript()"+parser.getPositionDescription());
      }
      // TODO parser.next();
    } while (true);
    
  }

  private void doContextDef(XmlParser parser) throws IOException, XmlPullParserException, AimlSyntaxException {
    require(parser,XmlParser.START_TAG,"context");
    if (parser.getAttributeValue(null,"name")==null)
      throw new AimlSyntaxException("Syntax error while parsing context definition, mandatory attribute 'name' missing "+parser.getPositionDescription());
    parser.next();
    doPattern(parser);
    require(parser,XmlParser.END_TAG,"context");
    parser.nextTag();
  }

  private void doPattern(XmlParser parser) throws IOException, XmlPullParserException, AimlSyntaxException {
PatternLoop:    
    do {
      switch (parser.getEventType()) {
        case XmlParser.START_TAG:
          if (parser.getName().equals("bot"))
            doBotConst(parser);
          else
            throw new AimlSyntaxException("Unexpected start tag '" + parser.getName() + "' while parsing pattern, only 'bot' allowed "+parser.getPositionDescription());
          break;
        case XmlParser.END_TAG:
          break PatternLoop;
        case XmlParser.TEXT:
          //append text to pattern
          parser.next();
          break;
        case XmlParser.END_DOCUMENT:
          throw new AimlSyntaxException("Unexpected end of document while parsing pattern "+parser.getPositionDescription());
        default:
          throw new IllegalStateException("Something really weird happened while parsing pattern "+parser.getPositionDescription());
      }
    } while (true);   
  }


  private void doBotConst(XmlParser parser) throws IOException, XmlPullParserException, AimlSyntaxException {
    require(parser,XmlParser.START_TAG,"bot");
    if (parser.getAttributeValue(null,"name")==null)
      throw new AimlSyntaxException("Syntax error while parsing bot constant in pattern, mandatory attribute 'name' missing "+parser.getPositionDescription());
    if (!parser.isEmptyElementTag())
      throw new AimlSyntaxException("Syntax error while parsing bot constant in pattern, element must be empty "+parser.getPositionDescription());
    parser.nextTag();
    parser.next();    
  }

  private void doThatC(XmlParser parser) throws IOException, XmlPullParserException, AimlSyntaxException {
    require(parser,XmlParser.START_TAG,"that");
    parser.next();
    doPattern(parser);
    require(parser,XmlParser.END_TAG,"that");
    parser.nextTag();    
  }

  private void doPatternC(XmlParser parser) throws IOException, XmlPullParserException, AimlSyntaxException {
    require(parser,XmlParser.START_TAG,"pattern");
    parser.next();
    doPattern(parser);
    require(parser,XmlParser.END_TAG,"pattern");
    parser.nextTag();    
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
    
    private void loadFail(Reader in,Class<? extends Exception> exception) throws Exception {
      try {
        load(in);
        fail("Expected AimlSyntaxException");
      } catch (Exception e) {
        if (exception.isAssignableFrom(e.getClass()))
          return;
        else
          throw e;
      }
      fail("Expected exception " + exception);
    }

    private void loadFail(InputStream in,String encoding, Class<? extends Exception> exception) throws Exception {
      try {
        load(in,encoding);
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
      load(new StringReader("<aiml version='1.0'/>"));     
      loadFail(new StringReader("<aiml></aiml>"),AimlSyntaxException.class);
      loadFail(new StringReader("<AIML></AIML>"),AimlSyntaxException.class);      
      loadFail(new StringReader("<aiml version='1.0p'></aiml>"),InvalidAimlVersionException.class);
      loadFail(new StringReader("<aiml version='1.0'></aiml><foo></foo>"),XmlPullParserException.class);
      
    }

    public void testCategoryList() throws Exception {
      load(new FileInputStream("tests/categoryList-ok.aiml"),"UTF-8");
      loadFail(new FileInputStream("tests/categoryList-badstart.aiml"),"UTF-8",AimlSyntaxException.class);
      loadFail(new FileInputStream("tests/categoryList-badstart2.aiml"),"UTF-8",AimlSyntaxException.class);
      loadFail(new FileInputStream("tests/categoryList-badend.aiml"),"UTF-8",AimlSyntaxException.class);
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
