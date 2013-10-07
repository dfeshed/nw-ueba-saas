package fortscale.services.fe.impl;

import fortscale.services.fe.IScoreDistribution;

public class ScoreDistribution implements	IScoreDistribution {
	
	private String name;
	private int count;
	private int percentage;
	private int lowestScore;
	private int highestScore;
	
	public ScoreDistribution(String name, int count, int percentage, int lowestScore, int highestScore){
		this.name = name;
		this.count = count;
		this.percentage = percentage;
		this.lowestScore = lowestScore;
		this.highestScore = highestScore;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public int getPercentage() {
		return percentage;
	}

	@Override
	public int getLowestScore() {
		return lowestScore;
	}

	@Override
	public int getHighestScore() {
		return highestScore;
	}

}
