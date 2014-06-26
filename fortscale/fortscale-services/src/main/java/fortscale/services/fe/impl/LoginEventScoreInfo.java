package fortscale.services.fe.impl;

import java.util.HashMap;
import java.util.Map;

import fortscale.domain.core.User;
import fortscale.services.fe.ILoginEventScoreInfo;

public class LoginEventScoreInfo implements ILoginEventScoreInfo {
	
	private User user;
	private Map<String, Object> attributeValueMap;
	
	public LoginEventScoreInfo(User user, Map<String, Object> attributeValueMap){
		this.attributeValueMap = attributeValueMap;
		this.user = user;
	}

	public String getUserId() {
		return user.getId();
	}

	public String getUsername() {
		return user.getUsername();
	}
	
	public Boolean isUserFollowed() {
		return user.getFollowed();
	}

	@Override
	public Map<String, Object> createMap() {
		Map<String, Object> ret = new HashMap<>(attributeValueMap);
		ret.put("userId", getUserId());
		ret.put("username", getUsername());
		ret.put("isUserFollowed", isUserFollowed());
		
		return ret;
	}

}
