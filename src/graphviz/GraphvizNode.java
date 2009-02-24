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
package graphviz;

/** 
 * <p>An interface that simplifies the generation of graphviz based graphs. It is used in
 * the visualisation of the classification tree. It should not be used for graphs
 * containing cycles.</p>
 * <p>The methods that generate the textual description of the graph use a StringBuffer -
 * each method accepts a StringBuffer as a destination for the textual representation, and it
 * returns the modified buffer.</p>
 * <p>There are two methods that generate edges from the node to the rest of the world. The first,
 * <code>internalGraph()</code> generates edges and nodes that logically belong to the same graph
 * (the same context, and use the same algorithm). The <code>externalGraph()</code> generates
 * an "external" graph, where the context (and possibly the matching algorithm) is different.</code>
 * 
 * @author Kim Sullivan
 *
 */
public interface GraphvizNode {

  /**
   * Returns the unique node ID of this graphviz node.
   * @return
   */
  public abstract String gvNodeID();

  /**
   * Returns the display label of this node.
   * @return
   */
  public abstract String gvNodeLabel();

  /**
   * Appends a textual description of the subgraph that has this node as root (possibly
   * recursively calling the gvGraph() method of child nodes).
   * @param graph the destination buffer
   * @return the modified buffer
   */
  public abstract Graphviz gvGraph(Graphviz graph);

  /**
   * Appends a textual description of this node (without any edges).
   * @param graph the destination buffer
   * @return the modified buffer
   */
  public abstract Graphviz gvNodes(Graphviz graph);

  /**
   * Generates edges that belong to the same graph.
   * @param graph the destination buffer
   * @return the modified buffer
   */
  public abstract Graphviz gvInternalGraph(Graphviz graph);

  /**
   * Generates edges that belong to a different (sub)graph.
   * @param graph the destination buffer
   * @return the modified buffer
   */
  public abstract Graphviz gvExternalGraph(Graphviz graph);

}