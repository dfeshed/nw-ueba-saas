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
	public String normalize(String username, String targetMachine, JSONObject message, String classifier, boolean
			updateOnly) {
		username = username.toLowerCase();
		targetMachine = targetMachine.toLowerCase();
		String ret;
		logger.debug("Normalizing user - {}", username);
		//get the list of users matching the samaccountname
		List<User> users = usernameService.getUsersBysAMAccountName(username);
		//if no users were found - return the username with the fake suffix (target machine)
		if(users.size() == 0){
			ret = username + "@" + targetMachine;
			//update or create user in mongo
			userService.updateOrCreateUserWithClassifierUsername(Classifier.valueOf(classifier), ret, ret, updateOnly,
					true);
			logger.debug("No users found, saved normalized user - {}", ret);
		}else if(users.size() == 1){
			//if only one such user was found - return the full username (including domain)
			ret = users.get(0).getUsername();
			logger.debug("One user found - {}", ret);
		}else{
			//if more than one user was found - return the post normalization that will include the source machine
			//domain as the user domain
			logger.debug("More than one user found");
			ret = postNormalize(username, convertToString(message.get(sourceMachineField)), targetMachine, classifier,
					updateOnly);
		}
		return ret;
	}

	@Override
	public String postNormalize(String username, String sourceMachine, String targetMachine, String classifier, boolean
			updateOnly) {
		String ret;
		logger.debug("Normalizing according to source machine - {}", sourceMachine);
		String sourceMachineDomain = computerService.getDomainNameForHostname(sourceMachine);
		logger.debug("Domain of source machine found - {}", sourceMachineDomain);
		//if a domain name was found for the source machine (only one machine found) - return the username with it
		if(sourceMachineDomain != null) {
			ret = username + "@" + sourceMachineDomain;
		}else{
			//could not locate domain name for the source machine or more than one machine found
			ret = username + "@" + targetMachine;
		}
		//update or create user in mongo
		userService.updateOrCreateUserWithClassifierUsername(Classifier.valueOf(classifier), ret, ret, updateOnly,
				true);
		logger.debug("Saved normalized user - {}", ret);
		return ret;
	}

	@Override
	public void afterPropertiesSet() throws Exception {}

}