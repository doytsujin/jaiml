/*
    jaiml - java AIML library
    Copyright (C) 2004, 2009  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package aiml.classifier;

import junit.framework.TestCase;

public class PatternTest extends TestCase {

  public void testThisWordEnd() {
    //0123456789012345
    //TEST * PATTERN *
    assertEquals(0, Pattern.thisWordEnd(0, ""));
    assertEquals(4, Pattern.thisWordEnd(0, "TEST * PATTERN *"));
    assertEquals(6, Pattern.thisWordEnd(5, "TEST * PATTERN *"));
    assertEquals(14, Pattern.thisWordEnd(7, "TEST * PATTERN *"));
    assertEquals(16, Pattern.thisWordEnd(15, "TEST * PATTERN *"));
  }

  public void testNextWord() {
    //0123456789012345
    //TEST * PATTERN *
    assertEquals(0, Pattern.nextWord(0, ""));

    assertEquals(5, Pattern.nextWord(0, "TEST * PATTERN *"));
    assertEquals(5, Pattern.nextWord(4, "TEST * PATTERN *"));

    assertEquals(7, Pattern.nextWord(5, "TEST * PATTERN *"));
    assertEquals(7, Pattern.nextWord(6, "TEST * PATTERN *"));

    assertEquals(15, Pattern.nextWord(7, "TEST * PATTERN *"));
    assertEquals(15, Pattern.nextWord(14, "TEST * PATTERN *"));

    assertEquals(16, Pattern.nextWord(15, "TEST * PATTERN *"));

  }

}
