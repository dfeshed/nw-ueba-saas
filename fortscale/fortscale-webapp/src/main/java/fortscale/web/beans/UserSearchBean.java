package fortscale.web.beans;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;

public class UserSearchBean {

	private User user;
	
	
	public UserSearchBean(User user){
		this.user = user;
	}
	
	public String getName() {
		String ret = null;
		if(!StringUtils.isEmpty(user.getAdInfo().getFirstname()) || !StringUtils.isEmpty(user.getAdInfo().getLastname())) {
			ret = user.getAdInfo().getFirstname() + " " + user.getAdInfo().getLastname();
		} else {
			ret = user.getUsername();
		}
		return ret;
	}

	public String getId() {
		return user.getId().toString();
	}
	
	public String getUsername(){
		return user.getUsername();
	}
	
	public Map<String, String> getUsernameMap(){
		Map<String, String> ret = new HashMap<String, String>();
		for(ApplicationUserDetails appUserDetails: user.getApplicationUserDetails().values()){
			ret.put(appUserDetails.getApplicationName(), appUserDetails.getUserName());
		}
		
		return ret;
	}
}