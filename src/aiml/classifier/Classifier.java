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

import javax.swing.event.EventListenerList;

import aiml.category.CategoryEvent;
import aiml.category.CategoryListener;
import aiml.classifier.PatternSequence.PatternIterator;
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

  /** The list of category listeners */
  private EventListenerList categoryListeners;

  /**
   * Creates an instance of the aiml matcher.
   */
  public Classifier() {
    contextInfo = new ContextInfo();
    categoryListeners = new EventListenerList();
  }

  /**
   * Match the current context state to the paths in the tree.
   * 
   * @return the result of matching
   */
  public MatchState match(Environment e) {
    MatchState m = new MatchState(e);

    if (tree != null) {
      tree.match(m);
    }
    return m;
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
  public void add(PatternSequence sequence, Object o)
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
    fireCategoryAdded(sequence, o);
    count++; // this is OK, because if the sequence isn't added, an exception gets
    // thrown before we reach this
  }

  /**
   * Adds the specified category listener to recieve category events from this
   * classifier.
   * 
   * @param listener
   *          the category listener
   */
  public void addCategoryListener(CategoryListener listener) {
    categoryListeners.add(CategoryListener.class, listener);
  }

  /**
   * Removes the specified category listeners so that it no longer recieves
   * category eventsf rom this classifier.
   * 
   * @param listener
   *          the category listener
   */
  public void removeCategoryListener(CategoryListener listener) {
    categoryListeners.remove(CategoryListener.class, listener);
  }

  /**
   * Notify all listeners that a category was added
   * 
   * @param sequence
   *          the sequence identifying the category
   * @param value
   *          the value object of a category
   */
  private void fireCategoryAdded(PatternSequence sequence, Object value) {
    Object[] listeners = categoryListeners.getListenerList();
    CategoryEvent categoryEvent = null;
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == CategoryListener.class) {
        // Lazily create the event:
        if (categoryEvent == null)
          categoryEvent = new CategoryEvent(this, sequence, value);
        ((CategoryListener) listeners[i + 1]).categoryAdded(categoryEvent);
      }
    }
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

  public NodeStatistics getNodeStats() {
    NodeStatistics stats = new NodeStatistics();
    if (tree != null) {
      tree.getNodeStats(stats);
    }
    return stats;
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
