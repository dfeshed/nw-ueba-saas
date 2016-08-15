package fortscale.ml.model;

public class PersonalThresholdModelBuilderData {
	private int numOfContexts;
	private double organizationKTopProbOfHighScore;
	private int numOfOrganizationScores;

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

	public PersonalThresholdModelBuilderData setNumOfOrganizationScores(int numOfOrganizationScores) {
		this.numOfOrganizationScores = numOfOrganizationScores;
		return this;
	}

	public int getNumOfOrganizationScores() {
		return numOfOrganizationScores;
	}
}
