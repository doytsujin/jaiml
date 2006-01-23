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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import aiml.bot.Bot;
import aiml.bot.InvalidConstantException;

import junit.framework.*;
import junit.textui.TestRunner;


public class AIMLParser {
  
  XmlPullParser parser;
  Bot bot;
  
  public AIMLParser(Bot bot) throws XmlPullParserException {
    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    parser = factory.newPullParser();
    this.bot=bot;
  }
  
  private boolean isEvent(int eventType, String name) throws XmlPullParserException {
    return (parser.getEventType()==eventType && (name==null || parser.getName().equals(name)));         
  }
  
  private void require(int eventType, String name, String failMessage) throws AimlSyntaxException, XmlPullParserException {
    if ((parser.getEventType()==eventType && (name==null || parser.getName().equals(name))))
      return;
    else
      throw new AimlSyntaxException("Syntax error: " + failMessage + " " + parser.getPositionDescription());
  }

  private void require(int eventType, String name) throws AimlSyntaxException, XmlPullParserException {
    if (parser==null)
      throw new IllegalArgumentException();
    if ((parser.getEventType()==eventType && (name==null || parser.getName().equals(name))))
      return;
    else
      throw new AimlSyntaxException("Syntax error: expected " + XmlPullParser.TYPES[eventType] + " '" + name + "' " + parser.getPositionDescription());
  }
  
  private String requireAttrib(String name) throws AimlSyntaxException {
    if (parser.getAttributeValue(null,name)==null)
      throw new AimlSyntaxException("Syntax error: mandatory attribute '" + name + "' missing from element '" + parser.getName() + "' "+ parser.getPositionDescription());
    else return parser.getAttributeValue(null,name);
  }
  
  private void doAiml() throws IOException, XmlPullParserException, AimlParserException {
    parser.nextTag();
    require(XmlPullParser.START_TAG,"aiml","root element must be 'aiml'");
    String version=requireAttrib("version");
    if (!version.equals("1.0") && !version.equals("1.0.1"))
      throw new InvalidAimlVersionException("Unsupported AIML version, refusing forward compatible processing mode");
      
    parser.nextTag();
    doCategoryList();
    require(XmlPullParser.END_TAG, "aiml");
    parser.next();
    assert (isEvent(XmlPullParser.END_DOCUMENT,null)) : "Syntax error, no markup allowed after the root element";
  }
  
  
  
