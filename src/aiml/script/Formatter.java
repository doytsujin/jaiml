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

package aiml.script;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {
  public static String tab(int length) {
    StringBuffer b = new StringBuffer();
    for (int i = 0; i < length; i++)
      b.append("  ");
    return b.toString();
  }

  /**
   * <p>
   * Returns a string with all characters converted to lowercase
   * </p>
   * <p>
   * Equivalent to {@link String.toLowerCase()}
   * </p>
   * 
   * @param text -
   *                the original text
   * @return The {@code text}, converted to lowercase
   */
  public static String lowerCase(String text) {
    return text.toLowerCase();
  }

  /**
   * <p>
   * Returns a string with all characters converted to uppercase
   * </p>
   * <p>
   * Equivalent to {@link String.toUpperCase()}
   * </p>
   * 
   * @param text
   * 
   * @return The {@code text}, converted to uppercase
   */
  public static String upperCase(String text) {
    return text.toUpperCase();
  }

  /**
   * <p>
   * Returns a string in so called "formal case", so that the first letter of
   * each word is in uppercase.
   * </p>
   * 
   * @param text -
   *                the original string
   * @return A string where each word starts with upper case.
   */
  public static String formal(String text) {
    Pattern p = Pattern.compile("(?:^|\\p{javaWhitespace})(\\p{javaLowerCase})");
    Matcher m = p.matcher(text);
    StringBuffer result = new StringBuffer();

    while (m.find()) {
      m.appendReplacement(result, m.group().toUpperCase());
    }
    m.appendTail(result);
    return result.toString();
  }

  /**
   * <p>
   * Transforms a string to "sentence case" - the first letter of each sentence
   * is in uppercase. Unfortunately, the problem of sentence boundary
   * disambiguation in natural languages is nontrivial, so only a very simple
   * algorithm is employed:
   * </p>
   * <ul>
   * <li>Sentences are interpreted as strings whose last character is the
   * period or full-stop character ".", followed by whitespace (or the end of
   * the string)
   * <li>If the string does not contain a ".", then the entire string is
   * treated as a sentence and the first letter is capitalized.
   * <li>If the string contains several sentences but the last sentence is not
   * at the end, the rest of the string is capitalized as if it was a full
   * sentence.
   * </ul>
   * 
   * @param text
   * @return
   */
  public static String sentence(String text) {
    Pattern p = Pattern.compile("(?:^|\\p{javaWhitespace}+)(\\p{L}).*?\\.(?=^|\\p{javaWhitespace}+)");
    Matcher m = p.matcher(text);
    StringBuffer result = new StringBuffer();

    while (m.find()) {
      m.appendReplacement(result, m.group());
      upCaseCodePoint(result, m.start(1));
    }

    int start = result.length();
    m.appendTail(result);
    CharSequence tail = result.subSequence(start, result.length());
    Matcher m2 = Pattern.compile("(?:^|\\p{javaWhitespace}*)(\\p{L})").matcher(
        tail);

    if (m2.find()) {
      upCaseCodePoint(result, m2.start(1) + start);
    }
    return result.toString();
  }

  /**
   * <p>
   * Changes the unicode character on position <code>pos</code> inside the
   * StringBuffer buffer to upper case.
   * </p>
   * 
   * @param buffer
   *                A modifiable string
   * @param pos
   *                The position of the character to turn to upper case.
   */
  private static void upCaseCodePoint(StringBuffer buffer, int pos) {
    int codePoint = buffer.codePointAt(pos);
    codePoint = Character.toUpperCase(codePoint);

    String replacementChar = new String(Character.toChars(codePoint));
    buffer.replace(pos, buffer.offsetByCodePoints(pos, 1), replacementChar);
  }

  /**
   * <p>
   * Removes leading and trailing whitespace and collapses all whitespace in
   * between to a single space
   * </p>
   * 
   * @param s
   *                A string
   * @return A string with all whitespace collapsed.
   */
  public static String collapseWhitespace(String s) {
    StringBuilder result = new StringBuilder();
    int pos = 0;
    //skip leading whitespace
    while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
      pos++;
    }

    while (pos < s.length()) {
      //output characters 
      while (pos < s.length() && (!Character.isWhitespace(s.charAt(pos)))) {
        result.append(s.charAt(pos++));
      }
      //skip whitespace
      while (pos < s.length() && (Character.isWhitespace(s.charAt(pos)))) {
        pos++;
      }
      //if there are still some characters left, write out a single space
      if (pos < s.length()) {
        result.append(' ');
      }
    }

    return result.toString();
  }

  public static void main(String[] args) {
    System.out.println(collapseWhitespace("   abcdef   "));
  }
}
