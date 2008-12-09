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
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * <p>
 * This class wraps around an XMLPullParser, and adds additional utility methods
 * that simplify parsing code.
 * </p>
 * 
 * <p>
 * This class works with any XmlPullParser implementation - that's also the
 * reason why it's a wrapper, instead of inheriting from an existing class
 * </p>
 * 
 * @author Kim Sullivan
 * 
 */
public class CheckingParser implements XmlPullParser {
  private XmlPullParser parser;
  Constructor<? extends AimlParserException> c;

  private AimlParserException exception(String message) {
    try {
      return c.newInstance(message);
    } catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;

  }

  public CheckingParser(XmlPullParser parser,
      Class<? extends AimlParserException> exception) {
    if (parser == null) {
      throw new IllegalArgumentException("Parser must not be null");
    }
    this.parser = parser;

    try {
      c = exception.getConstructor(String.class);
    } catch (SecurityException e) {
      throw new IllegalArgumentException("Can't throw exceptions", e);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("Can't throw exceptions", e);
    }
  }

  /**
   * @param entityName
   * @param replacementText
   * @throws XmlPullParserException
   * @see org.xmlpull.v1.XmlPullParser#defineEntityReplacementText(java.lang.String,
   *      java.lang.String)
   */
  public void defineEntityReplacementText(String entityName,
      String replacementText) throws XmlPullParserException {
    parser.defineEntityReplacementText(entityName, replacementText);
  }

  /**
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getAttributeCount()
   */
  public int getAttributeCount() {
    return parser.getAttributeCount();
  }

  /**
   * @param index
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getAttributeName(int)
   */
  public String getAttributeName(int index) {
    return parser.getAttributeName(index);
  }

  /**
   * @param index
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getAttributeNamespace(int)
   */
  public String getAttributeNamespace(int index) {
    return parser.getAttributeNamespace(index);
  }

  /**
   * @param index
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getAttributePrefix(int)
   */
  public String getAttributePrefix(int index) {
    return parser.getAttributePrefix(index);
  }

  /**
   * @param index
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getAttributeType(int)
   */
  public String getAttributeType(int index) {
    return parser.getAttributeType(index);
  }

  /**
   * @param index
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getAttributeValue(int)
   */
  public String getAttributeValue(int index) {
    return parser.getAttributeValue(index);
  }

  /**
   * @param namespace
   * @param name
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getAttributeValue(java.lang.String,
   *      java.lang.String)
   */
  public String getAttributeValue(String namespace, String name) {
    return parser.getAttributeValue(namespace, name);
  }

  /**
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getColumnNumber()
   */
  public int getColumnNumber() {
    return parser.getColumnNumber();
  }

  /**
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getDepth()
   */
  public int getDepth() {
    return parser.getDepth();
  }

  /**
   * @return
   * @throws XmlPullParserException
   * @see org.xmlpull.v1.XmlPullParser#getEventType()
   */
  public int getEventType() throws XmlPullParserException {
    return parser.getEventType();
  }

  /**
   * @param name
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getFeature(java.lang.String)
   */
  public boolean getFeature(String name) {
    return parser.getFeature(name);
  }

  /**
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getInputEncoding()
   */
  public String getInputEncoding() {
    return parser.getInputEncoding();
  }

  /**
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getLineNumber()
   */
  public int getLineNumber() {
    return parser.getLineNumber();
  }

  /**
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getName()
   */
  public String getName() {
    return parser.getName();
  }

  /**
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getNamespace()
   */
  public String getNamespace() {
    return parser.getNamespace();
  }

  /**
   * @param prefix
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getNamespace(java.lang.String)
   */
  public String getNamespace(String prefix) {
    return parser.getNamespace(prefix);
  }

  /**
   * @param depth
   * @return
   * @throws XmlPullParserException
   * @see org.xmlpull.v1.XmlPullParser#getNamespaceCount(int)
   */
  public int getNamespaceCount(int depth) throws XmlPullParserException {
    return parser.getNamespaceCount(depth);
  }

  /**
   * @param pos
   * @return
   * @throws XmlPullParserException
   * @see org.xmlpull.v1.XmlPullParser#getNamespacePrefix(int)
   */
  public String getNamespacePrefix(int pos) throws XmlPullParserException {
    return parser.getNamespacePrefix(pos);
  }

  /**
   * @param pos
   * @return
   * @throws XmlPullParserException
   * @see org.xmlpull.v1.XmlPullParser#getNamespaceUri(int)
   */
  public String getNamespaceUri(int pos) throws XmlPullParserException {
    return parser.getNamespaceUri(pos);
  }

  /**
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getPositionDescription()
   */
  public String getPositionDescription() {
    return parser.getPositionDescription();
  }

  /**
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getPrefix()
   */
  public String getPrefix() {
    return parser.getPrefix();
  }

  /**
   * @param name
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getProperty(java.lang.String)
   */
  public Object getProperty(String name) {
    return parser.getProperty(name);
  }

  /**
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getText()
   */
  public String getText() {
    return parser.getText();
  }

