package org.insight.word2vec.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.insight.word2vec.Word2Vec;


public class GloveModelLoader {

	public static Word2Vec load (String word2vecModel) {

		Word2Vec model = new Word2Vec();

		try {

			BufferedInputStream bufIn = new BufferedInputStream(new FileInputStream(word2vecModel), 131072); // 128KB Buffer
			DataInputStream ds = new DataInputStream(bufIn);

			// no header:

			List<String> vocab = FileUtils.readLines(new File("/home/igor/glove/vocab.txt"));
			int numWords = vocab.size();
			
			int vecSize = 5 + 1; // +1 Bias Term!

			System.out.println( numWords + " Words with Vectors of size " + vecSize);

			// wordvectors::

			for (int i=0; i < numWords; i++) { // 3= numwords
				// Word:
				String word = vocab.get(i).split(" ")[0];
				// Vector:
				float[] vector = readFloatVector(ds, vecSize);

				if (i < 1) {
					System.out.println(word);
					for (int j=0; j < vecSize; j++) {
						System.out.print("w " + String.format("%6f", vector[j]));
					}
					System.out.println("");
				}

				model.put(word, vector);
			}

			//context vecs:

			for (int i=0; i < numWords; i++) { // 3= numwords
				// Word:
				String word = vocab.get(i).split(" ")[0];
				// Vector:
				float[] vector = readFloatVector(ds, vecSize);

				if (i < 1) {
					System.out.println(word);
					for (int j=0; j < vecSize; j++) {
						System.out.print("~ " + String.format("%6f", vector[j]));
					}
					System.out.println("");
				}

				model.put(word, VectorMath.addAll(model.get(word), vector));

			}


			for (int i=0; i < 1; i++) { // 3= numwords
				System.out.println("sachin");
				for (int j=0; j < vecSize; j++) {
					System.out.print(" " + String.format("%6f", model.get("sachin")[j]));
				}
				System.out.println("");
			}

			ds.close();
			bufIn.close();

			System.out.println(numWords + " Words with Vectors of size " + vecSize + " LOADED!");

		} catch (IOException e) {
			System.err.println("ERROR: Failed to load model: " + word2vecModel);
			e.printStackTrace();
		}

		return model;
	}


	/*
	 * Read a string from the binary model (System default should be UTF-8):
	 */
	private static String readString(DataInputStream ds) throws IOException {

		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		while (true) {
			byte byteValue = ds.readByte();
			if (byteValue != 32 && byteValue != 10) {
				byteBuffer.write(byteValue);
			} else if (byteBuffer.size() > 0) {
				break;
			}
		}
		//byteBuffer.flush();
		String word = byteBuffer.toString();
		byteBuffer.close();
		return word;
	}


	/*
	 * Read a Vector - Array of Floats from the binary model:
	 */
	private static float[] readFloatVector(DataInputStream ds, int vectorSize) throws IOException {
		// Vector is an Array of Floats...

		float[] vector = new float[vectorSize];

		// Vector:

		for (int j=0; j < vectorSize; j++) {

			long   l = ds.readLong();
			double d = Double.longBitsToDouble(Long.reverseBytes(l));

			vector[j] = (float)d;

		}




		return vector;
	}

}


