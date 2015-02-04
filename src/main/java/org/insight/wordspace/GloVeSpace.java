package org.insight.wordspace;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.insight.wordspace.util.VectorMath;
import org.insight.wordspace.util.WordSim;
import org.jblas.DoubleMatrix;

/*
 * A Java wrapper for GloVe - Only Reads a pre trained model!
 */
public class GloVeSpace extends GenericWordSpace<DoubleMatrix> {

  /*
   * Read .txt or .txt.gz model:
   */
  public static GloVeSpace load(String gloVeModel) {
    GloVeSpace model = new GloVeSpace();

    try {

      Reader decoder;

      if (gloVeModel.endsWith("gz")) {
        decoder = new InputStreamReader(new GZIPInputStream(new FileInputStream(gloVeModel)), "UTF-8");
      } else {
        decoder = new InputStreamReader(new FileInputStream(gloVeModel), "UTF-8");
      }

      BufferedReader buffered = new BufferedReader(decoder);

      long numWords = 0;

      String line;
      while ((line = buffered.readLine()) != null) {
        // Split into words:
        String[] wordvec = StringUtils.split(line, ' ');
        if (wordvec.length < 2) {
          break;
        }
        double[] vec = readDoubleVector(wordvec);
        model.store.put(wordvec[0], new DoubleMatrix(vec));
        numWords++;
      }

      decoder.close();
      buffered.close();

      int vecSize = model.store.entrySet().iterator().next().getValue().length;
      System.out.println(String.format("Loaded %s words, vector size %s", numWords, vecSize));

    } catch (IOException e) {
      System.err.println("ERROR: Failed to load model: " + gloVeModel);
      e.printStackTrace();
    }

    return model;
  }

  /*
   * Equivalent to Text model: Contexts, no bias
   */
  public static GloVeSpace load(String vocabFile, String gloVeModel) {
    return load(vocabFile, gloVeModel, true, false);
  }

  /*
   * Read binary model, includes bias term, context vectors:
   */
  public static GloVeSpace load(String vocabFile, String gloVeModel, boolean withContexts, boolean bias) {
    GloVeSpace model = new GloVeSpace();

    try {

      FileInputStream in = new FileInputStream(gloVeModel);
      DataInputStream ds = new DataInputStream(new BufferedInputStream(in, 131072));

      List<String> vocab = FileUtils.readLines(new File(vocabFile));
      long numWords = vocab.size();

      // Vector Size = num of bytes in total / 16 / vocab
      int vecSize = (int) (in.getChannel().size() / 16 / numWords) - 1;

      // Word Vectors:
      for (int i = 0; i < numWords; i++) {
        String word = StringUtils.split(vocab.get(i), ' ')[0];
        double[] vector = readDoubleVector(ds, vecSize, bias);
        model.store.put(word, new DoubleMatrix(vector));
      }

      // Context Vectors:
      if (withContexts) {
        for (int i = 0; i < numWords; i++) {
          String word = StringUtils.split(vocab.get(i), ' ')[0];
          double[] vector = readDoubleVector(ds, vecSize, bias);
          model.store.put(word, VectorMath.addDoubleMatrix(model.store.get(word), new DoubleMatrix(vector)));
        }
      }

      // Unit Vectors:
      for (Entry<String, DoubleMatrix> e : model.store.entrySet()) {
        model.store.put(e.getKey(), VectorMath.normalize(e.getValue()));
      }

      System.out.println(String.format("Loaded %s words, vector size %s", numWords, vecSize));

    } catch (IOException e) {
      System.err.println("ERROR: Failed to load model: " + gloVeModel);
      e.printStackTrace();
    }

    return model;
  }

