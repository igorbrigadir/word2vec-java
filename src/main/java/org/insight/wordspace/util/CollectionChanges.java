package org.insight.wordspace.util;

import java.util.Collection;
import java.util.HashSet;

public class CollectionChanges {

  public String summaryString() {

    StringBuilder sb = new StringBuilder();

    sb.append("Additions ");
    sb.append("" + addedToSet + "\n");
    // sb.append("Words + " + add_w.size() + " \n");
    // sb.append("#tags + " + add_hashtags.size() + " \n");
    // sb.append("@mentions + " + add_ment.size() + " \n");

    sb.append("Removals ");
    sb.append("" + removedFromSet + "\n");
    // sb.append("Words - " + rem_w.size() + " \n");
    // sb.append("#tags - " + rem_hashtags.size() + " \n");
    // sb.append("@mentions - " + rem_ment.size() + " \n");


    return sb.toString();

  }

  Collection<String> addedToSet = new HashSet<String>();
  Collection<String> removedFromSet = new HashSet<String>();

  Collection<String> add_hashtags = new HashSet<String>();
  Collection<String> rem_hashtags = new HashSet<String>();

  Collection<String> add_ment = new HashSet<String>();
  Collection<String> rem_ment = new HashSet<String>();

  Collection<String> add_w = new HashSet<String>();
  Collection<String> rem_w = new HashSet<String>();

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

        if (setElement.startsWith("#")) {
          add_hashtags.add(setElement);
        } else if (setElement.startsWith("@")) {
          add_ment.add(setElement);
        } else {
          add_w.add(setElement);
        }



      }
    }


    // Check for removed
    for (String setElement : previous) {
      if (!next.contains(setElement)) {
        this.setRemoval(setElement);


        if (setElement.startsWith("#")) {
          rem_hashtags.add(setElement);
        } else if (setElement.startsWith("@")) {
          rem_ment.add(setElement);
        } else {
          rem_w.add(setElement);
        }

      }
    }

    return this;
  }
}
