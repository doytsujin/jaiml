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
 * <p>
 * This class implements the "classic" AIML node, as implemented in Program D,
 * using a single map to store all child nodes and one large matching routine
 * that takes care of priorities.
 * </p>
 * 
 * <p>
 * Program D implements four variations on this theme, each storing more and
 * more nodes directly and only after this number has exceeded, switches to a
 * hashmap (java hashmaps have a large overhead, starting from 16 and increasing
 * twice for each reallocation). After some modifications that make the
 * Nodemaster/Nodemapper implementations compatible with Java's own Map
 * interface and fixing some bugs, it is possible to use these as a drop-in
 * replacement for Java's standard HashMap and TreeMap. Experiments with the AAA
 * aiml set show that after an initial large drop of memory consumption by the
 * first level of optimized NodeMapper, the other two versions don't provide any
 * significant space reduction.
 * </p>
 * 
 * @author Kim Sullivan
 * 
 */
public class AIMLNode extends PatternNode {

  /**
   * The map of all child nodes (wildcards or not)
   */
  private Map<String, PatternNode> children;

  public AIMLNode(PatternContextNode parent) {
    super(parent);
    children = (Map<String, PatternNode>) ((PatternBehaviour) parent.getContext().getBehaviour()).newMap();
  }

  @Override
  public AddResult add(int depth, String pattern) {
    if (depth == pattern.length()) {
      return new AddResult(this, this, depth);
    }
    String thisWord = pattern.substring(depth, Pattern.thisWordEnd(depth,
        pattern));
    PatternNode nextNode = children.get(thisWord);

    depth = Pattern.nextWord(depth, pattern);

    if (nextNode == null) {
      nextNode = parentContext.getPNF().getInstance(parentContext, depth,
          pattern);
    }
    AddResult result;
    result = nextNode.add(depth, pattern);
    children.put(thisWord, result.root);
    result.root = this;
    return result;
  }

  @Override
  public boolean match(MatchState match) {
    match.enterNode();

    String input = match.getContextValue();
    if ("".equals(input)) {//special handling for undefined inputs
      input = "*";
    }

    if (input.length() == match.depth) {
      if (subContext != null) {
        if (subContext.match(match)) {
          return match.leaveNode(true);
        }
      }
      return match.leaveNode(false);

    }

    PatternNode wildcardNode;

    if (matchWildcard("_", input, match)) {
      return match.leaveNode(true);
    }

    String thisWord = input.substring(match.depth, Pattern.thisWordEnd(
        match.depth, input));
    match.reenterNode();
    PatternNode node = children.get(thisWord);
    if (node != null) {
      int originalDepth = match.depth;
      match.depth = Pattern.nextWord(match.depth, input);
      if (node.match(match)) {
        return match.leaveNode(true);
      }
      match.depth = originalDepth;
    }

    if (matchWildcard("*", input, match)) {
      return match.leaveNode(true);
    }

    return match.leaveNode(false);
  }

  private boolean matchWildcard(String wc, String input, MatchState match) {
    PatternNode wildcardNode = children.get(wc);
    if (wildcardNode != null) {
      MatchState.Wildcard w = match.addWildcard();

      if ((wildcardNode instanceof AIMLNode) &&
          ((AIMLNode) wildcardNode).children.size() == 0) {
        match.depth = input.length();
        w.growRest();
      } else {
        match.depth = Pattern.nextWord(match.depth, input);
        w.growTo(match.depth);
      }

      match.reenterNode();
      if (wildcardNode.match(match)) {
        return true;
      }

      while (match.depth != input.length()) {
        match.reenterNode();
        match.depth = Pattern.nextWord(match.depth, input);
        w.growTo(match.depth);
        if (wildcardNode.match(match)) {
          return true;
        }
      }

      match.depth -= w.getLength();
      match.removeWildcard();
    }
    return false;
  }

  /**
   * Register this node type in PatternNodeFactory. The AIML node is universal,
   * and matches every pattern constituent.
   */
  public static void register(PatternNodeFactory patternNodeFactory) {
    patternNodeFactory.registerNode(new Creatable() {
      public boolean canCreate(int depth, String pattern) {
        return true;
      }

      public PatternNode getInstance(PatternContextNode parentContextNode) {
        return new AIMLNode(parentContextNode);
      }

    });
  }

  @Override
  public void gvNodes(Graphviz graph) {
    graph.node(gvNodeID(), "label", "");
  }

  @Override
  public void gvExternalGraph(Graphviz graph) {
    graph.connectGraph(this, subContext, Graphviz.EPSILON);
  }

  @Override
  public void gvInternalGraph(Graphviz graph) {
    for (Entry<String, PatternNode> branch : children.entrySet()) {
      if ("_".equals(branch.getKey())) {
        graph.edge(gvNodeID(), gvNodeID(), "label",
            (Graphviz.ALPHABET + "/" + branch.getKey()));
        graph.connectGraph(this, branch.getValue(),
            (Graphviz.ALPHABET + "/" + branch.getKey()));
      } else if ("*".equals(branch.getKey())) {
        graph.edge(gvNodeID(), gvNodeID(), "label",
            (Graphviz.ALPHABET + "/" + branch.getKey()));
        graph.connectGraph(this, branch.getValue(),
            (Graphviz.ALPHABET + "/" + branch.getKey()));
      } else {
        graph.connectGraph(this, branch.getValue(),
            ("'" + branch.getKey() + "'"));
      }
    }
  }

  @Override
  protected void getThisNodeStats(NodeStatistics stats) {
    super.getThisNodeStats(stats);
    if (children.containsKey("_")) {
      stats.addLoop(1);
    }
    if (children.containsKey("*")) {
      stats.addLoop(1);
    }
  }

  @Override
  protected void getInternalNodeCount(NodeStatistics stats) {
    super.getInternalNodeCount(stats);
    stats.addMapCount(1);
    stats.addBranches(children.size());
    stats.addBranchOverhead(MapInspector.getOverhead(children));
    for (Entry<String, PatternNode> branch : children.entrySet()) {
      branch.getValue().getNodeCount(stats);
    }
  }

}
