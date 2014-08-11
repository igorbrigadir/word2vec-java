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

	public static Word2Vec load (String vocabFile, String word2vecModel, int vecSize, boolean withContexts, boolean bias) {

		Word2Vec model = new Word2Vec();

		try {

			FileInputStream fs = new FileInputStream(word2vecModel+".glove.bin");

			BufferedInputStream bufIn = new BufferedInputStream(fs, 131072); // 128KB Buffer
			DataInputStream ds = new DataInputStream(bufIn);

			List<String> vocab = FileUtils.readLines(new File(vocabFile));
			long numWords = vocab.size();

			// vector size = num of bytes in total / 16 / vocab
			//int vecSize = (int) (fs.getChannel().size() / 16 / (numWords - 1));

			System.out.println( numWords + " Words with Vectors of size " + vecSize);

			// wordvectors::

			for (int i=0; i < numWords; i++) { // 3= numwords
				// Word:
				String word = vocab.get(i).split(" ")[0];
				// Vector:
				
				float[] vector = bias ? readFloatVector(ds, vecSize, true) : readFloatVector(ds, vecSize);

				model.put(word, vector);
			}

			//context vecs:

			if (withContexts) {

				for (int i=0; i < numWords; i++) { // 3= numwords
					// Word:
					String word = vocab.get(i).split(" ")[0];
					// Vector:
					float[] vector = bias ? readFloatVector(ds, vecSize, true) : readFloatVector(ds, vecSize);

					model.put(word, VectorMath.addAll(model.get(word), vector));
				}
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
		
		// Bias term: 
		ds.readLong();

		return vector;
	}
		
	private static float[] readFloatVector(DataInputStream ds, int vectorSize, boolean bias) throws IOException {
		// Vector is an Array of Floats...

		float[] vector = new float[vectorSize+1];

		// Vector:

		for (int j=0; j < vectorSize+1; j++) {

			long   l = ds.readLong();
			double d = Double.longBitsToDouble(Long.reverseBytes(l));

			vector[j] = (float)d;

		}
		return vector;
	}

}


