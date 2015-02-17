package org.insight.wordspace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.insight.wordspace.util.Filters;
import org.insight.wordspace.util.Filters.WordFilter;
import org.insight.wordspace.util.WordSim;

public abstract class GenericWordSpace<T> implements WordSpace<T> {

  /*
   * Store vectors & vocab in memory:
   */
  public Map<String, T> store = new HashMap<String, T>();

  @Override
  public boolean contains(String word) {
    return store.containsKey(word);
  }

  @Override
  public T vector(String word) {
    return store.get(word);
  }

  @Override
  public double cosineSimilarity(String w1, String w2) {
    return cosineSimilarity(vector(w1), vector(w2));
  }

  public abstract double cosineSimilarity(T vec1, T vec2);

  @Override
  public double distanceSimilarity(String w1, String w2) {
    return distanceSimilarity(vector(w1), vector(w2));
  }

  public abstract double distanceSimilarity(T vec1, T vec2);

  /*
   * Linear search for k Nearest Neighbours of a vector:
   */
  @Override
  public List<WordSim> knn(T vec, int k, WordFilter... filters) {
    PriorityQueue<WordSim> kSimilarWords = new PriorityQueue<WordSim>(k * 2);

    for (Entry<String, T> e : store.entrySet()) {
      if (Filters.apply(e.getKey(), filters)) {
        double dot = cosineSimilarity(vec, e.getValue());
        kSimilarWords.add(new WordSim(e.getKey(), dot));
      }
    }

    List<WordSim> col = new ArrayList<WordSim>();
    for (int i = 0; i < k; i++) {
      WordSim ws = kSimilarWords.poll();
      if (ws != null) {
        col.add(ws);
      }
    }
    return col;
  }

  @Override
  public List<WordSim> knn(String word, int k, WordFilter... filters) {
    return knn(vector(word), k, filters);
  }

  @Override
  public List<String> knnWords(String word, int k, WordFilter... filters) {
    return knnWords(vector(word), k, filters);
  }

  @Override
  public List<String> knnWords(T vec, int k, WordFilter... filters) {
    List<WordSim> sims = knn(vec, k, filters);
    List<String> words = new ArrayList<String>();
    for (WordSim sim : sims) {
      words.add(sim.getString());
    }
    return words;
  }

  /*
   * Additive representation of several words:
   */
  @Override
  public T sentenceVector(String sentence) {
    return sentenceVector(sentence, Filters.removeStopwords);
  }

  @Override
  public T sentenceVector(String sentence, WordFilter... filters) {
    List<T> vectors = new ArrayList<T>();
    for (String word : StringUtils.split(sentence, ' ')) {
      if (Filters.apply(word, filters)) {
        vectors.add(vector(word));
      }
    }
    return additiveSentenceVector(vectors);
  }

  @Override
  public abstract T additiveSentenceVector(List<T> vectors);

  /*
   * Debug:
   */
  public void printSims(String w, List<WordSim> sims) {
    for (WordSim s : sims) {
      String str = String.format("%s %s %.4f", w, s.getString(), s.getDouble());
      System.out.println(str);
    }
  }

  /*
   * Save Model as text file:
   */
  public boolean saveAsText(File output) {
    for (Entry<String, T> entry : store.entrySet()) {
      String word = String.format("%s %s\n", entry.getKey(), StringUtils.join(entry.getValue(), ' '));
      // TODO:
      word = word.replaceAll("\\[", "");
      word = word.replaceAll(";", "");
      word = word.replaceAll("\\]", "");
      try {
        FileUtils.writeStringToFile(output, word, true);
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
    }
    return true;
  }


}
