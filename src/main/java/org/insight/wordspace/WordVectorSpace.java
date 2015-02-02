package org.insight.wordspace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.insight.wordspace.util.Filters;
import org.insight.wordspace.util.Filters.WordFilter;
import org.insight.wordspace.util.VectorMath;

public abstract class WordVectorSpace<T> {

  /*
   * Store it in memory:
   */
  public Map<String, T> store = new HashMap<String, T>();

  public boolean contains(String word) {
    return store.containsKey(word);
  }

  public T vector(String word) {
    return store.get(word);
  }


  /*
   * Cosine Distance between 2 words:
   * 
   * public Double cosineDistance(String word1, String word2) { if
   * (!contains(word1)) { // System.err.println( "Out of vocab word: " + word1
   * ); // word1 = "</s>"; return Double.NaN; } if (!contains(word2)) { //
   * System.err.println( "Out of vocab word: " + word2 ); // word2 = "</s>";
   * return Double.NaN; } return VectorMath.cosineSimilarity(vector(word1),
   * vector(word2)); }
   */

  public boolean saveAsText(File output) {
    for (Entry<String, T> entry : store.entrySet()) {
      String word = String.format("%s %s\n", entry.getKey(), StringUtils.join(entry.getValue(), ' '));
      try {
        FileUtils.writeStringToFile(output, word, true);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return false;
      }
    }
    return true;
  }

  public static W2vSpace normalizeModel(W2vSpace raw) {
    W2vSpace norm = new W2vSpace();
    for (Entry<String, float[]> e : raw.store.entrySet()) {
      norm.store.put(e.getKey(), VectorMath.normalize(e.getValue()));
    }
    return norm;
  }

  // Adding several words:
  public T sentenceVector(String sentence) {
    return sentenceVector(sentence, Filters.removeStopwords);
  }

  public T sentenceVector(String sentence, WordFilter... filters) {
    List<T> vectors = new ArrayList<T>();
    for (String word : StringUtils.split(sentence, ' ')) {
      if (Filters.apply(word, filters)) {
        vectors.add(vector(word));
      }
    }
    return VectorMath.addAll(vectors);;
  }

}
