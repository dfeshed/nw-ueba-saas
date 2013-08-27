package fortscale.web.beans;

import fortscale.domain.core.User;

public class UserSearchBean {

	private User user;
	
	
	public UserSearchBean(User user){
		this.user = user;
	}
	
	public String getName() {
		return user.getFirstname() + " " + user.getLastname();
	}

	public String getId() {
		return user.getId().toString();
	}
}
