package com.teamai.hackathon.core.nlp.service;

import com.teamai.hackathon.core.nlp.data.EvaluationRequest;
import com.teamai.hackathon.core.nlp.data.EvaluationResult;
import com.teamai.hackathon.core.nlp.data.ModelTrainRequest;

public interface DocumentCategorizer {
	
	/**
	* Categorizes the given text. The text is tokenized with the SimpleTokenizer before it
	* is passed to the feature generation.Then a best outcome is predicted as result.
	*/
	public String categorize(String text);
	
	/**
	* Trains a document categorizer model.
	* 
	* @param trainer contains Data required for the trainer
	* 
	* @return returns true on success
	*/
	public Boolean train(ModelTrainRequest trainRequest );
	
	
	/**
	* This method is used to evaluate the model
	* @param EvaluationRequest 
	* 
	* @return evaluation result
	*/
	public EvaluationResult evaluate(EvaluationRequest evaluationRequest);
}
