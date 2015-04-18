package word2vec;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.insight.wordspace.GenericWordSpace;
import org.insight.wordspace.WordSpace;
import org.insight.wordspace.util.Filters;
import org.insight.wordspace.util.Filters.WordFilter;
import org.junit.Test;

public class WordSimFilters {

  Filters f = new Filters();

  @Test
  public void testStopwordFilter() {
    WordFilter filter = f.removeStopwords;
    String word = "the";
    assertEquals("filter 'the'", false, Filters.apply(word, filter));
  }


  @Test
  public void testMentionsFilter() {
    WordFilter filter = f.removeMentions;
    String word = "@foobar";
    assertEquals("filter '@foobar'", false, Filters.apply(word, filter));
  }

  @Test
  public void testHashtagFilter() {
    WordFilter filter = f.removeHashtags;
    String word = "#foobar";
    assertEquals("filter '#foobar'", false, Filters.apply(word, filter));
  }


  @Test
  public void testSingleWordFilter() {
    WordFilter filter = f.removeWords.with("foobar");
    String word = "foobar";
    String word_keep = "barfoo";
    assertEquals("keep 'barfoo'", true, Filters.apply(word_keep, filter));
    assertEquals("filter 'foobar'", false, Filters.apply(word, filter));
  }


  @Test
  public void testKNNFilter() {

    WordSpace<int[]> testSpace = new GenericWordSpace<int[]>() {
      @Override
      public double cosineSimilarity(int[] vec1, int[] vec2) {
        return 1;
      }

      @Override
      public double distanceSimilarity(int[] vec1, int[] vec2) {
        return 1;
      }

      @Override
      public int[] additiveSentenceVector(List<int[]> vectors) {
        return new int[] { 1, 1 };
      }

      {
        this.store.put("the", new int[] { 1, 1 });
        this.store.put("foobar", new int[] { 1, 1 });
        this.store.put("@mention", new int[] { 1, 1 });
        this.store.put("#tag", new int[] { 1, 1 });
        this.store.put("test", new int[] { 1, 1 });
      }
    };

    List<String> result = new ArrayList<String>();

    result = testSpace.knnWords("test", 5);
    assertEquals("No filters: ", true, result.contains("the"));
    assertEquals("No filters: ", true, result.contains("foobar"));
    assertEquals("No filters: ", true, result.contains("@mention"));
    assertEquals("No filters: ", true, result.contains("#tag"));
    assertEquals("No filters: ", true, result.contains("test"));

    result = testSpace.knnWords("test", 5, f.wordsOnly());
    assertEquals("Words Only: ", false, result.contains("the"));
    assertEquals("Words Only: ", true, result.contains("foobar"));
    assertEquals("Words Only: ", false, result.contains("@mention"));
    assertEquals("Words Only: ", false, result.contains("#tag"));
    assertEquals("Words Only: ", true, result.contains("test"));

    result = testSpace.knnWords("test", 5, f.wordsOnly(f.removeWords.with("test")));
    assertEquals("Words Only, Remove: ", false, result.contains("the"));
    assertEquals("Words Only, Remove: ", true, result.contains("foobar"));
    assertEquals("Words Only, Remove: ", false, result.contains("@mention"));
    assertEquals("Words Only, Remove: ", false, result.contains("#tag"));
    assertEquals("Words Only, Remove: ", false, result.contains("test"));

    result = testSpace.knnWords("test", 5, f.removeWords.with("test"));
    assertEquals("Remove Only: ", true, result.contains("the"));
    assertEquals("Remove Only: ", true, result.contains("foobar"));
    assertEquals("Remove Only: ", true, result.contains("@mention"));
    assertEquals("Remove Only: ", true, result.contains("#tag"));
    assertEquals("Remove Only: ", false, result.contains("test"));

  }
}
