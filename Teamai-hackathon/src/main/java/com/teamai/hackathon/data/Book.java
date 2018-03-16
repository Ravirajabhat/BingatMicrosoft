package com.teamai.hackathon.data;

public class Book {

	private String recordId;
	private String topic;
	private String publicationYear;
	private String authors;
	private String title;
	private String summary;
	public String getRecordId() {
		return recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getAuthors() {
		return authors;
	}
	public void setAuthors(String authors) {
		this.authors = authors;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getPublicationYear() {
		return publicationYear;
	}
	public void setPublicationYear(String publicationYear) {
		this.publicationYear = publicationYear;
	}
	
}
