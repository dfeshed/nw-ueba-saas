package fortscale.web.beans;

import fortscale.domain.core.User;
import fortscale.services.UserApplication;

public class ApplicationUserDetailsBean {

	private User user;
	private UserApplication app;
	
	public ApplicationUserDetailsBean(User user, UserApplication app){
		this.user = user;
		this.app = app;
	}
	
	public String getAppId(){
		return app.getId();
	}
	
	public String getUsername(){
		return user.getApplicationUserDetails().get(app.getId()).getUserName();
	}
	
	public String getUserId(){
		return user.getId();
	}
}
