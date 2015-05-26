package fortscale.services.impl;

import fortscale.domain.core.User;
import fortscale.services.ComputerService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

import static fortscale.utils.ConversionUtils.convertToString;

public class SSHUsernameNormalizer extends UsernameNormalizer {

	private ComputerService computerService;

	public ComputerService getComputerService() {
		return computerService;
	}

	public void setComputerService(ComputerService computerService) {
		this.computerService = computerService;
	}

	@Override
	public String normalize(String username, String domain){
		username = username.toLowerCase();
		domain = domain.toLowerCase();
		String ret = null;
		//get the list of users matching the samaccountname
		List<User> users = usernameService.getUsersBysAMAccountName(username);
		//if no users were found - return the username with the fake suffix (target machine)
		if(users.size() == 0){
			ret = username + "@" + domain;
		}else if(users.size() == 1){
			//if only one such user was found - return the full username (including domain)
			ret = users.get(0).getUsername();
		}
		//if more than one user was found - return null (for now, this case will be handled in postNormalize)
		return ret;
	}

	@Override
	public String postNormalize(String username, String sourceMachine) {
		String ret;
		String domain = computerService.getDomainNameForHostname(sourceMachine);
		//if a domain name was found for the source machine - return the username with it
		if(domain != null) {
			ret = username + "@" + domain;
		}else{
			//could not locate domain name for the source machine - only return the username
			ret = username.toLowerCase();
		}
		return ret;
	}

	@Override
	public void afterPropertiesSet() throws Exception {}

}