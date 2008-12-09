/*
    jaiml - java AIML library
    Copyright (C) 2004-2008  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package aiml.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class CheckingParserTest extends TestCase {

  private CheckingParser parser;

  protected void setUp() throws Exception {
    parser = new CheckingParser(
        XmlPullParserFactory.newInstance().newPullParser(),
        AimlParserException.class);
  }

  private String elements(String... names) {
    List<String> elements = Arrays.asList(names);
    Collections.shuffle(elements);
    StringBuilder sb = new StringBuilder();
    for (String element : elements) {
      sb.append('<').append(element).append("/>");
    }

    return sb.toString();
  }

  public void testRequireIntStringArray2() throws XmlPullParserException,
      IOException, AimlParserException {
    StringBuilder sb = new StringBuilder();
    sb.append("<root>");
    String eNames[] = { "a", "b" };
    sb.append(elements(eNames));
    sb.append("</root>");
    parser.setInput(new StringReader(sb.toString()));
    parser.nextTag();
    parser.require(XmlPullParser.START_TAG);
    try {
      parser.require(XmlPullParser.END_TAG);
      fail("Expected AimlParserException");
    } catch (AimlParserException e) {
      //ok
    }
    parser.require(XmlPullParser.START_TAG, "root");
    try {
      parser.require(XmlPullParser.START_TAG, "foo");
      fail("Expected AimlParserException");
    } catch (AimlParserException e) {
      //ok
    }

    for (int i = 0; i < eNames.length; i++) {
      parser.nextTag();
      try {
        parser.require(XmlPullParser.START_TAG, "a", "b"); //wrong wa to do it!
        fail("Expected XmlPullParserException");
      } catch (XmlPullParserException e) {
        //ok
      }
      parser.require(XmlPullParser.START_TAG, new String[] { "a", "b" }); // right way to do it

      try {
        parser.require(XmlPullParser.START_TAG, new String[] { "x", "y" });
        fail("Expected AimlParserException");
      } catch (AimlParserException e) {
        //ok
      }
      parser.nextTag();
      parser.require(XmlPullParser.END_TAG, new String[] { "a", "b" });
    }
    parser.nextTag();
    parser.require(XmlPullParser.END_TAG, "root");

  }

  public void testRequireIntStringArray5() throws XmlPullParserException,
      IOException, AimlParserException {
    StringBuilder sb = new StringBuilder();
    sb.append("<root>");
    String eNames[] = { "a", "b", "c", "d" };
    sb.append(elements(eNames));
    sb.append("</root>");
    parser.setInput(new StringReader(sb.toString()));
    parser.nextTag();
    parser.require(XmlPullParser.START_TAG);
    try {
      parser.require(XmlPullParser.END_TAG);
      fail("Expected AimlParserException");
    } catch (AimlParserException e) {
      //ok
    }
    parser.require(XmlPullParser.START_TAG, "root");
    try {
      parser.require(XmlPullParser.START_TAG, "foo");
      fail("Expected AimlParserException");
    } catch (AimlParserException e) {
      //ok
    }

    for (int i = 0; i < eNames.length; i++) {
      parser.nextTag();
      parser.require(XmlPullParser.START_TAG, eNames);
      parser.nextTag();
      parser.require(XmlPullParser.END_TAG, eNames);
    }
    parser.nextTag();
    parser.require(XmlPullParser.END_TAG, "root");

  }

  public void testRequireAttrib() throws XmlPullParserException, IOException,
      AimlParserException {
    parser.setInput(new StringReader("<root><tag foo=\"bar\"/></root>"));
    parser.nextTag();
    try {
      parser.requireAttrib("foo");
      fail("Expected AimlParserException");
    } catch (AimlParserException e) {
      //ok
    }
    parser.nextTag();
    assertEquals("bar", parser.requireAttrib("foo"));

  }

  public void testIsEvent() throws XmlPullParserException, IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("<root>");
    String eNames[] = { "a", "b", "c", "d" };
    sb.append(elements(eNames));
    sb.append("</root>");
    parser.setInput(new StringReader(sb.toString()));
    assertTrue(parser.isEvent(XmlPullParser.START_DOCUMENT));
    parser.nextTag();
    assertTrue(parser.isEvent(XmlPullParser.START_TAG));
    assertTrue(parser.isEvent(XmlPullParser.START_TAG, "root"));
    assertFalse(parser.isEvent(XmlPullParser.END_TAG));
    assertFalse(parser.isEvent(XmlPullParser.START_TAG, "foobar"));
    for (int i = 0; i < eNames.length; i++) {
      parser.nextTag();
      assertTrue(parser.isEvent(XmlPullParser.START_TAG, eNames));
      parser.nextTag();
      assertTrue(parser.isEvent(XmlPullParser.END_TAG, eNames));
    }
    parser.nextTag();
    assertTrue(parser.isEvent(XmlPullParser.END_TAG));
    assertTrue(parser.isEvent(XmlPullParser.END_TAG, "root"));
    parser.next();
    parser.isEvent(XmlPullParser.END_DOCUMENT);

  }

}
