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

package aiml.substitutions;

import java.util.LinkedHashMap;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

public class SubstitutionsTest extends TestCase {
  static public void assertEqualsIgnoreCase(String expected, String actual) {
    assertEqualsIgnoreCase(null, expected, actual);
  }

  static public void assertEqualsIgnoreCase(String message, String expected,
      String actual) {
    if (expected == null && actual == null)
      return;
    if (expected != null && expected.equalsIgnoreCase(actual))
      return;
    throw new ComparisonFailure(message, expected, actual);
  }

  public void testApplyEmpty() throws DuplicateSubstitutionException {
    LinkedHashMap<String, String> substMap = new LinkedHashMap<String, String>();
    Substitutions s;

    s = new Substitutions(substMap);
    assertEqualsIgnoreCase("I'm Here Becasue ov yuo",
        s.apply("I'm Here Becasue ov yuo"));
  }

  public void testApply() throws DuplicateSubstitutionException {
    LinkedHashMap<String, String> substMap = new LinkedHashMap<String, String>();
    Substitutions s;

    substMap.put("\\bbecasue\\b", "because");
    substMap.put("\\bI'm\\b", "I am");
    substMap.put("\\byuo\\b", "you");
    substMap.put("\\bov\\b", "of");
    s = new Substitutions(substMap);

    assertEqualsIgnoreCase("I am here because of you",
        s.apply("I'm Here Becasue ov yuo"));

  }

}
