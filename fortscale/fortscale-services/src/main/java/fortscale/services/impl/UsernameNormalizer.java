package fortscale.services.impl;

import org.springframework.beans.factory.InitializingBean;

public class UsernameNormalizer implements InitializingBean{

	protected UsernameService usernameService;

	public UsernameService getUsernameService() {
		return usernameService;
	}

	public void setUsernameService(UsernameService usernameService) {
		this.usernameService = usernameService;
	}

	public String normalize(String username, String domain){
		username = username.toLowerCase();
		domain = domain.toLowerCase();
		String ret = null;
		if(usernameService.isOnlyOneUserExists(username)){
			ret = username + "@" + domain;
		}
		return ret;
	}

	@Override
	public void afterPropertiesSet() throws Exception {}

}