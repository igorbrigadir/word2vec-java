package org.insight.wordspace.util;

import java.io.File;

import org.insight.wordspace.W2vSpace;

public class ConvertFormats {

  public static void main(String[] args) {

    W2vSpace txt = W2vSpace.loadText("/home/igor/git/word2vec-java/src/test/resources/small_vectors.txt");

    W2vSpace gz = W2vSpace.loadText("/home/igor/git/word2vec-java/src/test/resources/small_vectors.txt.gz");


    txt.saveAsText(new File("/home/igor/git/word2vec-java/src/test/resources/small_vectors.txt1"));
    gz.saveAsText(new File("/home/igor/git/word2vec-java/src/test/resources/small_vectors.txt2"));

  }

}
