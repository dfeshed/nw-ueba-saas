package fortscale.web.beans;

import fortscale.domain.core.User;

public class UserContactInfoBean {

	private User user;
	
	public UserContactInfoBean(User user){
		this.user = user;
	}
	
	public String getMobile(){
		return user.getAdInfo().getMobile();
	}
	
	public String getEmail(){
		if(user.getAdInfo().getEmailAddress() == null){
			return null;
		}
		return user.getAdInfo().getEmailAddress().toString();
	}
	
	public String getPhone(){
		return user.getAdInfo().getTelephoneNumber();
	}
}
