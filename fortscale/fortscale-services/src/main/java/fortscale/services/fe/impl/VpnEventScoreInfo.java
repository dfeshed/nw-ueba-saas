package fortscale.services.fe.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fortscale.domain.core.User;
import fortscale.domain.fe.VpnScore;
import fortscale.services.fe.IVpnEventScoreInfo;

public class VpnEventScoreInfo implements IVpnEventScoreInfo{
	private User user;
	private VpnScore vpnScore;
	
	public VpnEventScoreInfo(User user, VpnScore vpnScore){
		this.vpnScore = vpnScore;
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
	public Boolean isUserFollowed(){
		return user.getFollowed();
	}

	@Override
	public String getSourceIp() {
		return vpnScore.getSource_ip();
	}

	@Override
	public Date getEventTime() {
		return vpnScore.getDate_time();
	}

	@Override
	public String getInternalIP() {
		return vpnScore.getLocal_ip();
	}

	@Override
	public double getEventScore() {
		return vpnScore.getEventScore();
	}

	@Override
	public String getStatus() {
		return vpnScore.getStatus();
	}

	@Override
	public double getEventTimeScore() {
		return vpnScore.getDate_timeScore();
	}

	@Override
	public String getCountry() {
		return vpnScore.getCountry();
	}

	@Override
	public double getCountryScore() {
		return vpnScore.getCountryScore();
	}

	@Override
	public String getRegion() {
		return vpnScore.getRegion();
	}

	@Override
	public String getCity() {
		return vpnScore.getCity();
	}

	@Override
	public String getIsp() {
		return vpnScore.getIsp();
	}

	@Override
	public String getIpusage() {
		return vpnScore.getIpusage();
	}

	@Override
	public double getRegionScore() {
		return vpnScore.getRegionScore();
	}

	@Override
	public double getCityScore() {
		return vpnScore.getCityScore();
	}


	@Override
	public Map<String, Object> createMap() {
		Map<String, Object> ret = new HashMap<>(vpnScore.allFields());
		ret.put("userId", getUserId());
		ret.put("username", getUsername());
		ret.put("isUserFollowed", isUserFollowed());
		
		return ret;
	}


}
