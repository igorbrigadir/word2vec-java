package org.insight.wordspace.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/*
 * Apply Various Filters to words:
 */
public class Filters {

  public static WordFilter removePrefix = new Filters.PrefixFilter();

  public static WordFilter removeHashtags = new Filters.PrefixFilter().with("#");

  public static WordFilter removeMentions = new Filters.PrefixFilter().with("@");

  public static WordFilter removeWords = new Filters.RemoveWords();

  /*
   * Remove default nltk + twitter stopwords
   */
  public static WordFilter removeStopwords = new Filters.RemoveWords() {
    {
      try (InputStream in = this.getClass().getResourceAsStream("/stopwords.txt")) {
        String text = IOUtils.toString(in, "UTF-8");
        remove.addAll(Arrays.asList(StringUtils.split(text, ' ')));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  };

  /*
   * Apply filters to words:
   */
  public static boolean apply(WordSim wordSim, WordFilter... filters) {
    return apply(wordSim.getString(), filters);
  }

  public static boolean apply(String word, WordFilter... filters) {
    boolean keep = true;
    //Remove Duplicate Filters:
    Set<WordFilter> applyFilters = new HashSet<WordFilter>(Arrays.asList(filters));
    // XOR Results:
    for (WordFilter f : applyFilters) {
      keep = keep ^ f.evaluate(word);
      // System.out.println("Eval: " + word + " " + f.evaluate(word) + " still keep? " + keep);
    }
    return keep;
  }

  /*
   * Common filter:
   */
  public static WordFilter[] wordsOnly(WordFilter... moreFilters) {
    WordFilter[] filters = new WordFilter[3 + moreFilters.length];
    filters[0] = Filters.removeStopwords;
    filters[1] = Filters.removeHashtags;
    filters[2] = Filters.removeMentions;
    int i = 3;
    for (WordFilter f : moreFilters) {
      filters[i++] = f;
    }
    return filters;
  }

  public interface WordFilter {
    public boolean evaluate(String word);

    public boolean evaluate(WordSim wordSim);

    public WordFilter with(String... words);
  }

  /*
   * Default: treat WordSim as String:
   */
  private static abstract class DefaultFilter implements WordFilter {
    @Override
    public abstract boolean evaluate(String word);

    @Override
    public boolean evaluate(WordSim wordSim) {
      return evaluate(wordSim.getString());
    }

    @Override
    public WordFilter with(String... words) {
      return this;
    }
  }

  /*
   * Remove words specified with init(...)
   */
  private static class RemoveWords extends DefaultFilter {
    public Set<String> remove = new HashSet<String>();

    @Override
    public boolean evaluate(String word) {
      return remove.contains(word);
    }

    @Override
    public WordFilter with(String... words) {
      remove.addAll(Arrays.asList(words));
      return this;
    }
  }



  /*
   * Useful to add if you want to keep a word that gets filtered.
   *
   * Invert removal of specific words: Filters.invert.init("is", "#is")
   *
   * Invert ALL removals from all other filters: Filters.invert
   */
  public static WordFilter invertRemove = new Filters.DefaultFilter() {
    Set<String> keep = new HashSet<String>();

    @Override
    public boolean evaluate(String word) {
      if (keep.size() > 0) {
        return keep.contains(word);
      } else {
        return !keep.contains(word);
      }
    }

    @Override
    public WordFilter with(String... words) {
      keep.addAll(Arrays.asList(words));
      return this;
    }
  };


  private static class PrefixFilter extends DefaultFilter {
    private String[] pref = new String[0];

    @Override
    public boolean evaluate(String word) {
      for (String p : pref) {
        return word.startsWith(p);
      }
      return false;
    }

    @Override
    public WordFilter with(String... words) {
      this.pref = words;
      return this;
    }
  }



}
