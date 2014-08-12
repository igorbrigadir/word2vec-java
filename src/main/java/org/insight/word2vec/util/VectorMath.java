package org.insight.word2vec.util;

import java.util.Arrays;
import java.util.List;

public class VectorMath {


	public static double cosineSimilarity(final float[] vec1, final float[] vec2) {
		double dot = 0.0D;
		double sum1 = 0.0D;
		double sum2 = 0.0D;
		for(int i=0; i < vec1.length; i++) {
			
			dot += (vec1[i] * vec2[i]);
			sum1 += (vec1[i] * vec1[i]);
			sum2 += (vec2[i] * vec2[i]);
		
		}
		return dot / (Math.sqrt(sum1) * Math.sqrt(sum2));
	}

    // return the Euclidean distance between this and that
    public static double distanceTo(final float[] vec1, final float[] vec2) {
          	return magnitude(minus(vec1, vec2));
    }

	/*
	 * Utility functions:
	 */

	public static float[] normalize(final float[] vector) {
		double magnitude = magnitude(vector);
		for (int i=0; i < vector.length; i++) {
			Double norm = vector[i] * (1 / magnitude);
			vector[i] = norm.floatValue();
		}
		return vector;
	}

	public static double magnitude(final float[] vec) {
		double dot = 0.0D;
		for(float i : vec) {
			dot = (dot + (i * i));
		}
		return Math.sqrt( dot );
	}

	public static float[] plus(float[] v1, float[] v2) {
		float[] c = new float[v1.length];
		for (int i = 0; i < v1.length; i++) {
			c[i] = v1[i] + v2[i];
		}       
		return c;
	}

	// return this - that
	public static float[] minus(float[] v1, float[] v2) {
		float[] c = new float[v1.length];

		for (int i = 0; i < v1.length; i++){
			c[i] = v1[i] - v2[i];
		}

		return c;
	}
	
	// return this - that
	public static float[] multiply(float[] v1, float[] v2) {
		float[] c = new float[v1.length];

		for (int i = 0; i < v1.length; i++){
			c[i] = v1[i] * v2[i];
		}

		return c;
	}

	// Add All:
	public static float[] addAll(float[]... all) {
		return addAll(Arrays.asList(all));	
	}
	
	
	public static float[] addAll(List<float[]> all) {
		
		float[] result = new float[all.get(0).length];
				
		for (float[] w1 : all) {
			result = plus(result, w1);
		}	
		
		return result;	
	}




}

/*
 * 
 * 
 * /*************************************************************************
 *  Compilation:  javac Vector.java
 *  Execution:    java Vector
 *
 *  Implementation of a vector of real numbers.
 *
 *  This class is implemented to be immutable: once the client program
 *  initialize a Vector, it cannot change any of its fields
 *  (N or data[i]) either directly or indirectly. Immutability is a
 *  very desirable feature of a data type.
 *
 *
 *  % java Vector
 *  x        =  (1.0, 2.0, 3.0, 4.0)
 *  y        =  (5.0, 2.0, 4.0, 1.0)
 *  x + y    =  (6.0, 4.0, 7.0, 5.0)
 *  10x      =  (10.0, 20.0, 30.0, 40.0)
 *  |x|      =  5.477225575051661
 *  <x, y>   =  25.0
 *  |x - y|  =  5.0990195135927845
 *
 *  Note that java.util.Vector is an unrelated Java library class.
 *
 *************************************************************************/
/*
public class Vector { 

    private final int N;         // length of the vector
    private double[] data;       // array of vector's components

    // create the zero vector of length N
    public Vector(int N) {
        this.N = N;
        this.data = new double[N];
    }

    // create a vector from an array
    public Vector(double[] data) {
        N = data.length;

        // defensive copy so that client can't alter our copy of data[]
        this.data = new double[N];
        for (int i = 0; i < N; i++)
            this.data[i] = data[i];
    }

    // create a vector from either an array or a vararg list
    // this constructor uses Java's vararg syntax to support
    // a constructor that takes a variable number of arguments, such as
    // Vector x = new Vector(1.0, 2.0, 3.0, 4.0);
    // Vector y = new Vector(5.0, 2.0, 4.0, 1.0);
/*
    public Vector(double... data) {
        N = data.length;

        // defensive copy so that client can't alter our copy of data[]
        this.data = new double[N];
        for (int i = 0; i < N; i++)
            this.data[i] = data[i];
    }
 */ /*
    // return the length of the vector
    public int length() {
        return N;
    }

    // return the inner product of this Vector a and b
    public double dot(Vector that) {
        if (this.N != that.N) throw new RuntimeException("Dimensions don't agree");
        double sum = 0.0;
        for (int i = 0; i < N; i++)
            sum = sum + (this.data[i] * that.data[i]);
        return sum;
    }

    // return the Euclidean norm of this Vector
    public double magnitude() {
        return Math.sqrt(this.dot(this));
    }

    // return the Euclidean distance between this and that
    public double distanceTo(Vector that) {
        if (this.N != that.N) throw new RuntimeException("Dimensions don't agree");
        return this.minus(that).magnitude();
    }

    // return this + that
    public Vector plus(Vector that) {
        if (this.N != that.N) throw new RuntimeException("Dimensions don't agree");
        Vector c = new Vector(N);
        for (int i = 0; i < N; i++)
            c.data[i] = this.data[i] + that.data[i];
        return c;
    }

    // return this - that
    public Vector minus(Vector that) {
        if (this.N != that.N) throw new RuntimeException("Dimensions don't agree");
        Vector c = new Vector(N);
        for (int i = 0; i < N; i++)
            c.data[i] = this.data[i] - that.data[i];
        return c;
    }

    // return the corresponding coordinate
    public double cartesian(int i) {
        return data[i];
    }

    // create and return a new object whose value is (this * factor)
    public Vector times(double factor) {
        Vector c = new Vector(N);
        for (int i = 0; i < N; i++)
            c.data[i] = factor * data[i];
        return c;
    }


    // return the corresponding unit vector
    public Vector direction() {
        if (this.magnitude() == 0.0) throw new RuntimeException("Zero-vector has no direction");
        return this.times(1.0 / this.magnitude());
    }

    // return a string representation of the vector
    public String toString() {
        String s = "(";
        for (int i = 0; i < N; i++) {
            s += data[i];
            if (i < N-1) s+= ", "; 
        }
        return s + ")";
    }


    // test client
    public static void main(String[] args) {
        double[] xdata = { 1.0, 2.0, 3.0, 4.0 };
        double[] ydata = { 5.0, 2.0, 4.0, 1.0 };

        Vector x = new Vector(xdata);
        Vector y = new Vector(ydata);

        System.out.println("x        =  " + x);
        System.out.println("y        =  " + y);
        System.out.println("x + y    =  " + x.plus(y));
        System.out.println("10x      =  " + x.times(10.0));
        System.out.println("|x|      =  " + x.magnitude());
        System.out.println("<x, y>   =  " + x.dot(y));
        System.out.println("|x - y|  =  " + x.minus(y).magnitude());
    }
}

  */
