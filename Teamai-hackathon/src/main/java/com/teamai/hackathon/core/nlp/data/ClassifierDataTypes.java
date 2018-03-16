package com.teamai.hackathon.core.nlp.data;

public enum ClassifierDataTypes {
	AUTHORS(-1), TITLE(2), SUMMARY(3);
	
	int startPosIdx;
	
	ClassifierDataTypes(int startPos) {
		this.startPosIdx = startPos;
	}
	
	public int getStartPos(String[] tokens) {
		if (this.equals(AUTHORS)) {
			return 4;
		}
		return Integer.valueOf(tokens[startPosIdx]);
	}
	
	public String[] getTokens(String[] tokens) {
		int start = getStartPos(tokens);
		int end = getEndPos(tokens);
		String[] sub = new String[end - start];
		System.arraycopy(tokens, start, sub, 0, end - start);
		return sub;
	}
	
	public int getEndPos(String[] tokens) {
		int ordinal = this.ordinal();
		if (this.equals(SUMMARY)) {
			return tokens.length;
		}
		ClassifierDataTypes next = values()[ordinal + 1];
		return Integer.valueOf(tokens[next.startPosIdx]);
	}
	
}