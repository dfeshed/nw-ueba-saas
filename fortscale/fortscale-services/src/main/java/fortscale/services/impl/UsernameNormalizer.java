package fortscale.services.impl;

import fortscale.domain.core.User;
import fortscale.services.UserService;
import fortscale.services.fe.Classifier;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.List;

public class UsernameNormalizer implements InitializingBean{

	private static Logger logger = LoggerFactory.getLogger(UsernameNormalizer.class);

	public SamAccountNameService getSamAccountNameService() {
		return samAccountNameService;
	}

	public void setSamAccountNameService(SamAccountNameService samAccountNameService) {
		this.samAccountNameService = samAccountNameService;
	}

	protected SamAccountNameService samAccountNameService;
	protected UsernameService usernameService;
	protected UserService userService;

	public UsernameService getUsernameService() {
		return usernameService;
	}

	public void setUsernameService(UsernameService usernameService) {
		this.usernameService = usernameService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	//this is the normalizer for vpn and amt events
	public String normalize(String username, String fakeDomain, String classifier, boolean updateOnly) {
		String ret;
		logger.debug("Normalizing user - {}", username);
		//get the list of users matching the samaccountname
		List<String> users = samAccountNameService.getUsersBysAMAccountName(username);
		//if only one such user was found - return the full username (including domain)
		if(users.size() == 1) {
			ret = users.get(0);
			logger.debug("One user found - {}", ret);
		}
		else {
			logger.debug("No users found or more than one found");
			ret = postNormalize(username, fakeDomain, classifier, updateOnly);
		}
		return ret;
	}

	public String postNormalize(String username, String suffix, String classifier, boolean updateOnly) {
		String ret = username + "@" + suffix;
		ret = ret.toLowerCase();
		//update or create user in mongo
		userService.updateOrCreateUserWithClassifierUsername(Classifier.valueOf(classifier), ret, ret, updateOnly,
				true);
		logger.debug("Saved normalized user - {}", ret);
		return ret;
	}

	@Override
	public void afterPropertiesSet() throws Exception {}

}