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
}
