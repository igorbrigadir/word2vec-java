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

  /*
   * Apply filters to words:
   */
  public static boolean apply(WordSim wordSim, WordFilter... filters) {
    return apply(wordSim.getString(), filters);
  }

  public static boolean apply(String word, WordFilter... filters) {
    boolean keep = true;
    // XOR Results:
    for (WordFilter f : filters) {
      keep = keep ^ f.evaluate(word);
    }
    return keep;
  }

  /*
   * Common filters:
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
   * By Default, don't need to init() filters, and treat WordSim as String:
   */
  public static abstract class DefaultFilter implements WordFilter {
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
  public static class RemoveWords extends DefaultFilter {
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
   * RemoveWords with WordFilter interface:
   */
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
      }
    }
  };

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

  /*
   * Remove #hashtags
   */
  public static WordFilter removeHashtags = new Filters.DefaultFilter() {
    @Override
    public boolean evaluate(String word) {
      return word.startsWith("#");
    }
  };

  /*
   * Remove @mentions
   */
  public static WordFilter removeMentions = new Filters.DefaultFilter() {
    @Override
    public boolean evaluate(String word) {
      return word.startsWith("@");
    }
  };

}
