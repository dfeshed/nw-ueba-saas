package fortscale.services.fe.impl;

import java.util.HashMap;
import java.util.Map;

import fortscale.domain.core.User;
import fortscale.services.fe.IVpnEventScoreInfo;

public class VpnEventScoreInfo implements IVpnEventScoreInfo{
	private User user;
	private Map<String, Object> vpnScore;
	
	public VpnEventScoreInfo(User user, Map<String, Object> vpnScore){
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
	public Map<String, Object> createMap() {
		Map<String, Object> ret = new HashMap<>(vpnScore);
		ret.put("userId", getUserId());
		ret.put("username", getUsername());
		ret.put("isUserFollowed", isUserFollowed());
		
		return ret;
	}


}
