package org.insight.wordspace;

import java.util.List;

import org.insight.wordspace.util.Compression;

/*
 * Random Indexing
 */
public class RidxSpace extends GenericWordSpace<Integer[]> {

  public static float[] convertVector(int[] input, boolean decompress) {
    if (decompress) {
      return convertVector(Compression.decompress(input, 5000));
    } else {
      return convertVector(input);
    }
  }

  public static float[] convertVector(int[] input) {
    if (input == null) {
      return new float[5000]; // Or throw an exception?
    }

    float[] output = new float[input.length];

    for (int i = 0; i < input.length; i++) {
      output[i] = input[i];
    }
    return output;
  }

  @Override
  public double cosineSimilarity(Integer[] vec1, Integer[] vec2) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double distanceSimilarity(Integer[] vec1, Integer[] vec2) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Integer[] additiveSentenceVector(List<Integer[]> vectors) {
    // TODO Auto-generated method stub
    return null;
  }

}
