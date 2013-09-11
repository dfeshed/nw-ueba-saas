package fortscale.services.impl;

import fortscale.services.IClassifierScoreDistribution;

public class ClassifierScoreDistribution implements	IClassifierScoreDistribution {
	
	private String name;
	private int count;
	private int percentage;
	
	public ClassifierScoreDistribution(String name, int count, int percentage){
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
