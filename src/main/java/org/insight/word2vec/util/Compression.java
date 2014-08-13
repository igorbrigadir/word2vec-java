package org.insight.word2vec.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.lemire.integercompression.FastPFOR;
import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.IntegerCODEC;
import me.lemire.integercompression.Composition;
import me.lemire.integercompression.VariableByte;


public class Compression {
	
    static final IntegerCODEC codec =  new Composition(new FastPFOR(), new VariableByte());
       
    
    

    
    public static HashMap<String,int[]> compressSemantics(HashMap<String,int[]> input) {
    	
    	HashMap<String,int[]> compressed = new HashMap<String,int[]>();
    	
    	   System.out.println("Compress...");
    	
    	for (Entry<String,int[]> e : input.entrySet()) {
    		
    		compressed.put(e.getKey(), compress(e.getValue()));
    		
    	}
    	
    	   System.out.println("...Done.");
    	return compressed;
    }
    
	public static int[] compress(int[] data) {
		
		//System.out.println("Compress:");
				        
        int[] compressed = new int[data.length+1024];// could need more
                
        // compressing
        IntWrapper inputoffset = new IntWrapper(0);
        IntWrapper outputoffset = new IntWrapper(0);
        codec.compress(data,inputoffset,data.length,compressed,outputoffset);
       // System.out.println("compressed unsorted integers from "+data.length*4/1024+"KB to "+outputoffset.intValue()*4/1024+"KB");
        // we can repack the data: (optional)
        compressed = Arrays.copyOf(compressed,outputoffset.intValue());
		
        return compressed;
	}
		
	public static int[] decompress(int[] compressed, int vectorSize) {
				
			int[] recovered = new int[vectorSize];
	        
		 	IntWrapper recoffset = new IntWrapper(0);
	        
	        codec.uncompress(compressed,new IntWrapper(0),compressed.length,recovered,recoffset);
	        
	        //if(Arrays.equals(data,recovered)) 
	         // System.out.println("data is recovered without loss");
	        //else
	       //   throw new RuntimeException("bug"); // could use assert
	       // System.out.println();
						
	        
	    //    System.out.println("...Done.");
	        
	        return recovered;
	}
	
}
