package org.insight.wordspace;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.insight.wordspace.util.Compression;
import org.insight.wordspace.util.VectorMath;
import org.jblas.DoubleMatrix;
import org.jblas.FloatMatrix;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.UnsafeMemoryInput;

/*
 * Random Indexing
 */
public class RidxSpace extends GenericWordSpace<int[]> {

  private static int vectorSize;

  public RidxSpace(int vectorSize) {
    RidxSpace.vectorSize = vectorSize;
  }

  @SuppressWarnings("unchecked")
  public static RidxSpace load(String ridxSpaceModel, int vectorSize) {
    RidxSpace model = new RidxSpace(vectorSize);
    System.out.println("Loading from file: " + ridxSpaceModel);
    try {
      // KRYO Store:
      HashMap<String, int[]> clazz = new HashMap<String, int[]>();
      Kryo kryo = new Kryo();
      kryo.register(clazz.getClass());
      UnsafeMemoryInput input = new UnsafeMemoryInput(new FileInputStream(ridxSpaceModel));
      clazz = kryo.readObject(input, clazz.getClass());
      for (Entry<String, int[]> e : clazz.entrySet()) {
        model.store.put(e.getKey(), Compression.decompress(e.getValue(), vectorSize));
      }
      input.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return model;
  }


  public static FloatMatrix convertVector(int[] input, boolean decompress) {
    FloatMatrix f = new FloatMatrix(convertVector(Compression.decompress(input, vectorSize)));
    return f;
  }

  public static float[] convertVector(int[] input) {
    if (input == null) {
      return new float[vectorSize]; // Or throw an exception?
    }
    float[] output = new float[input.length];
    for (int i = 0; i < input.length; i++) {
      output[i] = input[i];
    }
    return output;
  }


  @Override
  public double cosineSimilarity(int[] vec1, int[] vec2) {
    DoubleMatrix d1 = new DoubleMatrix(copyFromIntArray(vec1));
    DoubleMatrix d2 = new DoubleMatrix(copyFromIntArray(vec2));

    d1 = VectorMath.normalize(d1);
    d2 = VectorMath.normalize(d2);

    return VectorMath.cosineSimilarity(d1, d2);
  }


  @Override
  public double distanceSimilarity(int[] vec1, int[] vec2) {
    EuclideanDistance e = new EuclideanDistance();
    return e.compute(copyFromIntArray(vec1), copyFromIntArray(vec2));
  }


  @Override
  public int[] additiveSentenceVector(List<int[]> vectors) {

    int[] ret = new int[vectorSize];

    for (int[] vec : vectors) {
      // todo
    }

    return ret;

  }


  public static double[] copyFromIntArray(int[] source) {
    double[] dest = new double[source.length];
    for (int i = 0; i < source.length; i++) {
      dest[i] = source[i];
    }
    return dest;
  }


}
