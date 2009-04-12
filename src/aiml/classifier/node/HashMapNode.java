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

package aiml.classifier.node;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import aiml.classifier.Classifier;
import aiml.classifier.MatchState;
import aiml.classifier.Pattern;
import aiml.classifier.PatternContextNode;

/**
 * <p>
 * This very simple implementation of a Pattern node is backed by a map where
 * all the patterns are stored. It does not perform any path compaction or
 * wildcard matching (apart from being case-insensitive).
 * </p>
 * <p>
 * It consumes the whole rest of the pattern, and so has to be used in
 * conjunction with a "terminal" node like EndOfStringNode.
 * </p>
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public class HashMapNode extends PatternNode {
  private HashMap<String, PatternNode> map = new HashMap<String, PatternNode>();

  /**
   * Creates a new hash map pattern node
   */
  public HashMapNode(PatternContextNode parent) {
    super(parent);
  }

  /**
   * Adds the rest of the pattern to a hashmap.
   * 
   * @param depth
   *          int
   * @param pattern
   *          String
   * @return AddResult
   */
  public AddResult add(int depth, String pattern) {
    AddResult result;
    PatternNodeFactory patternNodeFactory = parentContext.getPNF();
    pattern = Pattern.normalize(pattern.substring(depth));

    PatternNode node = map.get(pattern);
    depth += pattern.length();
    if (node == null) {
      node = patternNodeFactory.getInstance(parentContext, depth, pattern);
    }
    result = node.add(depth, pattern);
    map.put(pattern, result.root);

    result.root = this;
    return result;

  }

  /**
   * Matches the current context value starting at the depth specified in the
   * match state. This method preforms a simple map lookup to determine a match,
   * no wildcards are processed.
   * 
   * @param match
   *          the match state
   * @return <code>true</code> if the match was successful; <code>false</code>
   *         if not
   */
  public boolean match(MatchState match) {
    //Match
    String s = Pattern.normalize(match.getContextValue().substring(match.depth));
    PatternNode node = map.get(s);
    //match is "done" check result:
    if (node != null) {
      match.depth += s.length();
      if (node.match(match)) {
        return true;
      } else {
        //restore the previous match state
        match.depth -= s.length();
        return false;
      }
    } else {
      return false;
    }
  }

  public String toString() {
    Iterator i = map.entrySet().iterator();
    StringBuffer result = new StringBuffer();
    while (i.hasNext()) {
      Map.Entry me = (Map.Entry) i.next();
      PatternNode subnode = (PatternNode) me.getValue();
      result.append("[" + me.getKey() + "]" + subnode + "\n");

    }
    return result.toString();
  }

  /**
   * Register this node type in PatternNodeFactory.
   * 
   * @param classifier
   *          TODO
   */
  public static void register(Classifier classifier) {
    PatternNodeFactory patternNodeFactory = classifier.getPNF();
    patternNodeFactory.registerNode(new Creatable() {
      public boolean canCreate(int depth, String pattern) {
        return (depth != pattern.length());
      }

      public PatternNode getInstance(PatternContextNode parentContextNode) {
        return new HashMapNode(parentContextNode);
      }

    });
  }
}
