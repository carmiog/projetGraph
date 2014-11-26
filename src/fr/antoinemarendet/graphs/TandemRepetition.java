package fr.antoinemarendet.graphs;

public class TandemRepetition {
	private int startPosition;
	private int endPosition;
	private String motif;
	private int numberOfRepetitions;

	public TandemRepetition(int startPosition, int numberOfRepetitions, String motif) {
		this.startPosition = startPosition;
		this.numberOfRepetitions = numberOfRepetitions;
		this.motif = motif;
		this.endPosition = startPosition + numberOfRepetitions * motif.length();
	}

	public int getStartPosition() {
		return startPosition;
	}

	public int getEndPosition() {
		return endPosition;
	}

	public String getMotif() {
		return motif;
	}

	public int getNumberOfRepetitions() {
		return numberOfRepetitions;
	}

	@Override
	public String toString() {
		return motif + "(n:" + getNumberOfRepetitions() + ", start: " + getStartPosition() + ")";
	}
}