  /*
   * Read a Vector - Array from text file:
   */
  private static double[] readDoubleVector(String[] line) throws IOException {
    int vectorSize = line.length;
    double[] vector = new double[vectorSize - 1];
    for (int j = 1; j < vectorSize; j++) {
      try {
        double d = Double.parseDouble(line[j]);
        vector[j - 1] = d;
      } catch (NumberFormatException e) {
        System.err.println("ERROR Parsing: " + line + " " + e.getMessage());
        vector[j - 1] = 0.0f;
      }
    }
    return vector;
  }

  /*
   * Read a Vector - Array from binary file:
   */
  private static double[] readDoubleVector(DataInputStream ds, int vectorSize, boolean bias) throws IOException {
    if (bias) {
      vectorSize += 1; // Include Bias
    }
    double[] vector = new double[vectorSize];
    for (int j = 0; j < vectorSize; j++) {
      long l = ds.readLong();
      double d = Double.longBitsToDouble(Long.reverseBytes(l));
      vector[j] = d;
    }
    if (!bias) {
      ds.readLong(); // Skip Bias
    }
    return vector;
  }

  public static void main(String[] args) throws InterruptedException {

    for (int j = 0; j < 1; j++) {

      long startTime, estimatedTime = 0L;
      float secs = 0.0f;

      GloVeSpace mdl;
      List<WordSim> sims;

      String vocab = "/home/igor/git/word2vec-java/src/main/resources/glove_text8_min_count-5_vector_size-50_iter-15_window-15.vocab.txt";
      String bin = "/home/igor/git/word2vec-java/src/main/resources/glove_text8_min_count-5_vector_size-50_iter-15_window-15.bin";

      String tx = "/home/igor/git/word2vec-java/src/main/resources/glove_text8_min_count-5_vector_size-50_iter-15_window-15.txt";
      String gz = "/home/igor/git/word2vec-java/src/main/resources/glove_text8_min_count-5_vector_size-50_iter-15_window-15.txt.gz";

      startTime = System.nanoTime();
      mdl = GloVeSpace.load(vocab, bin, true, true);
      mdl.saveAsText(new File("/home/igor/git/word2vec-java/src/main/resources/g_cb"));

      sims = mdl.knn(mdl.vector("democrats"), 10);
      mdl.printSims("test ", sims);

      estimatedTime = System.nanoTime() - startTime;
      secs = estimatedTime / 1000000000.0F;
      System.out.println("BIN cb LOAD TIME: " + secs);

      startTime = System.nanoTime();
      mdl = GloVeSpace.load(vocab, bin, true, false);
      mdl.saveAsText(new File("/home/igor/git/word2vec-java/src/main/resources/g_c_"));

      sims = mdl.knn(mdl.vector("democrats"), 10);
      mdl.printSims("test ", sims);

      estimatedTime = System.nanoTime() - startTime;
      secs = estimatedTime / 1000000000.0F;
      System.out.println("BIN c_ LOAD TIME: " + secs);

      startTime = System.nanoTime();
      mdl = GloVeSpace.load(vocab, bin, false, true);
      mdl.saveAsText(new File("/home/igor/git/word2vec-java/src/main/resources/g__b"));

      sims = mdl.knn(mdl.vector("democrats"), 10);
      mdl.printSims("test ", sims);

      estimatedTime = System.nanoTime() - startTime;
      secs = estimatedTime / 1000000000.0F;
      System.out.println("BIN _b LOAD TIME: " + secs);

      startTime = System.nanoTime();
      mdl = GloVeSpace.load(vocab, bin, false, false);
      mdl.saveAsText(new File("/home/igor/git/word2vec-java/src/main/resources/g___"));

      sims = mdl.knn(mdl.vector("democrats"), 10);
      mdl.printSims("test ", sims);

      estimatedTime = System.nanoTime() - startTime;
      secs = estimatedTime / 1000000000.0F;
      System.out.println("BIN __ LOAD TIME: " + secs);

      // long estimatedTime = System.nanoTime() - startTime;
      // float secs = estimatedTime / 1000000000.0F;
      // System.out.println("TOTAL EXECUTION TIME: " + secs);

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
