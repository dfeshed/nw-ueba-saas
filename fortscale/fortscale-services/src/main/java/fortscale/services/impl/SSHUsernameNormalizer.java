package fortscale.services.impl;

import fortscale.services.fe.Classifier;
import org.springframework.beans.factory.annotation.Value;
import fortscale.domain.core.User;
import fortscale.services.ComputerService;
import net.minidev.json.JSONObject;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.List;
import static fortscale.utils.ConversionUtils.convertToString;

public class SSHUsernameNormalizer extends UsernameNormalizer {

	private static Logger logger = LoggerFactory.getLogger(UsernameNormalizer.class);

	private ComputerService computerService;

	@Value("${impala.data.ssh.table.field.hostname}")
	private String sourceMachineField;

	public ComputerService getComputerService() {
		return computerService;
	}

	public void setComputerService(ComputerService computerService) {
		this.computerService = computerService;
	}

	@Override
	public String normalize(String username, String domain, JSONObject message, Classifier classifier, boolean
			updateOnly) {
		username = username.toLowerCase();
		domain = domain.toLowerCase();
		String ret;
		logger.debug("Normalizing user - {}", username);
		//get the list of users matching the samaccountname
		List<User> users = usernameService.getUsersBysAMAccountName(username);
		//if no users were found - return the username with the fake suffix (target machine)
		if(users.size() == 0){
			ret = username + "@" + domain;
			//update or create user in mongo
			userService.updateOrCreateUserWithClassifierUsername(classifier, ret, ret, updateOnly, true);
			logger.debug("No users found, saved normalized user - {}", ret);
		}else if(users.size() == 1){
			//if only one such user was found - return the full username (including domain)
			ret = users.get(0).getUsername();
			logger.debug("One user found - {}", ret);
		}else{
			//if more than one user was found - return the post normalization that will include the source machine
			//domain as the user domain
			logger.debug("More than one user found");
			ret = postNormalize(username, convertToString(message.get(sourceMachineField)), classifier, updateOnly);
		}
		return ret;
	}

	@Override
	public String postNormalize(String username, String sourceMachine, Classifier classifier, boolean updateOnly) {
		String ret;
		logger.debug("Normalizing according to source machine - {}", sourceMachine);
		String domain = computerService.getDomainNameForHostname(sourceMachine);
		logger.debug("Domain of source machine found - {}", domain);
		//if a domain name was found for the source machine - return the username with it
		if(domain != null) {
			ret = username + "@" + domain;
		}else{
			//could not locate domain name for the source machine - only return the username
			ret = username.toLowerCase();
		}
		//update or create user in mongo
		userService.updateOrCreateUserWithClassifierUsername(classifier, ret, ret, updateOnly, true);
		logger.debug("Saved normalized user - {}", ret);
		return ret;
	}

	@Override
	public void afterPropertiesSet() throws Exception {}

}