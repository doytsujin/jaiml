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

package aiml.substitutions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * This class represents a single list of substitutions that can be applied to a
 * string.
 * </p>
 * 
 * <p>
 * TODO: Test the effectiveness of this implementation.
 * </p>
 * 
 * @author Kim Sullivan
 * 
 */
public class Substitutions {
  private LinkedHashMap<String, String> substitutions = new LinkedHashMap<String, String>();
  private ArrayList<String> replacements = new ArrayList<String>();
  private Pattern regexPattern;

  public Substitutions(LinkedHashMap<String, String> substitutions)
      throws DuplicateSubstitutionException {
    for (Entry<String, String> substitution : substitutions.entrySet()) {
      add(substitution.getKey(), substitution.getValue());
    }
    if (!substitutions.isEmpty()) {
      regexPattern = Pattern.compile(makePattern(), Pattern.UNICODE_CASE |
          Pattern.CASE_INSENSITIVE);
    }
  }

  /**
   * Add a substitution
   * 
   * @param pattern
   * @param replacement
   * @throws DuplicateSubstitutionException
   */
  private void add(String pattern, String replacement)
      throws DuplicateSubstitutionException {
    if (substitutions.containsKey(pattern)) {
      throw new DuplicateSubstitutionException(pattern,
          substitutions.get(pattern));
    }
    substitutions.put(pattern, replacement);
    replacements.add(replacement);
  }

  /**
   * Apply the substitutions in the list to a string
   * 
   * @param s
   * @return
   */
  public String apply(String s) {
    if (regexPattern == null) {
      return s;
    }
    Matcher m = regexPattern.matcher(s);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb, getReplacement(m));
    }
    m.appendTail(sb);
    return sb.toString();
  }

  private String getReplacement(Matcher m) {
    for (int i = 1; i < m.groupCount() + 1; i++) {
      if (m.group(i) != null) {
        return replacements.get(i - 1);
      }
    }
    assert false : "If a matcher has matched, then at least one of the groups must be non-null";
    return null;
  }

  private String makePattern() {
    StringBuilder sb = new StringBuilder();
    for (String pattern : substitutions.keySet()) {
      sb.append('(').append(pattern).append(")|");
    }
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

}
