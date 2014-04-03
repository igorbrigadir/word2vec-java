package org.insight.word2vec.util;

public class WordSim implements Comparable<WordSim>{

	private String s;
	private Double d;

	public WordSim(String s, Double d)
	{
		this.s = s;
		this.d = d;
	}

	public String getString()
	{
		return this.s;
	}

	public Double getDouble()
	{
		return this.d;
	}

	@Override
	public int compareTo(WordSim other)
	{
		// Reverse:
		return (-1 * getDouble().compareTo(other.getDouble()));
	}

	@Override
	public String toString(){
		
		return "\n" + this.s + " : " + this.d;
		
	}

}


