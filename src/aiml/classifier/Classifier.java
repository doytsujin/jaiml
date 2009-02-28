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
import aiml.classifier.node.EndOfStringNode;
import aiml.classifier.node.PatternNodeFactory;
import aiml.classifier.node.StringNode;
import aiml.classifier.node.WildcardNode;
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

  /** The number of paths in the tree */
  private int count = 0;

  /**
   * Creates an instance of the aiml matcher. Since this class is meant to be
   * static, it's hidden. More robust techniques for making it a singleton might
   * be used in the future.
   */
  private Classifier() {
  }

  private static class Holder {
    private static final Classifier classifier = new Classifier();
  }

  public static Classifier getInstance() {
    return Holder.classifier;
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
   * Add a path to the matching tree.
   * 
   * @param path
   *          the path to be added
   * @param o
   *          the object to be stored
   * @throws DuplicatePathException
   */
  public void add(Path path, Object o) throws DuplicatePathException {
    assert (getPNF().getCount() > 0) : "You have to register node types";
    if (tree == null) {
      if (path.getLength() != 0) {
        tree = new PatternContextNode(path.iterator(), o);
      } else {
        tree = new LeafContextNode(o);
      }
    } else {
      tree = tree.add(path.iterator(), o);
    }
    count++; // this is OK, because if the path isn't added, an exception gets
    // thrown before we reach this
  }

  public PatternNodeFactory getPNF() {
    return PatternNodeFactory.getFactory();
  }

  /**
   * Returns the number of loaded patterns.
   * 
   * @return the number of loaded patterns.
   */
  public int getCount() {
    return count;
  }

  /**
   * <p>
   * Resets the whole matching tree. This is usefull when the order of contexts
   * needs to be changed, because this invalidates the whole data structure.
   * </p>
   * <p>
   * This must follow after resetting the ContextInfo structure, but can be used
   * as a stand-alone method to remove all patterns from the matching tree.
   * </p>
   * 
   * @see aiml.context.ContextInfo#reset()
   */
  public void reset() {
    tree = null;
    count = 0;
  }

  /**
   * <p>
   * This is a convenience method to register the default (or hopefully most
   * optimal) node handler classes. When using this method, you don't have to
   * think about all the different aiml.classifier.node.* implementations.
   * </p>
   * 
   * @see aiml.classifier.node
   */
  public void registerDefaultNodeHandlers() {
    StringNode.register();
    EndOfStringNode.register();
    WildcardNode.register();
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
