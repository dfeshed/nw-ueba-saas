package fortscale.services.fe.impl;

import java.util.List;

import fortscale.services.fe.ISuspiciousUserInfo;

public class SuspiciousUserInfo implements ISuspiciousUserInfo {
	private String classifierId;
	private String userId;
	private String username;
	private String displayName;
	private int score;
	private double trend;
	private Boolean isUserFollowed;
	private List<String> userTags;
	
	public SuspiciousUserInfo(String classifierId, String userId, String username, String displayName, int score, double trend, Boolean isUserFollowed, List<String> userTags){
		this.classifierId = classifierId;
		this.score = score;
		this.username = username;
		this.displayName = displayName;
		this.trend = trend;
		this.userId = userId;
		this.isUserFollowed = isUserFollowed;
		this.userTags = userTags;
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

	@Override
	public String getClassifierId() {
		return classifierId;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public List<String> getUserTags() {
		return userTags;
	}
}
