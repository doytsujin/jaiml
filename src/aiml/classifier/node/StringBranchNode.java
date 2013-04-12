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

import graphviz.Graphviz;

import java.util.Map;
import java.util.Map.Entry;

import aiml.classifier.MatchState;
import aiml.classifier.NodeStatistics;
import aiml.classifier.Pattern;
import aiml.classifier.PatternContextNode;
import aiml.context.behaviour.PatternBehaviour;
import aiml.util.MapInspector;

/**
 * A single character branch in the strings portion of the pattern tree. All by
 * themselves, StringBranchNodes implementan uncompressed trie. To make storage
 * effective, compressed string nodes have to be implemented.
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public class StringBranchNode extends PatternNode {

  /**
   * a map to store the branches
   */
  private Map<Character, PatternNode> map;

  /**
   * Create a new empty string branch node. The type is PatternNode.STRING
   */
  public StringBranchNode(PatternContextNode parent) {
    super(parent);
    type = PatternNode.STRING;
    map = (Map<Character, PatternNode>) ((PatternBehaviour) parent.getContext().getBehaviour()).newMap();
  }

  public StringBranchNode(PatternContextNode parent, StringNode node) {
    this(parent);
    char c = node.getPattern().charAt(0); //this is safe, because a stringnode always represents at least 1 character
    map.put(c, node.removePrefix(1));

  }

  /**
   * Add the pattern to itself. Since this already represents a branch node, no
   * optimizations/splitting are preformed, except when adding an end of a
   * string, or a wildcard.
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
    if (depth == pattern.length()) {
      PatternNode node = new EndOfStringNode(parentContext, this);
      result = node.add(depth, pattern);
      return result;
    }
    if (Pattern.isWildcard(depth, pattern)) {
      PatternNode node = new BranchNode(parentContext, this);
      result = node.add(depth, pattern);
      return result;
    }

    char c = pattern.charAt(depth);
    PatternNode node = map.get(c);
    depth++;
    if (node == null) {
      node = patternNodeFactory.getInstance(parentContext, depth, pattern);
    }
    result = node.add(depth, pattern);
    map.put(c, result.root);
    result.root = this;
    return result;

  }

  public boolean match(MatchState match) {
    //Match
    match.enterNode();
    char c;
    try {
      c = match.getContextValue().charAt(match.depth);
    } catch (StringIndexOutOfBoundsException e) {
      return match.leaveNode(false); //the current context is an empty string
    }
    PatternNode node = map.get(c);
    //match is "done" check result:
    if (node != null) {
      match.depth++;
      if (node.match(match)) {
        return match.leaveNode(true);
      } else {
        //restore the previous match state
        match.depth--;
        return match.leaveNode(false);
      }
    } else {
      return match.leaveNode(false);
    }
  }

  /**
   * Register this node type in PatternNodeFactory.
   * 
   * @param classifier
   *          TODO
   */
  public static void register(PatternNodeFactory patternNodeFactory) {
    patternNodeFactory.registerNode(new Creatable() {
      public boolean canCreate(int depth, String pattern) {

        return (depth != pattern.length() && pattern.length() > 0 && !Pattern.isWildcard(
            depth, pattern));
      }

      public PatternNode getInstance(PatternContextNode parentContextNode) {
        return new StringBranchNode(parentContextNode);
      }

    });
  }

  @Override
  public String toString() {

    return map.toString() + super.toString();
  }

  @Override
  public void gvNodes(Graphviz graph) {
    graph.node(gvNodeID(), "label", "");
  }

  @Override
  public void gvInternalGraph(Graphviz graph) {
    for (Entry<?, PatternNode> branch : map.entrySet()) {
      graph.connectGraph(this, branch.getValue(), ("'" + branch.getKey() + "'"));
    }
  }

  @Override
  protected void getInternalNodeCount(NodeStatistics stats) {
    super.getInternalNodeCount(stats);
    stats.addMapCount(1);
    stats.addBranches(map.size());
    stats.addBranchOverhead(MapInspector.getOverhead(map));
    for (Entry<?, PatternNode> branch : map.entrySet()) {
      branch.getValue().getNodeCount(stats);
    }
  }
}
