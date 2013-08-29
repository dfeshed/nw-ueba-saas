package fortscale.services.impl;

import fortscale.services.IUserScore;

public class UserScore implements IUserScore {
	
	private String id;
	private String name;
	private Double score;
	private Double avgScore;
	
	public UserScore( String id, String name, Double score, Double avgScore){
		this.id = id;
		this.name = name;
		this.score = score;
		this.avgScore = avgScore;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Double getScore() {
		return score;
	}

	@Override
	public Double getAvgScore() {
		return avgScore;
	}

}
