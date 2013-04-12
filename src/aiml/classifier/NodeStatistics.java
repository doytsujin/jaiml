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

package aiml.classifier;

/**
 * This class serves as an accumulator of statistics for nodes.
 * 
 * @author Kim Sullivan
 * 
 */
public class NodeStatistics {
  /** The number of nodes */
  private int nodeCount = 0;

  /** The number of branches */
  private int branchCount = 0;

  /** The number of self-loops */
  private int loopCount = 0;

  /** The overhead of allocated but unused branches */
  private int branchOverhead = 0;

  /** the number of allocated maps */
  private int mapCount = 0;

  /**
   * Returns the number of nodes in a classifier
   * 
   * @return the number of nodes
   */
  public int getNodeCount() {
    return nodeCount;
  }

  /**
   * Returns the total number of forward branches in a classifier
   * 
   * @return the number of forward branches
   */
  public int getBranchCount() {
    return branchCount;
  }

  /**
   * Returns the total number of self-loops in a classifier
   * 
   * @return the number of self-loops
   */
  public int getLoopCount() {
    return loopCount;
  }

  /**
   * Returns the branch overhead - how many branches are allocated but unused.
   * 
   * @return the number of unused branches
   */
  public int getBranchOverhead() {
    return branchOverhead;
  }

  /**
   * Returns the number of nodes that use a generic map
   * 
   * @return the number of nodes using a map
   */
  public int getMapCount() {
    return mapCount;
  }

  /**
   * Add a certain amount of nodes to the statistics
   * 
   * @param amount
   *          the amount of nodes to add
   */
  public void addNodes(int amount) {
    nodeCount += amount;
  }

  /**
   * Add a certain amount of branches to the statistics
   * 
   * @param amount
   *          the amount of branches to add
   */
  public void addBranches(int amount) {
    branchCount += amount;
  }

  /**
   * Add a certain amount of loops to the statistics
   * 
   * @param amount
   *          the amount of loops to add
   */
  public void addLoop(int amount) {
    loopCount += amount;
  }

  /**
   * Add a number of allocated but unused branches
   * 
   * @param amount
   *          the amount of unused branches
   * 
   */
  public void addBranchOverhead(int amount) {
    branchOverhead += amount;
  }

  /**
   * Increase the number of nodes that allocate a generic map
   * 
   * @param amount
   */
  public void addMapCount(int amount) {
    mapCount += amount;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getNodeCount());
    sb.append(" nodes, ");
    sb.append(getBranchCount());
    sb.append(" forward branches, ");
    sb.append(getLoopCount());
    sb.append(" self-loops, ");
    sb.append(getBranchOverhead());
    sb.append(" unused branches, ");
    sb.append(getMapCount());
    sb.append(" maps");

    return sb.toString();
  }

  public StringBuilder toCSV() {
    StringBuilder sb = new StringBuilder();

    sb.append(getNodeCount());
    sb.append(',');
    sb.append(getBranchCount());
    sb.append(',');
    sb.append(getLoopCount());
    sb.append(',');
    sb.append(getBranchOverhead());
    sb.append(',');
    sb.append(getMapCount());
    sb.append(',');

    return sb;
  }

  public static StringBuilder toCSVHeader() {
    StringBuilder sb = new StringBuilder();

    sb.append("nodes,");
    sb.append("branches,");
    sb.append("self-loops,");
    sb.append("branch overhead,");
    sb.append("maps,");
    return sb;
  }

}
