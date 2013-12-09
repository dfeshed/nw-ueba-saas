package fortscale.services.impl;

import fortscale.services.IUserScore;

public class UserScore implements IUserScore {
	private String userId;
	private String id;
	private String name;
	private int score;
	private int avgScore;
	
	public UserScore(String userId, String id, String name, int score, int avgScore){
		this.userId = userId;
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
	public int getScore() {
		return score;
	}

	@Override
	public int getAvgScore() {
		return avgScore;
	}

	@Override
	public String getUserId() {
		return userId;
	}

}