  /**
   * @param holderForStartAndLength
   * @return
   * @see org.xmlpull.v1.XmlPullParser#getTextCharacters(int[])
   */
  public char[] getTextCharacters(int[] holderForStartAndLength) {
    return parser.getTextCharacters(holderForStartAndLength);
  }

  /**
   * @param index
   * @return
   * @see org.xmlpull.v1.XmlPullParser#isAttributeDefault(int)
   */
  public boolean isAttributeDefault(int index) {
    return parser.isAttributeDefault(index);
  }

  /**
   * @return
   * @throws XmlPullParserException
   * @see org.xmlpull.v1.XmlPullParser#isEmptyElementTag()
   */
  public boolean isEmptyElementTag() throws XmlPullParserException {
    return parser.isEmptyElementTag();
  }

  /**
   * @return
   * @throws XmlPullParserException
   * @see org.xmlpull.v1.XmlPullParser#isWhitespace()
   */
  public boolean isWhitespace() throws XmlPullParserException {
    return parser.isWhitespace();
  }

  /**
   * @return
   * @throws XmlPullParserException
   * @throws IOException
   * @see org.xmlpull.v1.XmlPullParser#next()
   */
  public int next() throws XmlPullParserException, IOException {
    return parser.next();
  }

  /**
   * @return
   * @throws XmlPullParserException
   * @throws IOException
   * @see org.xmlpull.v1.XmlPullParser#nextTag()
   */
  public int nextTag() throws XmlPullParserException, IOException {
    return parser.nextTag();
  }

  /**
   * @return
   * @throws XmlPullParserException
   * @throws IOException
   * @see org.xmlpull.v1.XmlPullParser#nextText()
   */
  public String nextText() throws XmlPullParserException, IOException {
    return parser.nextText();
  }

  /**
   * @return
   * @throws XmlPullParserException
   * @throws IOException
   * @see org.xmlpull.v1.XmlPullParser#nextToken()
   */
  public int nextToken() throws XmlPullParserException, IOException {
    return parser.nextToken();
  }

  /**
   * @param type
   * @param namespace
   * @param name
   * @throws XmlPullParserException
   * @throws IOException
   * @see org.xmlpull.v1.XmlPullParser#require(int, java.lang.String,
   *      java.lang.String)
   */
  public void require(int type, String namespace, String name)
      throws XmlPullParserException, IOException {
    parser.require(type, namespace, name);
  }

  /**
   * @param name
   * @param state
   * @throws XmlPullParserException
   * @see org.xmlpull.v1.XmlPullParser#setFeature(java.lang.String, boolean)
   */
  public void setFeature(String name, boolean state)
      throws XmlPullParserException {
    parser.setFeature(name, state);
  }

  /**
   * @param inputStream
   * @param inputEncoding
   * @throws XmlPullParserException
   * @see org.xmlpull.v1.XmlPullParser#setInput(java.io.InputStream,
   *      java.lang.String)
   */
  public void setInput(InputStream inputStream, String inputEncoding)
      throws XmlPullParserException {
    parser.setInput(inputStream, inputEncoding);
  }

  /**
   * @param in
   * @throws XmlPullParserException
   * @see org.xmlpull.v1.XmlPullParser#setInput(java.io.Reader)
   */
  public void setInput(Reader in) throws XmlPullParserException {
    parser.setInput(in);
  }

  /**
   * @param name
   * @param value
   * @throws XmlPullParserException
   * @see org.xmlpull.v1.XmlPullParser#setProperty(java.lang.String,
   *      java.lang.Object)
   */
  public void setProperty(String name, Object value)
      throws XmlPullParserException {
    parser.setProperty(name, value);
  }

  void require(String failMessage, int eventType, String name)
      throws XmlPullParserException, AimlParserException {
    if ((getEventType() == eventType && (name == null || getName().equals(name))))
      return;
    else
      throw (AimlParserException) exception(
          "Syntax error: " + failMessage + " " + getPositionDescription()).fillInStackTrace();
  }

  public void require(int eventType, String... names)
      throws XmlPullParserException, AimlParserException {
    if (getEventType() == eventType) {
      if (names == null || names.length == 0) {
        return;
      }
      for (String n : names) {
        if (getName().equals(n)) {
          return;
        }
      }
    }

    throw (AimlParserException) exception(
        "Syntax error: expected " + XmlPullParser.TYPES[eventType] + " '" +
            Arrays.toString(names) + "' " + getPositionDescription()).fillInStackTrace();
  }

  public String requireAttrib(String name) throws AimlParserException {
    if (getAttributeValue(null, name) == null) {
      throw (AimlParserException) exception(
          "Syntax error: mandatory attribute '" + name +
              "' missing from element '" + getName() + "' " +
              getPositionDescription()).fillInStackTrace();
    } else {
      return getAttributeValue(null, name);
    }
  }

  public boolean isEvent(int eventType, String... names)
      throws XmlPullParserException {
    if (getEventType() == eventType) {
      if (names == null || names.length == 0)
        return true;
      for (String name : names) {
        if (getName().equals(name))
          return true;
      }
    }
    return false;
  }
}
