package org.insight.wordspace.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/*
 * Apply Various Filters to words:
 */
public class Filters {
	
	  public WordFilter ccomp = (new PrefixFilter()).with("ccomp");
	  public WordFilter nsubj = (new PrefixFilter()).with("nsubj");
	  public WordFilter agent = (new PrefixFilter()).with("agent");
	  public WordFilter advcl = (new PrefixFilter()).with("advcl");

	

  public WordFilter removeHashtags = (new PrefixFilter()).with("#");
  public WordFilter removeMentions = (new PrefixFilter()).with("@");

  public WordFilter removeWords = new RemoveWords();

  public WordFilter removeShort = new RemoveShortWords();

  
  /*
   * Remove default nltk + twitter stopwords
   */
  public WordFilter removeStopwords = new RemoveWords() {
    {
      try (InputStream in = this.getClass().getResourceAsStream("/nltk_en_stopwords.txt")) {
        String text = IOUtils.toString(in, "UTF-8");
        remove.addAll(Arrays.asList(StringUtils.split(text, ' ')));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  };
  
  public WordFilter removeShortWords = new RemoveWords() {
	    {
	      try (InputStream in = this.getClass().getResourceAsStream("/nltk_en_stopwords.txt")) {
	        String text = IOUtils.toString(in, "UTF-8");
	        remove.addAll(Arrays.asList(StringUtils.split(text, ' ')));
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
	    }
	  };

  /*
   * Common filter:
   */
  public WordFilter[] wordsOnly(WordFilter... moreFilters) {
    WordFilter[] filters = new WordFilter[3 + moreFilters.length];
    filters[0] = removeStopwords;
    filters[1] = removeHashtags;
    filters[2] = removeMentions;
    int i = 3;
    for (WordFilter f : moreFilters) {
      filters[i++] = f;
    }
    return filters;
  }

  /*
   * Apply filters to words:
   */
  public static boolean apply(WordSim wordSim, WordFilter... filters) {
    return apply(wordSim.getString(), filters);
  }

  public static boolean apply(String word, WordFilter... filters) {
    boolean keep = true;
    //Remove Duplicate Filters:
    final Set<WordFilter> applyFilters = new HashSet<WordFilter>(Arrays.asList(filters));
    // XOR Results:
    for (WordFilter f : applyFilters) {
      keep = keep ^ f.evaluate(word);
      // System.out.println("Eval: " + word + " " + f.evaluate(word) + " still keep? " + keep);
    }
    return keep;
  }

  public interface WordFilter {
    public boolean evaluate(String word);

    public boolean evaluate(WordSim wordSim);

    public WordFilter with(String... words);
    
    public WordFilter with(Collection<String> words);
  }

  /*
   * Default: treat WordSim as String:
   */
  private abstract class DefaultFilter implements WordFilter {
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
  private class RemoveWords extends DefaultFilter {
    Set<String> remove;

    public RemoveWords() {
      remove = new HashSet<String>();
    }

    @Override
    public boolean evaluate(String word) {
      return remove.contains(word);
    }

    @Override
    public WordFilter with(String... words) {
      remove.addAll(Arrays.asList(words));
      return this;
    }
	@Override
	public WordFilter with(Collection<String> words) {
		remove.addAll(words);
		return this;
	}
  }
  
  private class RemoveShortWords extends DefaultFilter {
	    public RemoveShortWords() {
	    }

	    @Override
	    public boolean evaluate(String word) {
	      return (word.length() < 3);
	    }

		@Override
		public WordFilter with(Collection<String> words) {
			return this;
		}

	  }  
  

  /*
   * Useful to add if you want to keep a word that gets filtered.
   *
   * Invert removal of specific words: Filters.invert.with("is", "#is")
   *
   * Invert ALL removals from all other filters: Filters.invert
   */
  public WordFilter invertRemove = new DefaultFilter() {
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
	public WordFilter with(Collection<String> words) {
		return this;
	}
  };

  public class PrefixFilter extends DefaultFilter {
    String[] pref = new String[0];

    @Override
    public boolean evaluate(String word) {
      for (String p : pref) {
        return word.startsWith(p);
      }
      return false;
    }

    @Override
    public WordFilter with(String... pref) {
      this.pref = pref;
      return this;
    }

	@Override
	public WordFilter with(Collection<String> words) {
		return this;
	}
  }
}