  private void doCategoryList() throws IOException, XmlPullParserException, AimlParserException  {
    do {
      switch (parser.getEventType()) {
        case XmlPullParser.START_TAG:
          if (parser.getName().equals("category")) {
            doCategory();
          } else if (parser.getName().equals("contextgroup")) {
            doContextGroup();
          } else if (parser.getName().equals("topic")) {
            doTopic();
          } else
            throw new AimlSyntaxException("Syntax error: expected category, contextgroup or topic "+parser.getPositionDescription());
          break;
        case XmlPullParser.END_TAG:
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

  private void doTopic() throws IOException, XmlPullParserException, AimlParserException {
    require(XmlPullParser.START_TAG,"topic");
    requireAttrib("name");
    parser.nextTag();
    doCategoryList();
    require(XmlPullParser.END_TAG,"topic");
    parser.nextTag();    
  }

  private void doContextGroup() throws IOException, XmlPullParserException, AimlParserException {
    require(XmlPullParser.START_TAG,"contextgroup");
    parser.nextTag();
    doContextList();
    doCategoryList();
    require(XmlPullParser.END_TAG,"contextgroup");
    parser.nextTag();
  }

  private void doContextList() throws XmlPullParserException, AimlSyntaxException, IOException {
    do {
      doContextDef();
    } while (isEvent(XmlPullParser.START_TAG,"context"));
  }

  private void doCategory() throws IOException, XmlPullParserException, AimlSyntaxException {
    require(XmlPullParser.START_TAG,"category");
    parser.nextTag();
    if (isEvent(XmlPullParser.START_TAG,"pattern")) {
      doPatternC();
    }
    if (isEvent(XmlPullParser.START_TAG,"that")) {
      doThatC();
    }
    if (isEvent(XmlPullParser.START_TAG,"context"))
      doContextList();
    require(XmlPullParser.START_TAG,"template","expected 'template' element in category");
    doTemplate();
    require(XmlPullParser.END_TAG,"category");
    parser.nextTag();    
  }

  private void doTemplate() throws IOException, XmlPullParserException, AimlSyntaxException {
    require(XmlPullParser.START_TAG,"template");
    parser.next();
    doAIMLScript();
    require(XmlPullParser.END_TAG,"template");
    parser.nextTag();
    
  }

  private void doAIMLScript() throws XmlPullParserException {
    // TODO Auto-generated method stub
    do {
      switch(parser.getEventType()) {
        case XmlPullParser.END_TAG:
          return;
        default:
          throw new UnsupportedOperationException("doAIMLScript()"+parser.getPositionDescription());
      }
      // TODO parser.next();
    } while (true);
    
  }

  private void doContextDef() throws IOException, XmlPullParserException, AimlSyntaxException {
    require(XmlPullParser.START_TAG,"context");
    requireAttrib("name");
    parser.next();
    doPattern();
    require(XmlPullParser.END_TAG,"context");
    parser.nextTag();
  }

  private void doPattern() throws IOException, XmlPullParserException, AimlSyntaxException {
PatternLoop:    
    do {
      switch (parser.getEventType()) {
        case XmlPullParser.START_TAG:
          if (parser.getName().equals("bot"))
            doBotConst();
          else
            throw new AimlSyntaxException("Unexpected start tag '" + parser.getName() + "' while parsing pattern, only 'bot' allowed "+parser.getPositionDescription());
          break;
        case XmlPullParser.END_TAG:
          break PatternLoop;
        case XmlPullParser.TEXT:
          //append text to pattern
          parser.next();
          break;
        case XmlPullParser.END_DOCUMENT:
          throw new AimlSyntaxException("Unexpected end of document while parsing pattern "+parser.getPositionDescription());
        default:
          throw new IllegalStateException("Something really weird happened while parsing pattern "+parser.getPositionDescription());
      }
    } while (true);   
  }


  private String doBotConst() throws IOException, XmlPullParserException, AimlSyntaxException {
    require(XmlPullParser.START_TAG,"bot");
    if (!parser.isEmptyElementTag())
      throw new AimlSyntaxException("Syntax error while parsing bot constant in pattern: element must be empty "+parser.getPositionDescription());
    String name = requireAttrib("name");
    String result;
    try {
      result = bot.getConstant(name);
    } catch (InvalidConstantException e) {
      throw new AimlSyntaxException("Syntax error: "+ e.getMessage() + " " + parser.getPositionDescription());
    }
    parser.nextTag();
    parser.next();
    return result;
  }

  private void doThatC() throws IOException, XmlPullParserException, AimlSyntaxException {
    require(XmlPullParser.START_TAG,"that");
    parser.next();
    doPattern();
    require(XmlPullParser.END_TAG,"that");
    parser.nextTag();    
  }

  private void doPatternC() throws IOException, XmlPullParserException, AimlSyntaxException {
    require(XmlPullParser.START_TAG,"pattern");
    parser.next();
    doPattern();
    require(XmlPullParser.END_TAG,"pattern");
    parser.nextTag();    
  }

  public void load(Reader in) throws IOException, XmlPullParserException, AimlParserException {
    parser.setInput(in);
    doAiml();
  }

  public void load(InputStream in, String encoding) throws IOException, XmlPullParserException, AimlParserException {
    parser.setInput(in,encoding);
    doAiml();
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
  
  public static Test suite() throws XmlPullParserException {
    Bot b = new Bot("foobar");
    b.setConstant("name","foobar");
    b.setConstant("baz","bar");
    AIMLParser ap= new AIMLParser(b);
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
  public static void main(String[] args) throws XmlPullParserException {
    TestRunner.run(AIMLParser.suite());    
  }

}
