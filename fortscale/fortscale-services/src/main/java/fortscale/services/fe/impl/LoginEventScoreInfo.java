package fortscale.services.fe.impl;

import java.util.Date;

import fortscale.domain.core.User;
import fortscale.domain.fe.AuthScore;
import fortscale.services.fe.ILoginEventScoreInfo;

public class LoginEventScoreInfo implements ILoginEventScoreInfo {
	
	private User user;
	private AuthScore authScore;
	private String sourceHostname;
	private String destinationIp;
	
	public LoginEventScoreInfo(User user, AuthScore authScore){
		this.authScore = authScore;
		this.user = user;
	}

	public void setSourceHostname(String sourceHostname) {
		this.sourceHostname = sourceHostname;
	}

	public void setDestinationIp(String destinationIp) {
		this.destinationIp = destinationIp;
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
	public String getSourceIp() {
		return authScore.getSourceIp();
	}

	@Override
	public String getSourceHostname() {
		return sourceHostname;
	}

	@Override
	public Date getEventTime() {
		return authScore.getEventTime();
	}

	@Override
	public String getDestinationIp() {
		return destinationIp;
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
	public double getUserScore() {
		return authScore.getGlobalScore();
	}

	@Override
	public String getErrorCode() {
		return authScore.getErrorCode();
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
	public double getErrorCodeScore() {
		return authScore.getErrorCodeScore();
	}

	@Override
	public double getEventTimeScore() {
		return authScore.getEventTimeScore();
	}

}
