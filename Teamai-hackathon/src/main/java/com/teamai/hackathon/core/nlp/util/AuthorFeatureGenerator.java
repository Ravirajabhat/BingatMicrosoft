package com.teamai.hackathon.core.nlp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.teamai.hackathon.core.nlp.data.ClassifierDataTypes;

import opennlp.tools.doccat.FeatureGenerator;

public class AuthorFeatureGenerator implements FeatureGenerator {

	private static final String AUTHER_PREFIX = "auth=";

	@Override
	public Collection<String> extractFeatures(String[] text) {

		Collection<String> features = new ArrayList<String>();

		String[] authorsStr = ClassifierDataTypes.AUTHORS.getTokens(text);

		List<String> authersList=new ArrayList<String>();
		StringBuilder autherName=new StringBuilder();
		for (String author : authorsStr) 
		{
			if(!author.contains(";"))
			{
				autherName.append(!autherName.toString().equals("") ?" "+ author:author);
			}
			else
			{
				String[] authSplit= author.split(";");
				if( authSplit.length > 0)
				{
					autherName.append( !autherName.toString().equals("") ?" "+ authSplit[0]:authSplit[0]);
					authersList.add(autherName.toString());
					autherName = new StringBuilder("");

					if(authSplit.length >1){
						autherName.append(authSplit[1]);
					}
					
				}else
				{
					authersList.add(autherName.toString());
					autherName = new StringBuilder("");
				}

			}
		}
		authersList.add(autherName.toString());
		
		for (String author : authersList) {
			features.add(AUTHER_PREFIX+ author);
		}

		return features;
	}

}
