package org.insight.word2vec;

public interface WordVectorSpace {
	
	public boolean contains(String word);
	
	public float[] vector(String word);
	
	public float[] sentenceVector(boolean filter, String sentence);
	
	public float[] sentenceVector(boolean filter, String... words);

}
