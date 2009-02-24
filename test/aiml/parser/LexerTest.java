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

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import aiml.parser.XmlParser.InternalState;
import aiml.parser.XmlParser.XmlParserPrivateAcessor;

public class LexerTest extends TestCase {
  private XmlParser parser;
  private XmlParserPrivateAcessor priv;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    this.parser = new XmlParser();
    this.priv = parser.getPrivateAccessor();
  }

  public void testNextReference() throws IOException, XmlPullParserException {
    parser.setInput(new StringReader("&fooBar;&#64;&lt;&amp;"));
    priv.nextChar();
    assertEquals(null, priv.nextReference());
    assertEquals(new String(Character.toChars(64)), priv.nextReference());
    assertEquals("<", priv.nextReference());
    assertEquals("&", priv.nextReference());
  }

  private void assertAttribute(int i, String name, String value) {
    assertEquals(name, parser.getAttributeName(i));
    assertEquals(value, parser.getAttributeValue(i));
  }

  public void testNextAttribute() throws IOException, XmlPullParserException {
    parser.setInput(new StringReader(
        "ap:kf  =   \n \r\n \"foo\r\n\n\r&amp;'xxx\"foofoo='wtf'"));
    priv.nextChar();
    priv.setEventType(XmlPullParser.START_TAG);
    priv.nextAttribute();
    priv.nextAttribute();
    assertEquals(2, parser.getAttributeCount());
    assertAttribute(0, "ap:kf", "foo   &'xxx");
    assertAttribute(1, "foofoo", "wtf");
  }

  public void testPIContent() throws IOException, XmlPullParserException {
    parser.setInput(new StringReader(
        "?> bla??? >>>>??? ? > ?hblah?>ffrrfraaafhr-->-->-aasdfasdf-asdfsad-asdfa->-asfd-->adasdf--asdf-->asdfasdf--->"));
    priv.nextChar();
    assertEquals("", priv.nextPIContent());
    assertEquals(" bla??? >>>>??? ? > ?hblah", priv.nextPIContent());
    try {
      priv.nextPIContent();
      fail("Expected XmlPullParserException");
    } catch (XmlPullParserException e) {
      assertTrue(true);
    }
  }

  public void testCommentContent() throws IOException, XmlPullParserException {
    parser.setInput(new StringReader(
        "ffrrfraaafhr-->-->-aasdfasdf-asdfsad-asdfa->-asfd-->adasdf--asdf-->asdfasdf--->"));
    priv.nextChar();
    assertEquals("ffrrfraaafhr", priv.nextCommentContent());
    assertEquals("", priv.nextCommentContent());
    assertEquals("-aasdfasdf-asdfsad-asdfa->-asfd", priv.nextCommentContent());
    try {
      priv.nextCommentContent();
      fail("Expected XmlPullParserException");
    } catch (XmlPullParserException e) {
      assertTrue(true);
    }
    assertEquals("asdf", priv.nextCommentContent());
    try {
      System.out.println(priv.nextCommentContent());
      fail("Expected XmlPullParserException");
    } catch (XmlPullParserException e) {
      assertTrue(true);
    }
  }

  public void testCDataContent() throws IOException, XmlPullParserException {
    parser.setInput(new StringReader(
        "]]12]3]4]]]]5]]6]]7]]] >]]]8]>9012>>>>>]>]>]>]]b]]>"));
    priv.nextChar();
    assertEquals("]]12]3]4]]]]5]]6]]7]]] >]]]8]>9012>>>>>]>]>]>]]b",
        priv.nextCDataContent());
  }

  public void testCharData() throws IOException, XmlPullParserException {
    parser.setInput(new StringReader(
        "asdfasdfjh<skdjfhaskdjfh&askjfh<]]12]3]4]]]]5]]6]]7]]] >]]]8]>9012>>>>>]>]>]>]]b<]]12]3]4]]]]5]]6]]7]]] >]]]8]>9012>>>>>]>]>]>]]b]]>asdf]]>asdf"));
    assertCData('a', "asdfasdfjh", '<');

    assertCData('s', "skdjfhaskdjfh", '&');

    assertCData('a', "askjfh", '<');

    assertCData(']', "]]12]3]4]]]]5]]6]]7]]] >]]]8]>9012>>>>>]>]>]>]]b", '<');

    assertEquals(']', priv.nextChar());
    try {
      priv.nextCharData();
      fail("Expected XmlPullParserException");
    } catch (XmlPullParserException e) {
      assertTrue(true);
    }
    assertEquals('>', priv.getChar());

    assertEquals(priv.nextChar(), 'a');
    try {
      priv.nextCharData();
      fail("Expected XmlPullParserException");
    } catch (XmlPullParserException e) {
      assertTrue(true);
    }
  }

  private void assertCData(char firstChar, String data, char next)
      throws IOException, XmlPullParserException {
    assertEquals(firstChar, priv.nextChar());
    assertEquals(data, priv.nextCharData());
    assertEquals(next, priv.getChar());
  }

  public void testStartTagContent() throws IOException, XmlPullParserException {
    parser.setInput(new StringReader(
        " foo='bar' bar='foo'> a='b' c='d'   > u='1' v='2'/>"));
    assertEquals(priv.nextChar(), ' ');
    priv.nextStartTagContent();
    assertEquals(2, parser.getAttributeCount());
    assertEquals(false, parser.isEmptyElementTag());
    assertAttribute(0, "foo", "bar");
    assertAttribute(1, "bar", "foo");
    try {
      parser.getAttributeName(2);
      fail("Expected IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    }
    assertEquals(priv.getChar(), ' ');

    priv.nextStartTagContent();
    assertEquals(parser.getAttributeCount(), 2);
    assertEquals(parser.isEmptyElementTag(), false);
    assertAttribute(0, "a", "b");
    assertAttribute(1, "c", "d");
    try {
      parser.getAttributeValue(2);
      fail("Expcected IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    }
    assertEquals(priv.getChar(), ' ');

    priv.nextStartTagContent();
    assertEquals(parser.getAttributeCount(), 2);
    assertEquals(parser.isEmptyElementTag(), true);
    assertAttribute(0, "u", "1");
    assertAttribute(1, "v", "2");
    try {
      parser.getAttributeValue(2);
      fail("Expcected IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    }
    assertEquals(XmlParser.EOF, priv.getChar());

  }

  public void testMarkupContentComment() throws IOException,
      XmlPullParserException {
    parser.setInput(new StringReader("!--foobar-->"));
    assertEquals(XmlPullParser.START_DOCUMENT, parser.getEventType());
    assertEquals('!', priv.nextChar());
    priv.nextMarkupContent();
    assertEquals(XmlPullParser.COMMENT, parser.getEventType());
    assertEquals("foobar", parser.getText());
    assertEquals(XmlParser.EOF, priv.getChar());
  }

  public void testMarkupContentCommentEmpty() throws IOException,
      XmlPullParserException {
    parser.setInput(new StringReader("!---->"));
    assertEquals(XmlPullParser.START_DOCUMENT, parser.getEventType());
    assertEquals('!', priv.nextChar());
    priv.nextMarkupContent();
    assertEquals(XmlPullParser.COMMENT, parser.getEventType());
    assertEquals("", parser.getText());
    assertEquals(XmlParser.EOF, priv.getChar());
  }

  public void testMarkupContentCommentError() throws IOException,
      XmlPullParserException {
    parser.setInput(new StringReader("!-foo-->"));
    assertEquals(XmlPullParser.START_DOCUMENT, parser.getEventType());
    assertEquals('!', priv.nextChar());
    try {
      priv.nextMarkupContent();
      fail("Expected XmlPullParserException");
    } catch (XmlPullParserException e) {
      assertTrue(true);
    }
  }

  public void testMarkupContentPI() throws Exception {
    parser.setInput(new StringReader("?php echo('j00 fail')?>"));
    assertEquals('?', priv.nextChar());
    assertEquals(XmlPullParser.START_DOCUMENT, parser.getEventType());
    priv.nextMarkupContent();
    assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.getEventType());
    assertEquals("php echo('j00 fail')", parser.getText());
    assertEquals(XmlParser.EOF, priv.getChar());
  }

  public void testMarkupContentPIXmlDecl() throws Exception {
    parser.setInput(new StringReader("?xml version='1.0'?>"));
    assertEquals('?', priv.nextChar());
    assertEquals(XmlPullParser.START_DOCUMENT, parser.getEventType());
    try {
      priv.nextMarkupContent();
      fail("Expected XmlPullParserException");
    } catch (XmlPullParserException e) {

    }

  }

  public void testMarkupContentCDSect() throws Exception {
    parser.setInput(new StringReader(
        "![CDATA[<this> will be &ignored;]]<><!---->]]>"));
    assertEquals('!', priv.nextChar());
    assertEquals(XmlPullParser.START_DOCUMENT, parser.getEventType());
    priv.nextMarkupContent();
    assertEquals(XmlPullParser.CDSECT, parser.getEventType());
    assertEquals("<this> will be &ignored;]]<><!---->", parser.getText());
    assertEquals(XmlParser.EOF, priv.getChar());
  }

  public void testMarkupContentDoctype() throws Exception {
    parser.setInput(new StringReader("!DOCTYPE [<!ELEMENT ]>"));
    assertEquals('!', priv.nextChar());
    assertEquals(XmlPullParser.START_DOCUMENT, parser.getEventType());
    priv.nextMarkupContent();
    assertEquals(XmlPullParser.DOCDECL, parser.getEventType());
  }

  public void testMarkupContentMarkedSectionError() throws Exception {
    parser.setInput(new StringReader("![RCDSECT[some RCDATA]]>"));
    assertEquals('!', priv.nextChar());
    assertEquals(XmlPullParser.START_DOCUMENT, parser.getEventType());
    try {
      priv.nextMarkupContent();
      fail("Expected XmlPullParserException");
    } catch (XmlPullParserException e) {
      assertTrue(true);
    }
  }

  public void testMarkupContentInvalidCharAfterExcl() throws Exception {
    parser.setInput(new StringReader("!]something"));
    assertEquals('!', priv.nextChar());
    assertEquals(XmlPullParser.START_DOCUMENT, parser.getEventType());
    try {
      priv.nextMarkupContent();
      fail("Expected XmlPullParserException");
    } catch (XmlPullParserException e) {
      assertTrue(true);
    }
  }

  public void testMarkupContentEndTag() throws Exception {
    parser.setInput(new StringReader("/endtag>"));
    assertEquals('/', priv.nextChar());
    assertEquals(XmlPullParser.START_DOCUMENT, parser.getEventType());
    priv.nextMarkupContent();
    assertEquals(XmlPullParser.END_TAG, parser.getEventType());
    assertEquals(null, parser.getText());
    assertEquals("endtag", parser.getName());
    assertEquals(XmlParser.EOF, priv.getChar());
  }

  public void testMarkupContentEndTagWithSpaces() throws Exception {
    parser.setInput(new StringReader("/endtag   >"));
    assertEquals('/', priv.nextChar());
    assertEquals(XmlPullParser.START_DOCUMENT, parser.getEventType());
    priv.nextMarkupContent();
    assertEquals(XmlPullParser.END_TAG, parser.getEventType());
    assertEquals(null, parser.getText());
    assertEquals("endtag", parser.getName());
    assertEquals(XmlParser.EOF, priv.getChar());
  }

  public void testMarkupContentEndTagMalformed() throws Exception {
    parser.setInput(new StringReader("/ endtag   >"));
    assertEquals('/', priv.nextChar());
    assertEquals(XmlPullParser.START_DOCUMENT, parser.getEventType());
    try {
      priv.nextMarkupContent();
      fail("Expected XmlPullParserException");
    } catch (XmlPullParserException e) {
      assertTrue(true);
    }

    parser.setInput(new StringReader("/endtag s>"));
    assertEquals('/', priv.nextChar());
    assertEquals(XmlPullParser.START_DOCUMENT, parser.getEventType());
    try {
      priv.nextMarkupContent();
      fail("Expected XmlPullParserException");
    } catch (XmlPullParserException e) {
      assertTrue(true);
    }

  }

  public void testXmlDeclVersion() throws Exception {
    parser.setInput(new StringReader(
        "<?xml version='1.0' encoding='windows-1250' standalone='yes'?>"));
    assertEquals('<', priv.nextChar());
    priv.tryXmlDecl();
    assertTrue("xmlDeclParsed", priv.isXmlDeclParsed());
    assertTrue("isStandalone", priv.isStandalone());
    assertEquals("windows-1250", priv.getEncodingDeclared());
  }

  public void testNextToken() throws Exception {
    parser.setInput(new StringReader(
        "<foo>some mixed content<bar>foo&amp;bar</bar></foo>"));
    assertEquals(XmlPullParser.START_DOCUMENT, parser.getEventType());
    assertEquals("depth", 0, parser.getDepth());
    assertEquals("internal state", InternalState.DOCUMENT_START,
        priv.getInternalState());

    assertEquals("start tag nt", XmlPullParser.START_TAG, parser.nextToken());
    assertEquals("start tag et", XmlPullParser.START_TAG, parser.getEventType());
    assertEquals("depth 1", 1, parser.getDepth());
    assertEquals("foo", parser.getName());

    assertEquals("text nt", XmlPullParser.TEXT, parser.nextToken());
    assertEquals("text et", XmlPullParser.TEXT, parser.getEventType());
    assertEquals("depth 1", 1, parser.getDepth());
    assertEquals("some mixed content", parser.getText());
    assertEquals("internal state", InternalState.CONTENT,
        priv.getInternalState());

    assertEquals("start tag nt", XmlPullParser.START_TAG, parser.nextToken());
    assertEquals("start tag et", XmlPullParser.START_TAG, parser.getEventType());
    assertEquals("depth 2", 2, parser.getDepth());
    assertEquals("bar", parser.getName());
    assertEquals("internal state", InternalState.CONTENT,
        priv.getInternalState());

    assertEquals("text nt", XmlPullParser.TEXT, parser.nextToken());
    assertEquals("text et", XmlPullParser.TEXT, parser.getEventType());
    assertEquals("depth 2", 2, parser.getDepth());
    assertEquals("foo", parser.getText());
    assertEquals("internal state", InternalState.CONTENT,
        priv.getInternalState());

    assertEquals("entity nt", XmlPullParser.ENTITY_REF, parser.nextToken());
    assertEquals("entity et", XmlPullParser.ENTITY_REF, parser.getEventType());
    assertEquals("depth 2", 2, parser.getDepth());
    assertEquals("&", parser.getText());
    assertEquals("internal state", InternalState.CONTENT,
        priv.getInternalState());

    assertEquals("text nt", XmlPullParser.TEXT, parser.nextToken());
    assertEquals("text et", XmlPullParser.TEXT, parser.getEventType());
    assertEquals("depth 2", 2, parser.getDepth());
    assertEquals("bar", parser.getText());
    assertEquals("internal state", InternalState.CONTENT,
        priv.getInternalState());

    assertEquals("end tag nt", XmlPullParser.END_TAG, parser.nextToken());
    assertEquals("end tag et", XmlPullParser.END_TAG, parser.getEventType());
    assertEquals("depth 2", 2, parser.getDepth());
    assertEquals("bar", parser.getName());
    assertEquals("internal state", InternalState.CONTENT,
        priv.getInternalState());

    assertEquals("end tag nt", XmlPullParser.END_TAG, parser.nextToken());
    assertEquals("end tag et", XmlPullParser.END_TAG, parser.getEventType());
    assertEquals("depth 1", 1, parser.getDepth());
    assertEquals("foo", parser.getName());
    assertEquals("internal state", InternalState.EPILOG,
        priv.getInternalState());

    assertEquals("end tag nt", XmlPullParser.END_DOCUMENT, parser.nextToken());
    assertEquals("end tag et", XmlPullParser.END_DOCUMENT, parser.getEventType());
    assertEquals("depth 0", 0, parser.getDepth());
    assertEquals("internal state", InternalState.DOCUMENT_END,
        priv.getInternalState());

    assertEquals("end tag nt", XmlPullParser.END_DOCUMENT, parser.nextToken());
    assertEquals("end tag et", XmlPullParser.END_DOCUMENT, parser.getEventType());
    assertEquals("depth 0", 0, parser.getDepth());
    assertEquals("internal state", InternalState.DOCUMENT_END,
        priv.getInternalState());

  }

  public void testChardataWhitespace() throws Exception {
    parser.setInput(new StringReader("    <   s  <   ><"));
    priv.setEventType(XmlPullParser.TEXT);

    priv.nextChar();
    priv.nextCharData();
    assertTrue(parser.isWhitespace());

    priv.nextChar();
    priv.nextCharData();
    assertFalse(parser.isWhitespace());

    priv.nextChar();
    priv.nextCharData();
    assertFalse(parser.isWhitespace());
  }

  public void testCDataWhitespace() throws Exception {
    parser.setInput(new StringReader("    ]]>   ] ]]>   ]] ]]>"));
    priv.setEventType(XmlPullParser.CDSECT);

    priv.nextChar();
    priv.nextCDataContent();
    assertTrue(parser.isWhitespace());

    priv.nextChar();
    priv.nextCDataContent();
    assertFalse(parser.isWhitespace());

    priv.nextChar();
    priv.nextCDataContent();
    assertFalse(parser.isWhitespace());
  }
}