package org.insight.wordspace;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.insight.wordspace.util.Filters;
import org.insight.wordspace.util.VectorMath;

/*
 * A Java wrapper for W2v - Only Reads a pre trained model!
 */
public class W2vSpace extends WordVectorSpace<float[]> {

  public static W2vSpace load(String word2vecModel) {
    W2vSpace model = new W2vSpace();
    try (DataInputStream ds = new DataInputStream(new BufferedInputStream(new FileInputStream(word2vecModel), 131072))) {
      // Read header:
      int numWords = Integer.parseInt(readString(ds));
      int vecSize = Integer.parseInt(readString(ds));
      // System.out.println(numWords + " Words, Vector size " + vecSize);
      for (int i = 0; i < numWords; i++) {
        // Word:
        String word = readString(ds);
        // Vector:
        float[] vector = readFloatVector(ds, vecSize);
        // Unit Vector
        model.store.put(word, VectorMath.normalize(vector));
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

  public static void main(String[] args) {

    W2vSpace w2v = new W2vSpace();

    // System.out.println(w2v.sentenceVector("@quick #brown fox and",
    // Filters.removeMentions));

    // System.out.println(w2v.sentenceVector("@quick #brown fox and",
    // Filters.removeHashtags));

    // System.out.println(w2v.sentenceVector("@quick #brown fox and",
    // Filters.removeHashtags, Filters.removeMentions));


    // System.out.println(w2v.sentenceVector("@quick #brown fox and",
    // Filters.removeStopwords));

    System.out.println(w2v.sentenceVector("@quick #brown fox and foo", Filters.removeHashtags, Filters.removeMentions, Filters.removeStopwords));

    System.out.println(w2v.sentenceVector("@quick #brown fox and foo", Filters.removeWords, Filters.removeHashtags, Filters.removeMentions,
        Filters.removeStopwords));

    System.out.println("rm'F:"
        + w2v.sentenceVector("@quick #brown fox and foo", Filters.removeWords.with("fox"), Filters.removeHashtags, Filters.removeMentions,
            Filters.removeStopwords));

    System.out.println("inv*:"
        + w2v.sentenceVector("@quick #brown fox and foo", Filters.invertRemove, Filters.removeHashtags, Filters.removeMentions, Filters.removeStopwords));

    System.out.println("inv&:"
        + w2v.sentenceVector("@quick #brown #yellow fox and foo", Filters.invertRemove.with("and", "#brown"), Filters.removeHashtags, Filters.removeMentions,
            Filters.removeStopwords));



    System.out.println();

    /*
     * for (int j = 0; j < 10; j++) {
     * 
     * long startTime = System.nanoTime();
     * 
     * W2vSpace w2v = W2VModelLoader.load(
     * "/home/igor/git/word2vec-java/src/main/resources/vectors.bin");
     * 
     * long estimatedTime = System.nanoTime() - startTime; float secs =
     * estimatedTime / 1000000000.0F; System.out.println("TOTAL Load TIME: " +
     * secs);
     * 
     * 
     * startTime = System.nanoTime();
     * 
     * int i = 100; for (String word : w2v.store.keySet()) { w2v.knn(word, 20);
     * i--; if (i < 0) { break; } }
     * 
     * w2v.printSims("king", w2v.knn("king", 10)); w2v.printSims("queen",
     * w2v.knn("queen", 10));
     * 
     * 
     * estimatedTime = System.nanoTime() - startTime; secs = estimatedTime /
     * 1000000000.0F; System.out.println("TOTAL EXECUTION TIME: " + secs);
     */
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
