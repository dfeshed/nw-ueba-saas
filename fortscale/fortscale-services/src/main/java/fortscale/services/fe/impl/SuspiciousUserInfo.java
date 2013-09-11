package fortscale.services.fe.impl;

import fortscale.services.fe.ISuspiciousUserInfo;

public class SuspiciousUserInfo implements ISuspiciousUserInfo {
	
	private String username;
	private int score;
	private double trend;
	
	public SuspiciousUserInfo(String username, int score, double trend){
		this.score = score;
		this.username = username;
		this.trend = trend;
	}

	@Override
	public String getUserName() {
		return username;
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	public double getTrend() {
		return trend;
	}

}
