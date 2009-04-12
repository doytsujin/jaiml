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

import graphviz.Graphviz;
import aiml.classifier.PaternSequence.PatternIterator;
import aiml.context.ContextInfo;
import aiml.environment.Environment;

/**
 * <p>
 * This class encapsulates all AIML pattern matching functionality.
 * </p>
 * 
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public class Classifier {

  /** The root context tree */
  private ContextNode tree;

  /** Information about context set up */
  private ContextInfo contextInfo;

  /** The number of paths in the tree */
  private int count = 0;

  /**
   * Creates an instance of the aiml matcher.
   */
  public Classifier() {
    contextInfo = new ContextInfo();
  }

  /**
   * Match the current context state to the paths in the tree.
   * 
   * @return a complete match state if succesfull; <code>null</code> otherwise
   */
  public MatchState match(Environment e) {
    MatchState m = new MatchState(e);

    if (tree != null && tree.match(m)) {
      return m;
    } else {
      return null;
    }
  }

  /**
   * Add the sequence of patterns to the classifier trie.
   * 
   * @param sequence
   *          the sequence to be added
   * @param o
   *          the object to be stored
   * @throws DuplicatePathException
   */
  public void add(PaternSequence sequence, Object o)
      throws DuplicatePathException {
    PatternIterator patterns = sequence.iterator();
    if (tree == null) {
      if (patterns.hasNext()) {
        tree = patterns.peek().getContext().createClassifierNode(this,
            patterns, null, o);
      } else {
        tree = new LeafContextNode(this, o);
      }
    } else {
      tree = tree.add(patterns, o);
    }
    count++; // this is OK, because if the sequence isn't added, an exception gets
    // thrown before we reach this
  }

  public ContextInfo getContextInfo() {
    return contextInfo;
  }

  /**
   * Returns the number of loaded patterns.
   * 
   * @return the number of loaded patterns.
   */
  public int getCount() {
    return count;
  }

  public Graphviz gvGraph(Graphviz graph) {
    graph.start("digraph classifier");
    graph.graphAttributes("rankdir", "LR");
    if (tree != null) {
      tree.gvGraph(graph);
    }
    graph.end();
    return graph;
  }
}
