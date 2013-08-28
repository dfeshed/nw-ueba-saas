package fortscale.web.beans;

import fortscale.domain.core.User;

public class UserContactInfoBean {

	private User user;
	
	public UserContactInfoBean(User user){
		this.user = user;
	}
	
	public String getMobile(){
		return user.getMobile();
	}
	
	public String getEmail(){
		return user.getEmailAddress().toString();
	}
	
	public String getPhone(){
		return user.getTelephoneNumber();
	}
}
