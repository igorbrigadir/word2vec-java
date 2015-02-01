package org.insight.wordspace;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import org.insight.wordspace.util.W2VModelLoader;

/*
 * A Java wrapper for W2vSpace - Reads a pre trained model from disk, doesn't
 * load into memory!
 */
public class W2vSpaceFromDisk extends W2vSpace {

  public W2vSpace cache = new W2vSpace();

  private static final long serialVersionUID = 1L;

  private final String word2vecModel;

  public W2vSpaceFromDisk(String model) {
    this.word2vecModel = model;
  }

  public static W2vSpaceFromDisk normalizeModel(W2vSpaceFromDisk raw) {
    return raw;
  }

  /*
   * Check if word is in vocab:
   */
  @Override
  public boolean contains(String word) {

    if (cache.contains(word)) {
      return true;
    } else {

      if (vector(word) != null) {
        return true;
      } else {
        return false;
      }

    }

  }

  public boolean preCache(Set<String> words) {

    try {

      BufferedInputStream bufIn = new BufferedInputStream(new FileInputStream(word2vecModel), 131072); // 128KB
                                                                                                       // Buffer
                                                                                                       // 4096
      DataInputStream ds = new DataInputStream(bufIn);

      // Read header:
      int numWords = Integer.parseInt(W2VModelLoader.readString(ds));
      int vecSize = Integer.parseInt(W2VModelLoader.readString(ds));



      for (int i = 0; i < numWords; i++) {
        // Word:
        String word = W2VModelLoader.readString(ds);
        // Vector:
        float[] vector = W2VModelLoader.readFloatVector(ds, vecSize);

        if (words.contains(word)) {

          cache.put(word, vector);



          // Progress bar...
          if ((i % 30) == 0) {
            System.err.print("." + cache.size());
          }
          if ((i % 90) == 0) {
            System.err.println("." + cache.size() + " of " + words.size());
          }

          if (cache.size() == words.size()) {
            break;
          }

        }



      }

      ds.close();
      bufIn.close();

      System.out.println(numWords + " Words with Vectors of size " + vecSize + " LOADED!");

    } catch (IOException e) {
      System.err.println("ERROR: Failed to load model: " + word2vecModel);
      e.printStackTrace();
      return false;
    }

    return true;

  }

  /*
   * Get Vector Representation of a word
   */
  @Override
  public float[] vector(String searchWord) {

    boolean found = false;

    if (cache.contains(searchWord)) {
      return cache.vector(searchWord);
    } else {
      return null;
    }

    /*
     * try {
     * 
     * BufferedInputStream bufIn = new BufferedInputStream(new
     * FileInputStream(word2vecModel), 131072); // 128KB Buffer 4096
     * DataInputStream ds = new DataInputStream(bufIn);
     * 
     * // Read header: int numWords =
     * Integer.parseInt(W2VModelLoader.readString(ds)); int vecSize =
     * Integer.parseInt(W2VModelLoader.readString(ds));
     * 
     * 
     * for (int i=0; i < numWords; i++) { // Word: String word =
     * W2VModelLoader.readString(ds); // Vector: float[] vector =
     * W2VModelLoader.readFloatVector(ds, vecSize);
     * 
     * if (word.equalsIgnoreCase(searchWord)) {
     * 
     * cache.put(word, vector); found = true;
     * 
     * // Progress bar... if (i % 30 == 0) { System.err.print("."+cache.size());
     * } if (i % 90 == 0) { System.err.println("."+cache.size()); }
     * 
     * break;
     * 
     * }
     * 
     * 
     * 
     * 
     * }
     * 
     * ds.close(); bufIn.close();
     * 
     * //System.out.println(numWords + " Words with Vectors of size " + vecSize
     * + " LOADED!");
     * 
     * } catch (IOException e) {
     * System.err.println("ERROR: Failed to load model: " + word2vecModel);
     * e.printStackTrace(); }
     * 
     * if (found) { return cache.vector(searchWord); } else { return null; }
     */
  }

}
