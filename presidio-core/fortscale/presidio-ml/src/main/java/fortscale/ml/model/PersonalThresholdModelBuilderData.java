package fortscale.ml.model;

public class PersonalThresholdModelBuilderData {
	private int numOfContexts;
	private double organizationKTopProbOfHighScore;
	private long numOfOrganizationScores;

	public PersonalThresholdModelBuilderData setNumOfContexts(int numOfContexts) {
		this.numOfContexts = numOfContexts;
		return this;
	}

	public int getNumOfContexts() {
		return numOfContexts;
	}

	public PersonalThresholdModelBuilderData setOrganizationKTopProbOfHighScore(double organizationKTopProbOfHighScore) {
		this.organizationKTopProbOfHighScore = organizationKTopProbOfHighScore;
		return this;
	}

	public double getOrganizationKTopProbOfHighScore() {
		return organizationKTopProbOfHighScore;
	}

	public PersonalThresholdModelBuilderData setNumOfOrganizationScores(long numOfOrganizationScores) {
		this.numOfOrganizationScores = numOfOrganizationScores;
		return this;
	}

	public long getNumOfOrganizationScores() {
		return numOfOrganizationScores;
	}
}
