package org.insight.wordspace;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.insight.wordspace.util.VectorMath;
import org.jblas.FloatMatrix;

/*
 * A Java wrapper for W2v - Only Reads a pre trained model!
 */
public class W2vSpace extends GenericWordSpace<FloatMatrix> {

  public static W2vSpace load(String word2vecModel) {
    W2vSpace model = new W2vSpace();
    try (DataInputStream ds = new DataInputStream(new BufferedInputStream(new FileInputStream(word2vecModel), 131072))) {
      // Read header:
      int numWords = Integer.parseInt(readString(ds));
      int vecSize = Integer.parseInt(readString(ds));
      for (int i = 0; i < numWords; i++) {
        // Word:
        String word = readString(ds);
        // Unit Vector
        FloatMatrix f = new FloatMatrix(readFloatVector(ds, vecSize));
        model.store.put(word, VectorMath.normalize(f));
      }
      System.out.println(String.format("Loaded %s words, vector size %s", numWords, vecSize));
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
