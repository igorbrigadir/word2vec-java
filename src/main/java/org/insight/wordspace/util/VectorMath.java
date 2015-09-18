package org.insight.wordspace.util;

import java.util.List;

import org.jblas.DoubleMatrix;
import org.jblas.FloatMatrix;

public class VectorMath {

  /*
   * Vectors are normalized on load, just need dot product:
   */
  public static double cosineSimilarity(FloatMatrix vec1, FloatMatrix vec2) {
    return vec1.dot(vec2);
  }

  public static double cosineSimilarity(DoubleMatrix vec1, DoubleMatrix vec2) {
    return vec1.dot(vec2);
  }

  public static double distance(FloatMatrix vec1, final FloatMatrix vec2) {
    return vec1.distance2(vec2);
  }

  public static double distanceSimilarity(FloatMatrix vec1, final FloatMatrix vec2) {
    return 1 / (1 + vec1.distance2(vec2));
  }

  public static double distanceSimilarity(DoubleMatrix vec1, final DoubleMatrix vec2) {
    return 1 / (1 + vec1.distance2(vec2));
  }

  /*
   * Utility functions:
   */

  public static FloatMatrix normalize(FloatMatrix f) {
    return f.divi(f.norm2());
  }

  public static DoubleMatrix normalize(DoubleMatrix d) {
    return d.divi(d.norm2());
  }


  public static FloatMatrix addFloatMatrix(List<FloatMatrix> vectors) {
    if (vectors.size() < 1) {
      return null;
    }
    FloatMatrix vec = vectors.remove(0);
    for (FloatMatrix f : vectors) {
      vec.addi(f);
    }
    return vec;
  }

  public static DoubleMatrix addDoubleMatrix(List<DoubleMatrix> vectors) {
    if (vectors.size() < 1) {
      return null;
    }
    DoubleMatrix vec = vectors.remove(0);
    for (DoubleMatrix d : vectors) {
      vec.addi(d);
    }
    return vec;
  }

  /*
   * Only used by GloveModelLoader
   */
  public static DoubleMatrix addDoubleMatrix(DoubleMatrix vector1, DoubleMatrix vector2) {
    return vector1.addi(vector2);
  }


}
