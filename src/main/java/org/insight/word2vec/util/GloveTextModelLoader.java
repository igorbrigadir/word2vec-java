package org.insight.word2vec.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.insight.word2vec.Word2Vec;


public class GloveTextModelLoader {

	public static Word2Vec load (String word2vecModel) {

		Word2Vec model = new Word2Vec();

		try {

			//InputStream fileStream = new FileInputStream(Parameters.outputDirectory() + Parameters.getModelName(param.withModel("stream"), windowEndDate) + ".gz");
			//InputStream gzipStream = new GZIPInputStream(fileStream);
			//Reader decoder = new InputStreamReader(gzipStream, "UTF-8");

			InputStream fileStream = new FileInputStream(word2vecModel);

			System.out.println("Loading GloVe: " + word2vecModel);


			Reader decoder = new InputStreamReader(fileStream, "UTF-8");

			BufferedReader buffered = new BufferedReader(decoder);

			double numWords = 0;
			String line;			

			while ((line = buffered.readLine()) != null) {
				// Split into words:
				String[] wordvec = line.split(" ");

				model.put(wordvec[0], readFloatVector(wordvec, wordvec.length-1));

				numWords++;

			} // Over entire file
			buffered.close();

			System.out.println( numWords + " Words loaded. ");

		} catch (IOException e) {
			System.err.println("ERROR: Failed to load model: " + word2vecModel);
			e.printStackTrace();
		}

		return model;
	}



	/*
	 * Read a Vector - Array of Floats from the binary model:
	 */
	private static float[] readFloatVector(String[] line, int vectorSize) throws IOException {
		// Vector is an Array of Floats...

		float[] vector = new float[vectorSize];

		// Vector:
		for (int j=1; j < vectorSize+1; j++) {
			double d = Double.parseDouble(line[j]);
			vector[j] = (float)d;
		}
		return vector;
	}

}


