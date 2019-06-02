package fr.mbds;

import java.util.Arrays;

public class SingleResultDetail {

	private double score;
	private String[] contentFragments;
	private String filename;

	public SingleResultDetail(double score, String filename, String[] contentFragments) {
		this.score = score;
		this.filename = filename;
		this.contentFragments = contentFragments;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getScore() {
		return score;
	}

	public void setContentFragments(String[] contentFragments) {
		this.contentFragments = contentFragments;
	}

	public String[] getContentFragments() {
		return this.contentFragments;
	}

	public String getFilename() {
		return filename;
	}

	public String toString() {
		return String.format("SingleResultDetail[score: %f, filename: %s, contentFragments: %s]",
			score, filename, Arrays.toString(contentFragments));
	}
}