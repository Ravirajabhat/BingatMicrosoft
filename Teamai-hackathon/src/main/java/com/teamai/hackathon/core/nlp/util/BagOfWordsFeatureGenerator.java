package com.teamai.hackathon.core.nlp.util;

import java.util.ArrayList;
import java.util.Collection;

import opennlp.tools.doccat.FeatureGenerator;

import com.teamai.hackathon.core.nlp.data.ClassifierDataTypes;

public class BagOfWordsFeatureGenerator implements FeatureGenerator {

	private static final String BAGOFWORD_PREFIX = "bow=";
	
	public BagOfWordsFeatureGenerator(){
		
	}

	@Override
	public Collection<String> extractFeatures(String[] text) {

		Collection<String> features = new ArrayList<String>();
		
		//Summary Bag Of word
		String[] summary = ClassifierDataTypes.SUMMARY.getTokens(text);
		for (int i = 0; i < summary.length; i++) {
			String word = summary[i];
			word=word.replaceAll(".", "");
			features.add(BAGOFWORD_PREFIX + word);
		}
		
		//Summary Bag Of word
		String[] titel = ClassifierDataTypes.TITLE.getTokens(text);
		for (int i = 0; i < titel.length; i++) {
			String word = titel[i];
			word=word.replaceAll(".", "");
			features.add(BAGOFWORD_PREFIX + word);
		}
		
		return features;
		
	}


}
