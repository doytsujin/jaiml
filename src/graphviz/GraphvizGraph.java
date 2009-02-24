package graphviz;

public class GraphvizGraph {
  private StringBuilder sb;

  public GraphvizGraph() {
    this.sb = new StringBuilder();
  }

  public StringBuilder getSb() {
    return sb;
  }

  @Override
  public String toString() {
    return sb.toString();
  }
}