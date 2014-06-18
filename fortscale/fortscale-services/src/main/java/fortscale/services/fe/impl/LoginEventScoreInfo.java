package fortscale.services.fe.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fortscale.domain.core.User;
import fortscale.domain.fe.AuthScore;
import fortscale.services.fe.ILoginEventScoreInfo;

public class LoginEventScoreInfo implements ILoginEventScoreInfo {
	
	private User user;
	private AuthScore authScore;
	
	public LoginEventScoreInfo(User user, AuthScore authScore){
		this.authScore = authScore;
		this.user = user;
	}

	@Override
	public String getUserId() {
		return user.getId();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}
	
	@Override
	public Boolean isUserFollowed() {
		return user.getFollowed();
	}

	@Override
	public String getSourceIp() {
		return authScore.getSourceIp();
	}


	@Override
	public Date getEventTime() {
		return authScore.getEventTime();
	}


	@Override
	public String getDestinationHostname() {
		return authScore.getTargetId();
	}

	@Override
	public double getEventScore() {
		return authScore.getEventScore();
	}

	@Override
	public double getUserNameScore() {
		return authScore.getUserNameScore();
	}

	@Override
	public double getTargetIdScore() {
		return authScore.getTargetIdScore();
	}

	@Override
	public double getSourceIpScore() {
		return authScore.getSourceIpScore();
	}


	@Override
	public double getEventTimeScore() {
		return authScore.getEventTimeScore();
	}

	@Override
	public Map<String, Object> createMap() {
		Map<String, Object> ret = new HashMap<>(authScore.getAllFields());
		ret.put("userId", getUserId());
		ret.put("username", getUsername());
		ret.put("isUserFollowed", isUserFollowed());
		
		return ret;
	}

}
