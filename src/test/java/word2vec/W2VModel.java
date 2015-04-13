package word2vec;

import java.util.List;

import org.insight.wordspace.W2vSpace;
import org.insight.wordspace.util.WordSim;
import org.junit.Test;

public class W2VModel {

  @Test
  public void test() {
    for (int j = 0; j < 10; j++) {

      long startTime = System.nanoTime();

      W2vSpace w2v =
          W2vSpace
              .load("/home/igor/git/word2vec-java/src/test/resources/w2v_text8_min_count-5_vector_size-50_iter-15_window-15_cbow-1_negative-25_hs-0_sample-1e4.bin");

      List<WordSim> sims = w2v.knn(w2v.vector("democrats"), 10, w2v.f.wordsOnly(w2v.f.removeWords.with("democrat", "democrats")));

      w2v.printSims("test ", sims);

      long estimatedTime = System.nanoTime() - startTime;
      float secs = estimatedTime / 1000000000.0F;
      System.out.println("TOTAL EXECUTION TIME: " + secs);

    }
  }

}
