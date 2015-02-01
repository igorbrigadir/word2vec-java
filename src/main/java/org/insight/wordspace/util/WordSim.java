package org.insight.wordspace.util;

public class WordSim implements Comparable<WordSim> {

  private final String s;
  private final Double d;

  public WordSim(String s, Double d) {
    this.s = s;
    this.d = d;
  }

  public String getString() {
    return this.s;
  }

  public Double getDouble() {
    return this.d;
  }

  @Override
  public int compareTo(WordSim other) {
    // Reverse:
    return (-1 * getDouble().compareTo(other.getDouble()));
  }

  @Override
  public String toString() {
    return String.format("%s : %s", this.s, this.d);
  }

}
