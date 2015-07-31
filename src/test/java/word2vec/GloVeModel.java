package word2vec;

import java.util.List;

import org.insight.wordspace.GloVeSpace;
import org.insight.wordspace.util.WordSim;

public class GloVeModel {

  public static void main(String[] args) {
    String vocab = "/home/igor/git/word2vec-java/src/test/resources/glove_text8_min_count-5_vector_size-50_iter-15_window-15.vocab.txt";
    String bin = "/home/igor/git/word2vec-java/src/test/resources/glove_text8_min_count-5_vector_size-50_iter-15_window-15.bin";
    String tx = "/home/igor/git/word2vec-java/src/test/resources/glove_text8_min_count-5_vector_size-50_iter-15_window-15.txt";
    String gz = "/home/igor/git/word2vec-java/src/test/resources/glove_text8_min_count-5_vector_size-50_iter-15_window-15.txt.gz";

    // c = load context vestors, b = bias term
    timeBin(GloVeSpace.load(vocab, bin, true, true), "cb");
    timeBin(GloVeSpace.load(vocab, bin, true, false), "c_");
    timeBin(GloVeSpace.load(vocab, bin, false, true), "_b");
    timeBin(GloVeSpace.load(vocab, bin, false, false), "__");

    timeBin(GloVeSpace.load(tx), "tx");
    timeBin(GloVeSpace.load(gz), "gz");
  }

  public static void timeBin(GloVeSpace mdl, String msg) {
    long startTime, estimatedTime = 0L;
    float secs = 0.0f;
    startTime = System.nanoTime();
    List<WordSim> sims = mdl.knn(mdl.vector("test"), 10);
    mdl.printSims("test ", sims);
    estimatedTime = System.nanoTime() - startTime;
    secs = estimatedTime / 1000000000.0F;
    System.out.println("BIN " + msg + " LOAD TIME: " + secs);
  }

}
