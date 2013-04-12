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
 * This class serves as an accumulator of matching statistics
 * 
 * @author Kim Sullivan
 * 
 */
public class MatchStatistics {
  /** Tracks the number of nodes entered */
  private int nodeEnter;
  /** Tracks the number of nodes left during backtracking */
  private int nodeLeave;
  /** Tracks the nodes re-entered due to self-loops */
  private int nodeLoop;
  /** Tracks the number of context nodes entered */
  private int contextEnter;
  /** Tracks the number of context nodes left during backtracking */
  private int contextLeave;

  /**
   * Returns the number of nodes that have been entered during matching
   * 
   * @return the number of entered nodes
   */
  public int getNodeEnter() {
    return nodeEnter;
  }

  /**
   * Adds information about a single node being entered
   */
  public void addNodeEnter() {
    addNodeEnter(1);
  }

  private void addNodeEnter(int amount) {
    nodeEnter += amount;
  }

  /**
   * Returns the number of nodes that have been left during backtracking
   * 
   * @return the number of backtraced nodes
   */
  public int getNodeLeave() {
    return nodeLeave;
  }

  /**
   * Adds information about a single node being backtracked
   */
  public void addNodeLeave() {
    addNodeLeave(1);
  }

  private void addNodeLeave(int amount) {
    nodeLeave += amount;
  }

  /**
   * Returns the number of nodes that have been re-entered due to self-loops
   * 
   * @return the number of re-entered nodes
   */
  public int getNodeLoop() {
    return nodeLoop;
  }

  /**
   * Adds information about a single node loop
   */
  public void addNodeLoop() {
    addNodeLoop(1);
  }

  private void addNodeLoop(int amount) {
    nodeLoop += amount;
  }

  /**
   * Adds information about entering a context node
   */
  public void addContextEnter() {
    addContextEnter(1);
  }

  private void addContextEnter(int amount) {
    contextEnter += amount;
  }

  /**
   * Returns the number of context nodes that were entered
   * 
   * @return the number of entered context nodes
   */
  public int getContextEnter() {
    return contextEnter;
  }

  /**
   * Adds information about backtracking a context node
   */
  public void addContextLeave() {
    addContextLeave(1);
  }

  private void addContextLeave(int amount) {
    contextLeave += amount;
  }

  /**
   * Returns the number of context nodes that were backtracked
   * 
   * @return the number of backtracked context nodes
   */
  public int getContextLeave() {
    return contextLeave;
  }

  public void add(MatchStatistics matchStatistics) {
    addNodeEnter(matchStatistics.getNodeEnter());
    addNodeLeave(matchStatistics.getNodeLeave());
    addNodeLoop(matchStatistics.getNodeLoop());
    addContextEnter(matchStatistics.getContextEnter());
    addContextLeave(matchStatistics.getContextLeave());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(getNodeEnter());
    sb.append(" nodes entered, ");
    sb.append(getNodeLoop());
    sb.append(" nodes looped, ");
    sb.append(getNodeLeave());
    sb.append(" nodes backtracked, ");

    sb.append(getContextEnter());
    sb.append(" contexts entered, ");
    sb.append(getContextLeave());
    sb.append(" contexts left, ");

    return sb.toString();
  }

  public StringBuilder toCSV() {
    StringBuilder sb = new StringBuilder();

    sb.append(getNodeEnter());
    sb.append(',');
    sb.append(getNodeLoop());
    sb.append(',');
    sb.append(getNodeLeave());
    sb.append(',');

    sb.append(getContextEnter());
    sb.append(',');
    sb.append(getContextLeave());
    sb.append(',');

    return sb;
  }

  public static StringBuilder toCSVHeader() {
    StringBuilder sb = new StringBuilder();

    sb.append("nodes entered,");
    sb.append("nodes looped,");
    sb.append("nodes backtracked,");

    sb.append("contexts entered,");
    sb.append("contexts left,");

    return sb;
  }
}