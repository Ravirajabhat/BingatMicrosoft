package com.teamai.hackathon.core.nlp.data;
import java.util.List;

public class DocumentCollection {

	private List<String> DocumentList;
	
	private String encodingType;
	
	public DocumentCollection(){}
	
	//Copy constructor to separate wish train and test set
	public DocumentCollection(DocumentCollection documentCollection,int startIndex,int endIndex){
		
		if( startIndex != endIndex 
			&& startIndex < endIndex 
			&& endIndex <= documentCollection.getDocumentList().size()){
			
			this.DocumentList=documentCollection.getDocumentList().subList(startIndex, endIndex);
			this.encodingType=documentCollection.getEncodingType();
		}
		
	}

	public List<String> getDocumentList() {
		return DocumentList;
	}

	public void setDocumentList(List<String> documentList) {
		DocumentList = documentList;
	}

	public String getEncodingType() {
		return encodingType;
	}

	public void setEncodingType(String encodingType) {
		this.encodingType = encodingType;
	}

	public int getNumberOfDocuments(){
		
		return DocumentList.size();
	
	}
}
