package com.teamai.hackathon.core.nlp.data;

import java.util.Map;

public class ModelTrainRequest {

	private DocumentCollection documentCollection;

	private Boolean ignoreCrossFoldEvaluation;
	
	private Map<String,Object> metadata;
	
	public DocumentCollection getDocumentCollection() {

		return documentCollection;

	}

	public void setDocumentCollection(DocumentCollection documentCollection) {
		this.documentCollection = documentCollection;
	}

	public Map<String,Object> getMetadata() {

		return metadata;

	}

	public void setMetadata(Map<String,Object> metadata) {

		this.metadata = metadata;

	}

	public Boolean getIgnoreCrossFoldEvaluation() {
		return ignoreCrossFoldEvaluation;
	}

	public void setIgnoreCrossFoldEvaluation(Boolean ignoreCrossFoldEvaluation) {
		this.ignoreCrossFoldEvaluation = ignoreCrossFoldEvaluation;
	}


	
}
