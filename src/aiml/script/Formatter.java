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

class Formatter {
  public static String tab(int length) {
    StringBuffer b = new StringBuffer();
    for (int i = 0; i < length; i++)
      b.append("  ");
    return b.toString();
  }

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

  public static void main(String[] args) {
    System.out.println(sentence("she stopped. she said, \"Hello there,\" and then went (very slowly) on. asalkjdfa.alkjsd was really bad asdf.123 asd313.itu."));
  }
}
