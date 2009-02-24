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
package aiml.parser;

import java.io.StringReader;

import junit.framework.TestCase;

import org.xmlpull.v1.XmlPullParser;

public class XmlParserTest extends TestCase {
  private XmlParser pp;

  public static void main(String[] args) {
    junit.textui.TestRunner.run(XmlParserTest.class);
  }

  protected void setUp() throws Exception {
    super.setUp();
    pp = new XmlParser();

  }

  public void testEmptyStartElement() throws Exception {
    pp.setInput(new StringReader("<root><test/>foo</root>"));
    assertEquals(XmlPullParser.START_TAG, pp.nextToken());

    assertEquals(XmlPullParser.START_TAG, pp.nextToken());
    assertEquals(XmlPullParser.START_TAG, pp.getEventType());
    assertEquals(true, pp.isEmptyElementTag());

    assertEquals(XmlPullParser.END_TAG, pp.nextToken());
    assertEquals(XmlPullParser.END_TAG, pp.getEventType());

    assertEquals(XmlPullParser.TEXT, pp.nextToken());
    assertEquals(XmlPullParser.TEXT, pp.getEventType());
    assertEquals("foo", pp.getText());

    assertEquals(XmlPullParser.END_TAG, pp.nextToken());
    assertEquals(XmlPullParser.END_TAG, pp.getEventType());
  }

  public void testEmptyRootElement() throws Exception {
    pp.setInput(new StringReader("<root/><!--some comment-->"));
    assertEquals(XmlPullParser.START_TAG, pp.nextToken());
    assertEquals(XmlPullParser.START_TAG, pp.getEventType());
    assertEquals(true, pp.isEmptyElementTag());
    assertEquals(1, pp.getDepth());

    assertEquals(XmlPullParser.END_TAG, pp.nextToken());
    assertEquals(XmlPullParser.END_TAG, pp.getEventType());
    assertEquals(1, pp.getDepth());

    assertEquals(XmlPullParser.COMMENT, pp.nextToken());
    assertEquals(XmlPullParser.COMMENT, pp.getEventType());
    assertEquals(0, pp.getDepth());

  }

  public void testSetInputToNullReader() throws Exception {
    pp.setInput(null);
  }

  public void testSetInputToNullStream() throws Exception {
    try {
      pp.setInput(null, null);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
    }

  }

  public void testEntityName() throws Exception {
    pp.setInput(new StringReader("<foo>&gt;</foo>"));

    assertEquals(XmlPullParser.START_TAG, pp.nextToken());
    assertEquals("foo", pp.getName());

    assertEquals(XmlPullParser.ENTITY_REF, pp.nextToken());
    assertEquals("gt", pp.getName());
    assertEquals(">", pp.getText());

    assertEquals(XmlPullParser.END_TAG, pp.nextToken());
    assertEquals("foo", pp.getName());

    assertEquals(XmlPullParser.END_DOCUMENT, pp.nextToken());

  }
}
