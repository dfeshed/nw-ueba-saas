package fortscale.services.impl;

import fortscale.services.ComputerService;
import fortscale.utils.logging.Logger;

import java.util.List;

public class SSHUsernameNormalizer extends UsernameNormalizer {

	private static Logger logger = Logger.getLogger(UsernameNormalizer.class);

	private ComputerService computerService;

	public ComputerService getComputerService() {
		return computerService;
	}

	public void setComputerService(ComputerService computerService) {
		this.computerService = computerService;
	}

	@Override
	public String normalize(String username, String targetMachine, String classifier, boolean updateOnly) {
		String ret;
		serviceMetrics.normalizeUsernameSSH++;
		logger.debug("Normalizing user - {}", username);

		//get the list of users matching the samaccountname
		List<String> users = samAccountNameService.getUsersBysAMAccountName(username);
		//if no users were found - return the username with the fake suffix (target machine)
		if(users.size() == 0){
			serviceMetrics.noSAMAccountFoundSSH++;
			ret = username + "@" + targetMachine;
			ret = ret.toLowerCase();
			//update or create user in mongo
			userService.updateOrCreateUserWithClassifierUsername(classifier, ret, ret, updateOnly, true);
			logger.debug("No users found, saved normalized user - {}", ret);
		} else if(users.size() == 1) {
			serviceMetrics.oneSAMAccountFoundSSH++;
			//if only one such user was found - return the full username (including domain)
			ret = users.get(0);
			logger.debug("One user found - {}", ret);
		}else {
			//if more than one user was found - return the post normalization that will include the source machine
			//domain as the user domain
			serviceMetrics.moreThanOneSAMAccountFoundSSH++;
			logger.debug("More than one user found");
			ret = postNormalize(username, targetMachine, classifier, updateOnly);
		}
		return ret;
	}

	@Override
	public String postNormalize(String username, String targetMachine, String classifierId, boolean updateOnly) {
		String ret;
		logger.debug("Normalizing according to target machine - {}", targetMachine);
		String targetMachineDomain = computerService.getDomainNameForHostname(targetMachine);
		logger.debug("Domain of target machine found - {}", targetMachineDomain);
		//if a domain name was found for the target machine (only one machine found) - return the username with it
		if (targetMachineDomain != null) {
			ret = username + "@" + targetMachineDomain;
		} else {
			//could not locate domain name for the target machine or more than one machine found
			ret = username + "@" + targetMachine;
		}
		ret = ret.toLowerCase();
		//update or create user in mongo
		serviceMetrics.updateOrCreateUserSSH++;
		userService.updateOrCreateUserWithClassifierUsername(classifierId, ret, ret, updateOnly,
				true);
		logger.debug("Saved normalized user - {}", ret);
		return ret;
	}

}

