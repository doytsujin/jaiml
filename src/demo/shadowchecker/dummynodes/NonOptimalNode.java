package demo.shadowchecker.dummynodes;

import java.util.Map;

import aiml.classifier.MatchState;
import aiml.classifier.node.PatternNode;

public class NonOptimalNode extends PatternNode {
  protected Map<String, PatternNode> children;

  public NonOptimalNode(Map<String, PatternNode> map) {
    super(null);
    this.children = map;
  }

  public NonOptimalNode() {
    this(null);
  }

  @Override
  public AddResult add(int depth, String pattern) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean match(MatchState match) {
    // TODO Auto-generated method stub
    return false;
  }

}
