package com.teamai.hackathon.core.nlp.data;

public class EvaluationResult {

	/**
	 * accuracy = correctly categorized documents / total documents
	 *
	 * @return the arithmetic mean of all precision scores
	 */
	private double accuracy;
	
	/**
	 * Retrieves the arithmetic mean of the precision scores calculated for each
	 * evaluated sample.
	 *
	 * @return the arithmetic mean of all precision scores
	 */
	private double precision;

	/**
	 * Retrieves the arithmetic mean of the recall score calculated for each
	 * evaluated sample.
	 *
	 * @return the arithmetic mean of all recall scores
	 */
	private double recall;

	/**
	 * Retrieves the f-measure score.
	 *
	 * f-measure = 2 * precision * recall / (precision + recall)
	 *
	 * @return the f-measure or -1 if precision + recall <= 0
	 */
	private double fmeasure;

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	
	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}

	public double getFmeasure() {
		return fmeasure;
	}

	public void setFmeasure(double fmeasure) {
		this.fmeasure = fmeasure;
	}

	@Override
	public String toString() {
		return "Accuracy:"+Double.toString(accuracy);
	}

}
