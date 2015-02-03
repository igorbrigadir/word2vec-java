package org.insight.wordspace;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.insight.wordspace.util.Filters;
import org.insight.wordspace.util.VectorMath;
import org.insight.wordspace.util.WordSim;
import org.jblas.FloatMatrix;

/*
 * A Java wrapper for W2v - Only Reads a pre trained model!
 */
public class W2vSpace extends WordVectorSpace<FloatMatrix> {

  public static W2vSpace load(String word2vecModel) {
    W2vSpace model = new W2vSpace();
    try (DataInputStream ds = new DataInputStream(new BufferedInputStream(new FileInputStream(word2vecModel), 131072))) {
      // Read header:
      int numWords = Integer.parseInt(readString(ds));
      int vecSize = Integer.parseInt(readString(ds));
      System.out.println(numWords + " Words, Vector size " + vecSize);
      for (int i = 0; i < numWords; i++) {
        // Word:
        String word = readString(ds);
        // Unit Vector
        FloatMatrix f = new FloatMatrix(readFloatVector(ds, vecSize));
        model.store.put(word, VectorMath.normalize(f));
      }
    } catch (IOException e) {
      System.err.println("ERROR: Failed to load model: " + word2vecModel);
      e.printStackTrace();
    }
    return model;
  }

  /*
   * Read a string from the binary model (System default should be UTF-8):
   */
  public static String readString(DataInputStream ds) throws IOException {
    ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
    while (true) {
      byte byteValue = ds.readByte();
      if ((byteValue != 32) && (byteValue != 10)) {
        byteBuffer.write(byteValue);
      } else if (byteBuffer.size() > 0) {
        break;
      }
    }
    String word = byteBuffer.toString();
    byteBuffer.close();
    return word;
  }

  /*
   * Read a Vector - Array of Floats from the binary model:
   */
  public static float[] readFloatVector(DataInputStream ds, int vectorSize) throws IOException {
    // Vector is an Array of Floats...
    float[] vector = new float[vectorSize];
    // Floats stored as 4 bytes
    byte[] vectorBuffer = new byte[4 * vectorSize];
    // Read the full vector in a single chunk:
    ds.read(vectorBuffer);
    // Parse bytes into floats
    for (int i = 0; i < vectorSize; i++) {
      // & with 0xFF to get unsigned byte value as int
      int byte1 = (vectorBuffer[(i * 4) + 0] & 0xFF) << 0;
      int byte2 = (vectorBuffer[(i * 4) + 1] & 0xFF) << 8;
      int byte3 = (vectorBuffer[(i * 4) + 2] & 0xFF) << 16;
      int byte4 = (vectorBuffer[(i * 4) + 3] & 0xFF) << 24;
      // Encode the 4 byte values (0-255) above into a single int
      // Reverse bytes for endian compatibility
      int reverseBytes = (byte1 | byte2 | byte3 | byte4);
      vector[i] = Float.intBitsToFloat(reverseBytes);
    }
    return vector;
  }

  /*
   * // Linear search k Nearest Neighbour: public List<String> knnWords(String
   * word, int k) { if (!this.contains(word)) {
   * System.err.println("Out of vocab word: " + word); return new
   * ArrayList<String>(); }
   * 
   * List<WordSim> words = knn(word, k); List<String> ret = new
   * ArrayList<String>(); for (WordSim w : words) { ret.add(w.getString()); }
   * return ret; }
   * 
   * 
   * public List<WordSim> knn(String word, int k) { return knn(word,
   * vector(word), k); }
   * 
   * // Internal! but can be called to get similarities! public List<WordSim>
   * knn(String word, float[] vec, int k) { PriorityQueue<WordSim> kSimilarWords
   * = new PriorityQueue<WordSim>(k * 2);
   * 
   * for (Entry<String, float[]> e : store.entrySet()) { WordSim sim = new
   * WordSim(e.getKey(), VectorMath.cosineSimilarity(vec, e.getValue()));
   * kSimilarWords.add(sim); }
   * 
   * List<WordSim> col = new ArrayList<WordSim>(); // col.clear(); for (int i =
   * 0; i < k; i++) { WordSim ws = kSimilarWords.poll(); if
   * (!ws.getString().equalsIgnoreCase(word)) { col.add(ws); } } return col; }
   */

  // WITH PREFIX:
  /*
   * public List<WordSim> knn(String word, int k, String prefix) { if
   * (!this.contains(word)) { System.err.println("Out of vocab word: " + word);
   * return new ArrayList<WordSim>(); }
   * 
   * if (prefix.equalsIgnoreCase("none")) { return knn(this.vector(word), k,
   * true); } else { return knn(this.vector(word), k, prefix); } }
   */

  /*
   * public List<WordSim> knn(float[] vec, int k, boolean withScores, String
   * prefix) { PriorityQueue<WordSim> kSimilarWords = new
   * PriorityQueue<WordSim>(k); for (Entry<String, float[]> e :
   * store.entrySet()) {
   * 
   * if (!e.getKey().startsWith(prefix)) { continue; }
   * 
   * WordSim sim = new WordSim(e.getKey(), VectorMath.cosineSimilarity(vec,
   * e.getValue()));
   * 
   * kSimilarWords.add(sim);
   * 
   * } List<WordSim> col = new ArrayList<WordSim>(); // col.clear();
   * 
   * for (int i = 0; i < (k + 1); i++) { col.add(kSimilarWords.poll()); } return
   * col; }
   * 
   * 
   * 
   * 
   * public List<WordSim> knWords(float[] vec, int k) { PriorityQueue<WordSim>
   * kSimilarWords = new PriorityQueue<WordSim>(k); for (Entry<String, float[]>
   * e : store.entrySet()) {
   * 
   * if (e.getKey().startsWith("@") || e.getKey().startsWith("#")) { continue; }
   * 
   * WordSim sim = new WordSim(e.getKey(), VectorMath.cosineSimilarity(vec,
   * e.getValue())); kSimilarWords.add(sim); } List<WordSim> col = new
   * ArrayList<WordSim>(); for (int i = 0; i < k; i++) {
   * col.add(kSimilarWords.poll()); } return col; }
   * 
   * void printSims(String w, List<WordSim> sims) { for (WordSim s : sims) {
   * String str = String.format("%s %s %.4f", w, s.getString(), s.getDouble());
   * System.out.println(str); } }
   */

  public static void main(String[] args) throws InterruptedException {

    for (int j = 0; j < 10; j++) {

      long startTime = System.nanoTime();

      W2vSpace w2v =
          W2vSpace
              .load("/home/igor/git/word2vec-java/src/main/resources/w2v_text8_min_count-5_vector_size-50_iter-15_window-15_cbow-1_negative-25_hs-0_sample-1e4.bin");

      List<WordSim> sims = w2v.knn(w2v.vector("democrats"), 10, Filters.wordsOnly(Filters.removeWords.with("democrat", "democrats")));

      w2v.printSims("test ", sims);

      long estimatedTime = System.nanoTime() - startTime;
      float secs = estimatedTime / 1000000000.0F;
      System.out.println("TOTAL EXECUTION TIME: " + secs);

    }
  }

  @Override
  public double cosineSimilarity(FloatMatrix vec1, FloatMatrix vec2) {
    return VectorMath.cosineSimilarity(vec1, vec2);
  }

  @Override
  public double distanceSimilarity(FloatMatrix vec1, FloatMatrix vec2) {
    return VectorMath.distanceSimilarity(vec1, vec2);
  }

  @Override
  public FloatMatrix additiveSentenceVector(List<FloatMatrix> vectors) {
    return VectorMath.addFloatMatrix(vectors);
  }

}
