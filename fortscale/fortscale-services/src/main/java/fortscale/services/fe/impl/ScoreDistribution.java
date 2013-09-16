package fortscale.services.fe.impl;

import fortscale.services.fe.IScoreDistribution;

public class ScoreDistribution implements	IScoreDistribution {
	
	private String name;
	private int count;
	private int percentage;
	
	public ScoreDistribution(String name, int count, int percentage){
		this.name = name;
		this.count = count;
		this.percentage = percentage;
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

}
