package fortscale.web.beans;

import org.apache.commons.lang.StringUtils;

import fortscale.domain.core.User;

public class UserSearchBean {

	private User user;
	
	
	public UserSearchBean(User user){
		this.user = user;
	}
	
	public String getName() {
		String ret = null;
		if(!StringUtils.isEmpty(user.getFirstname()) || !StringUtils.isEmpty(user.getLastname())) {
			ret = user.getFirstname() + " " + user.getLastname();
		} else {
			ret = user.getAdUserPrincipalName();
		}
		return ret;
	}

	public String getId() {
		return user.getId().toString();
	}
}
