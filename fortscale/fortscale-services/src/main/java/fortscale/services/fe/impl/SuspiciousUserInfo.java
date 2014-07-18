package fortscale.services.fe.impl;

import fortscale.services.fe.ISuspiciousUserInfo;

public class SuspiciousUserInfo implements ISuspiciousUserInfo {
	private String classifierId;
	private String userId;
	private String username;
	private String displayName;
	private int score;
	private double trend;
	private Boolean isUserFollowed;
	
	public SuspiciousUserInfo(String classifierId, String userId, String username, String displayName, int score, double trend, Boolean isUserFollowed){
		this.classifierId = classifierId;
		this.score = score;
		this.username = username;
		this.displayName = displayName;
		this.trend = trend;
		this.userId = userId;
		this.isUserFollowed = isUserFollowed;
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

	@Override
	public String getUserId() {
		return userId;
	}

	@Override
	public Boolean getIsUserFollowed() {
		return isUserFollowed;
	}

	public String getClassifierId() {
		return classifierId;
	}

	public String getDisplayName() {
		return displayName;
	}
	
}
