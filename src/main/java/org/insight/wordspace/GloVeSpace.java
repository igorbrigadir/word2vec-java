package org.insight.wordspace;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.insight.wordspace.util.Filters;
import org.insight.wordspace.util.VectorMath;
import org.insight.wordspace.util.WordSim;
import org.jblas.DoubleMatrix;

/*
 * A Java wrapper for W2v - Only Reads a pre trained model!
 */
public class GloVeSpace extends WordVectorSpace<DoubleMatrix> {

  public static GloVeSpace load(String vocabFile, String gloVeModel, int vecSize, boolean withContexts, boolean bias) {
    GloVeSpace model = new GloVeSpace();
    try (DataInputStream ds = new DataInputStream(new BufferedInputStream(new FileInputStream(gloVeModel), 131072))) {

      List<String> vocab = FileUtils.readLines(new File(vocabFile));
      long numWords = vocab.size();

      /*
       * System.out.println(numWords + " Words, Vector size " + vecSize); for
       * (int i = 0; i < numWords; i++) { // Word: String word = readString(ds);
       * // Unit Vector DoubleMatrix f = new DoubleMatrix(readFloatVector(ds,
       * vecSize)); model.store.put(word, VectorMath.normalize(f)); }
       */

      // vector size = num of bytes in total / 16 / vocab
      // int vecSize = (int) (fs.getChannel().size() / 16 / (numWords - 1));

      System.out.println(numWords + " Words with Vectors of size " + vecSize);

      // wordvectors::
      for (int i = 0; i < numWords; i++) { // 3= numwords
        // Word:
        String word = vocab.get(i).split(" ")[0];
        // Vector:
        float[] vector = bias ? readDoubleVector(ds, vecSize, true) : readFloatVector(ds, vecSize);
        model.put(word, vector);
      }

      // context vecs:
      if (withContexts) {
        for (int i = 0; i < numWords; i++) { // 3= numwords
          // Word:
          String word = vocab.get(i).split(" ")[0];
          // Vector:
          float[] vector = bias ? readDoubleVector(ds, vecSize, true) : readFloatVector(ds, vecSize);
          model.put(word, VectorMath.addAll(model.get(word), vector));
        }
      }

    } catch (IOException e) {
      System.err.println("ERROR: Failed to load model: " + gloVeModel);
      e.printStackTrace();
    }
    return model;
  }

  /*
   * Read a Vector - Array of Floats from the binary model:
   */
  private static float[] readFloatVector(DataInputStream ds, int vectorSize) throws IOException {
    // Vector is an Array of Floats...
    float[] vector = new float[vectorSize];
    // Vector:
    for (int j = 0; j < vectorSize; j++) {
      long l = ds.readLong();
      double d = Double.longBitsToDouble(Long.reverseBytes(l));
      vector[j] = (float) d;
    }
    // Bias term:
    ds.readLong();
    return vector;
  }

  private static float[] readDoubleVector(DataInputStream ds, int vectorSize, boolean bias) throws IOException {
    // Vector is an Array of Doubles...
    float[] vector = new float[vectorSize + 1];
    // Vector:
    for (int j = 0; j < (vectorSize + 1); j++) {
      long l = ds.readLong();
      double d = Double.longBitsToDouble(Long.reverseBytes(l));
      vector[j] = (float) d;
    }
    return vector;
  }


  public static void main(String[] args) throws InterruptedException {

    for (int j = 0; j < 10; j++) {

      long startTime = System.nanoTime();

      GloVeSpace w2v =
          GloVeSpace
              .load("/home/igor/git/word2vec-java/src/main/resources/w2v_text8_min_count-5_vector_size-50_iter-15_window-15_cbow-1_negative-25_hs-0_sample-1e4.bin");

      List<WordSim> sims = w2v.knn(w2v.vector("democrats"), 10, Filters.wordsOnly(Filters.removeWords.with("democrat", "democrats")));

      w2v.printSims("test ", sims);

      long estimatedTime = System.nanoTime() - startTime;
      float secs = estimatedTime / 1000000000.0F;
      System.out.println("TOTAL EXECUTION TIME: " + secs);

    }
  }

  @Override
  public double cosineSimilarity(DoubleMatrix vec1, DoubleMatrix vec2) {
    return VectorMath.cosineSimilarity(vec1, vec2);
  }

  @Override
  public double distanceSimilarity(DoubleMatrix vec1, DoubleMatrix vec2) {
    return VectorMath.distanceSimilarity(vec1, vec2);
  }

  @Override
  public DoubleMatrix additiveSentenceVector(List<DoubleMatrix> vectors) {
    return VectorMath.addDoubleMatrix(vectors);
  }

}
