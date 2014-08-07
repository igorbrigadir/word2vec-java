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
      int numWords = vocab.size() - 1;
      int vecSize = 50;

      System.out.println( numWords + " Words with Vectors of size " + vecSize);
     
      // Read Data:
      
      for (int i=0; i < numWords; i++) {
        // Word:
        String word = vocab.get(i).split(" ")[0];
        // Vector:
        float[] vector = readFloatVector(ds, vecSize);
       
        //FileUtils.writeStringToFile(new File(word2vecModel + "words.txt"), word + "\n", true);
        System.out.println(word);
        for (int j=0; j < vecSize; j++) {
        	System.out.print(" " + String.format("%6f", vector[j]));
        }
        System.out.println("");

        // Tradeoff here: Faster Model Load time? Don't Normalize on load, Making Lots of Calculations? Normalize on load.
 
        //  model.put(word, VectorMath.normalize(vector));
        model.put(word, vector);
  
        // Progress bar...
        //System.out.println("");
        //System.out.println(offset);
        //if (i % 10000 == 0) {
        // 	System.err.print(".");
        // }
        //if (i % 100000 == 0) {
        //	System.err.println("");
        //}
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

    //Glove stored as 8 bytes double
    byte[] vectorBuffer = new byte[8*vectorSize];

    // Read the full vector in a single chunk:
    ds.read(vectorBuffer);

    // Parse bytes into doubles
    for (int j=0; j < vectorSize; j++) {
      // & with 0xFF to get unsigned byte value as int
      int byte1 = (vectorBuffer[(j*8)+0] & 0xFF) << 0;
      int byte2 = (vectorBuffer[(j*8)+1] & 0xFF) << 8;
      int byte3 = (vectorBuffer[(j*8)+2] & 0xFF) << 16;
      int byte4 = (vectorBuffer[(j*8)+3] & 0xFF) << 24;
      int byte5 = (vectorBuffer[(j*8)+4] & 0xFF) << 32;
      int byte6 = (vectorBuffer[(j*8)+5] & 0xFF) << 40;
      int byte7 = (vectorBuffer[(j*8)+6] & 0xFF) << 48;
      int byte8 = (vectorBuffer[(j*8)+7] & 0xFF) << 56;
      // Encode the 4 byte values (0-255) above into a single int
      // Reverse bytes for endian compatibility
      long reverseBytes = (byte1 | byte2 | byte3 | byte4 | byte5 | byte6| byte7| byte8);
     // vector[j] = Float.intBitsToFloat(reverseBytes);
 
      vector[j] = (float) Double.longBitsToDouble(reverseBytes);
    }
    return vector;
  }

}


