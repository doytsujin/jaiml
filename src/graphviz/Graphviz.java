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
 * This class provides utility methods for generating edges and nodes in the Graphviz language.
 * @author Kim Sullivan
 *
 */
public class Graphviz {
  /**
   * Indentation level.
   */
  private static int indent=0;
  /**
   * Generates indentation according to the current indentation level.
   * @param sb
   */
  private static void indent(StringBuilder sb) {
    for (int i=0;i<indent; i++) {
      sb.append("\t");
    }
  }
  
  /**
   * The epsilon symbol used in epsilon transitions
   */
  public static final String EPSILON = "epsilon";

  /**
   * A symbol that accepts all characters from the alphabet (used in wildcards)
   */
  public static final String ALPHABET = "T";

  /**
   * Generate a list of node or edge attributes according to the graphviz syntax 
   * @param sb
   * @param attributes an array of key - value strings.
   * @return
   */
  private static StringBuilder attributes(StringBuilder sb, String... attributes) {
    if (attributes.length >0) {
      sb.append('[');
      for (int i=0; i<attributes.length;i+=2) {
        sb.append(attributes[i]);
        sb.append("=\"");
        sb.append(attributes[i+1]);
        sb.append("\" ");
      }
      sb.append(']');
    }

    return sb;
  }
  
  /**
   * Generate a list of graph or subgraph attributes with the correct graphviz syntax.
   * @param sb
   * @param attributes an array of key - value strings.
   * @return
   */
  public static StringBuilder graphAttributes(StringBuilder sb, String... attributes) {
    if (attributes.length >0) {
      indent(sb);
      for (int i=0; i<attributes.length;i+=2) {
        sb.append(attributes[i]);
        sb.append("=\"");
        sb.append(attributes[i+1]);
        sb.append("\";\n");
      }
    }

    return sb;
  }
  /**
   * Generate a graphviz node (with optional attributes)
   * @param sb
   * @param id the node id
   * @param attributes keys and values
   * @return
   */
  public static StringBuilder node(StringBuilder sb,String id, String... attributes) {  
    indent(sb);
    sb.append(id);
    attributes(sb,attributes);
    sb.append(";\n"); 
    return sb;
  }
  
  /**
   * Generate a graphviz edge between the two specified nodes.
   * @param sb
   * @param from source node id
   * @param to destination node id
   * @param attributes optional list of attributes
   * @return
   */
  public static StringBuilder edge(StringBuilder sb, String from, String to, String...attributes) {
    indent(sb);
    sb.append(from);
    sb.append("->");
    sb.append(to);
    attributes(sb,attributes);
    sb.append(";\n");     
    return sb;    
  }
  
  /**
   * Starts a new graph or subgraph, and increases indentation.
   * @param sb
   * @param what
   * @return
   */
  public static StringBuilder start(StringBuilder sb, String what) {
    indent(sb);
    sb.append(what);
    sb.append(" {\n");
    indent++;
    return sb;
  }
  /**
   * Ends a graph or subgraph, and decreases indentation.
   * @param sb
   * @return
   */
  public static StringBuilder end(StringBuilder sb) {
    indent--;
    indent(sb);
    sb.append("}\n");
    return sb;
  }
  
  /**
   * Generates a labeled edge between a GraphvizNode and another,
   * and generates the graph induced by the "to" node.
   * @param sb
   * @param from source node
   * @param to destination graph
   * @param edgeLabel edge label
   * @return
   */
  public static StringBuilder connectGraph(StringBuilder sb, GraphvizNode from, GraphvizNode to, String edgeLabel) {
    if (to!=null) {      
      edge(sb, from.gvNodeID(), to.gvNodeID(),"label",edgeLabel);
      to.gvGraph(sb);      
    }
    return sb;
  }
  
}
