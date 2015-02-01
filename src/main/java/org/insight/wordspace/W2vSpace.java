package org.insight.wordspace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.insight.wordspace.util.StopWords;
import org.insight.wordspace.util.VectorMath;
import org.insight.wordspace.util.W2VModelLoader;
import org.insight.wordspace.util.WordSim;

/*
 * A Java wrapper for W2v - Only Reads a pre trained model!
 */
// public class W2vSpace extends HashMap<String, float[]> {

public class W2vSpace extends WordVectorSpace<float[]> {

  public boolean saveAsText(File output) {
    for (Entry<String, float[]> entry : store.entrySet()) {
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

  public float[] sentenceVector(boolean filter, String sentence) {
    return sentenceVector(filter, sentence.toLowerCase().split(" "));
  }

  public float[] sentenceVector(boolean filter, String... words) {
    List<float[]> vectors = new ArrayList<float[]>();
    for (String w : words) {

      // Stopword filter:
      if (filter && StopWords.isStopWord(w)) {
        continue;
      }

      if (this.contains(w)) {
        vectors.add(vector(w));
      }
    }
    return VectorMath.addAll(vectors);
  }



  // Linear search k Nearest Neighbour:
  public List<String> knnWords(String word, int k) {
    if (!this.contains(word)) {
      System.err.println("Out of vocab word: " + word);
      return new ArrayList<String>();
    }

    List<WordSim> words = knn(word, k);
    List<String> ret = new ArrayList<String>();
    for (WordSim w : words) {
      ret.add(w.getString());
    }
    return ret;
  }


  public List<WordSim> knn(String word, int k) {
    return knn(word, vector(word), k);
  }

  // Internal! but can be called to get similarities!
  public List<WordSim> knn(String word, float[] vec, int k) {
    PriorityQueue<WordSim> kSimilarWords = new PriorityQueue<WordSim>(k * 2);

    for (Entry<String, float[]> e : store.entrySet()) {
      WordSim sim = new WordSim(e.getKey(), VectorMath.cosineSimilarity(vec, e.getValue()));
      kSimilarWords.add(sim);
    }

    List<WordSim> col = new ArrayList<WordSim>();
    // col.clear();
    for (int i = 0; i < k; i++) {
      WordSim ws = kSimilarWords.poll();
      if (!ws.getString().equalsIgnoreCase(word)) {
        col.add(ws);
      }
    }
    return col;
  }


  // WITH PREFIX:
  /*
   * public List<WordSim> knn(String word, int k, String prefix) { if
   * (!this.contains(word)) { System.err.println("Out of vocab word: " + word);
   * return new ArrayList<WordSim>(); }
   * 
   * if (prefix.equalsIgnoreCase("none")) { return knn(this.vector(word), k,
   * true); } else { return knn(this.vector(word), k, prefix); } }
   */


  public List<WordSim> knn(float[] vec, int k, boolean withScores, String prefix) {
    PriorityQueue<WordSim> kSimilarWords = new PriorityQueue<WordSim>(k);
    for (Entry<String, float[]> e : store.entrySet()) {

      if (!e.getKey().startsWith(prefix)) {
        continue;
      }

      WordSim sim = new WordSim(e.getKey(), VectorMath.cosineSimilarity(vec, e.getValue()));

      kSimilarWords.add(sim);

    }
    List<WordSim> col = new ArrayList<WordSim>();
    // col.clear();

    for (int i = 0; i < (k + 1); i++) {
      col.add(kSimilarWords.poll());
    }
    return col;
  }



  /*
   * WordsOnly
   */

  public List<WordSim> knWords(float[] vec, int k) {
    PriorityQueue<WordSim> kSimilarWords = new PriorityQueue<WordSim>(k);
    for (Entry<String, float[]> e : store.entrySet()) {

      if (e.getKey().startsWith("@") || e.getKey().startsWith("#")) {
        continue;
      }

      WordSim sim = new WordSim(e.getKey(), VectorMath.cosineSimilarity(vec, e.getValue()));
      kSimilarWords.add(sim);
    }
    List<WordSim> col = new ArrayList<WordSim>();
    for (int i = 0; i < k; i++) {
      col.add(kSimilarWords.poll());
    }
    return col;
  }

  void printSims(String w, List<WordSim> sims) {
    for (WordSim s : sims) {
      String str = String.format("%s %s %.4f", w, s.getString(), s.getDouble());
      System.out.println(str);
    }
  }

  public static void main(String[] args) {
    long startTime = System.nanoTime();

    W2vSpace w2v = W2VModelLoader.load("/home/igor/git/word2vec-java/src/main/resources/vectors.bin");

    long estimatedTime = System.nanoTime() - startTime;
    float secs = estimatedTime / 1000000000.0F;
    System.out.println("TOTAL Load TIME: " + secs);


    startTime = System.nanoTime();

    int i = 100;
    for (String word : w2v.store.keySet()) {
      w2v.knn(word, 20);
      i--;
      if (i < 0) {
        break;
      }
    }

    w2v.printSims("king", w2v.knn("king", 10));
    w2v.printSims("queen", w2v.knn("queen", 10));


    estimatedTime = System.nanoTime() - startTime;
    secs = estimatedTime / 1000000000.0F;
    System.out.println("TOTAL EXECUTION TIME: " + secs);
  }

  /*
   * 
   * W2vSpace w2v =
   * W2VModelLoader.load("/home/igor/tmp/stream_w24_r15_v200_rsf_201311012345.bin"
   * );
   * 
   * System.out.println("");
   * 
   * System.out.println("lax"); System.out.println("shooting");
   * 
   * float[] vec = new float[200];
   * 
   * //vec = VectorMath.plus(vec, w2v.vector("police")); //vec =
   * VectorMath.plus(vec, w2v.vector("shooting")); //vec = VectorMath.plus(vec,
   * w2v.vector("police")); //vec = VectorMath.plus(vec,
   * w2v.vector("shooting"));
   * 
   * List<WordSim> sims = w2v.knn(vec, 100, true); for (WordSim sim : sims) {
   * 
   * System.out.println(sim.getString() + " - " + sim.getDouble());
   * 
   * }
   * 
   * System.out.println("");
   * 
   * 
   * System.exit(0);
   * 
   * float[] result = VectorMath.multiply(w2v.vector("police"),
   * w2v.vector("shooting"));
   * 
   * List<WordSim> aspects = w2v.knn(result, 100, true);
   * 
   * System.out.println("police * Shooting");
   * 
   * for (WordSim sim : aspects) {
   * 
   * System.out.println(sim.getString() + " - " + sim.getDouble());
   * 
   * } }
   * 
   * }
   */

}
