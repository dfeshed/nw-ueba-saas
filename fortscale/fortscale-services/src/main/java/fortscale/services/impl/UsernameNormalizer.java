package fortscale.services.impl;

import fortscale.domain.core.User;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import java.util.List;

public class UsernameNormalizer implements InitializingBean{

	protected UsernameService usernameService;

	public UsernameService getUsernameService() {
		return usernameService;
	}

	public void setUsernameService(UsernameService usernameService) {
		this.usernameService = usernameService;
	}

	//this is the normalizer for vpn and amt events
	public String normalize(String username, String domain){
		username = username.toLowerCase();
		domain = domain.toLowerCase();
		String ret;
		//get the list of users matching the samaccountname
		List<User> users = usernameService.getUsersBysAMAccountName(username);
		//if only one such user was found - return the full username (including domain)
		if(users.size() == 1){
			ret = users.get(0).getUsername();
		} else {
			//none or more than one user was found - return the username with the fake suffix (e.g vpnConnect)
			ret = username + "@" + domain;
		}
		return ret;
	}

	public String postNormalize(String username, String sourceMachine) { return username.toLowerCase(); }

	@Override
	public void afterPropertiesSet() throws Exception {}

}