package org.insight.wordspace.util;

import java.util.Collection;
import java.util.HashSet;

public class CollectionChanges {

  public String summaryString() {

    StringBuilder sb = new StringBuilder();

    sb.append("Additions: ");
    sb.append("" + addedToSet + "\n");

    sb.append("Removals: ");
    sb.append("" + removedFromSet + "\n");

    return sb.toString();

  }

  Collection<String> addedToSet = new HashSet<String>();
  Collection<String> removedFromSet = new HashSet<String>();

  public void setAddition(String setItem) {
    addedToSet.add(setItem);
  }

  public void setRemoval(String setItem) {
    removedFromSet.add(setItem);
  }

  public Collection<String> additions() {
    return addedToSet;
  }

  public Collection<String> removals() {
    return removedFromSet;
  }

  public boolean isEmpty() {
    return addedToSet.isEmpty() && removedFromSet.isEmpty();
  }

  /**
   * Compute the changes between the two sets.
   *
   * @param previous the previous set
   * @param next the current set
   * @return the changes
   */

  public static CollectionChanges findAdditions(Collection<String> previous, Collection<String> next) {

    CollectionChanges chg = new CollectionChanges();

    // Check for added terms
    for (String setElement : next) {
      if (!previous.contains(setElement)) {
        chg.setAddition(setElement);
      }
    }

    return chg;

  }

  public static CollectionChanges findAdditions(Collection<String> previous, Collection<String> next, boolean ignore_mentions) {

    CollectionChanges chg = new CollectionChanges();

    // Check for added terms
    for (String setElement : next) {

      if (setElement.startsWith("@")) {
        continue;
      }

      if (!previous.contains(setElement)) {
        chg.setAddition(setElement);
      }
    }

    return chg;

  }

  public CollectionChanges findChanges(Collection<String> previous, Collection<String> next) {

    // Check for added users
    for (String setElement : next) {
      if (!previous.contains(setElement)) {
        this.setAddition(setElement);
      }
    }

    // Check for removed
    for (String setElement : previous) {
      if (!next.contains(setElement)) {
        this.setRemoval(setElement);
      }
    }

    return this;
  }
}
