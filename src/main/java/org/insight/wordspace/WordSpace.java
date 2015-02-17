package org.insight.wordspace;

import java.util.List;

import org.insight.wordspace.util.Filters.WordFilter;
import org.insight.wordspace.util.WordSim;

public interface WordSpace<T> {

  public boolean contains(String word);

  public T vector(String word);

  public T sentenceVector(String sentence);

  public T sentenceVector(String sentence, WordFilter... filters);

  public T additiveSentenceVector(List<T> vectors);

  public double cosineSimilarity(String w1, String w2);

  public double distanceSimilarity(String w1, String w2);

  public List<WordSim> knn(String word, int k, WordFilter... filters);

  public List<WordSim> knn(T vec, int k, WordFilter... filters);

  public List<String> knnWords(String word, int k, WordFilter... filters);

  public List<String> knnWords(T vec, int k, WordFilter... filters);

}
