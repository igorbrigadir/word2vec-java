package org.insight.wordspace.util;

import java.util.HashSet;
import java.util.Set;

import org.insight.wordspace.GenericWordSpace;

public class WordSpaceUtils {

	/*
	 * Remove vectors from model:
	 */
	public static <T> GenericWordSpace<T> reduceModelVocab(GenericWordSpace<T> model, Set<String> keyVectors) {
		Set<String> vocab = new HashSet<String>(model.store.keySet());
		for (String word : vocab) {
			if (!keyVectors.contains(word)) {
				model.store.remove(word);
			}
		}
		return model;
	}

}
