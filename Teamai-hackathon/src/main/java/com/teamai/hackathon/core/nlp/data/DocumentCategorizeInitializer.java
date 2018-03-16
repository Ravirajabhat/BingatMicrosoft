package com.teamai.hackathon.core.nlp.data;

import java.util.Map;

public class DocumentCategorizeInitializer {
	
	private String languageCode;
	private String categoryType;
	private String modelDirectoryRootPath;
	private Map<String,Object> metadata;
	
	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public String getModelDirectoryRootPath() {
		return modelDirectoryRootPath;
	}

	public void setModelDirectoryRootPath(String modelDirectoryRootPath) {
		this.modelDirectoryRootPath = modelDirectoryRootPath;
	}

	public Map<String,Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String,Object> metadata) {
		this.metadata = metadata;
	}

}
