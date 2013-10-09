package fortscale.services.fe.impl;

import java.util.Date;

import fortscale.domain.core.User;
import fortscale.domain.fe.VpnScore;
import fortscale.services.fe.IVpnEventScoreInfo;

public class VpnEventScoreInfo implements IVpnEventScoreInfo{
	private User user;
	private VpnScore vpnScore;
	private String sourceHostname;
	private String localHostname;
	
	public VpnEventScoreInfo(User user, VpnScore vpnScore){
		this.vpnScore = vpnScore;
		this.user = user;
	}

	public void setSourceHostname(String sourceHostname) {
		this.sourceHostname = sourceHostname;
	}

	public void setDestinationIp(String localHostname) {
		this.localHostname = localHostname;
	}

	@Override
	public String getUserId() {
		return user.getId();
	}

	@Override
	public String getUsername() {
		return user.getAdUserPrincipalName();
	}

	@Override
	public String getSourceIp() {
		return vpnScore.getSourceIp();
	}

	@Override
	public String getSourceHostname() {
		return sourceHostname;
	}

	@Override
	public Date getEventTime() {
		return vpnScore.getEventTime();
	}

	@Override
	public String getLocalIp() {
		return vpnScore.getLocalIp();
	}

	@Override
	public double getEventScore() {
		return vpnScore.getEventScore();
	}

	@Override
	public double getUserScore() {
		return vpnScore.getGlobalScore();
	}

	@Override
	public String getLocalHostname() {
		return localHostname;
	}


}
