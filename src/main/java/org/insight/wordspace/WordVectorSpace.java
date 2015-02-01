package org.insight.wordspace;

import java.util.HashMap;
import java.util.Map;

import org.insight.wordspace.util.VectorMath;

public abstract class WordVectorSpace<T> {

  public Map<String, T> store = new HashMap<String, T>();

  public boolean contains(String word) {
    return store.containsKey(word);
  }

  public T vector(String word) {
    return store.get(word);
  }


  /*
   * Cosine Distance between 2 words:
   */
  public Double cosineDistance(String word1, String word2) {
    if (!this.contains(word1)) {
      // System.err.println( "Out of vocab word: " + word1 );
      // word1 = "</s>";
      return Double.NaN;
    }
    if (!this.contains(word2)) {
      // System.err.println( "Out of vocab word: " + word2 );
      // word2 = "</s>";
      return Double.NaN;
    }
    return VectorMath.cosineSimilarity(vector(word1), vector(word2));
  }

}
