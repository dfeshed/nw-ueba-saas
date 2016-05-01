
package fortscale.streaming.service.usernameNormalization;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import fortscale.services.impl.UsernameNormalizer;
import fortscale.services.users.SSHUsersWhitelistService;

/**
* Date: 24/01/2015.
*/
public class SshUsernameNormalizationService extends UsernameNormalizationService {

	@Autowired
	SSHUsersWhitelistService sshUsersWhitelist;

	@Override
	public UsernameNormalizer getUsernameNormalizer(){
		return usernameNormalizer;
	}

	@Override
	public boolean shouldDropRecord(String username, String normalizedUsername) {

		boolean shouldDrop = super.shouldDropRecord(username, normalizedUsername);
		if (usernameNormalizer == null || !shouldDrop) {
			return shouldDrop;
		}

		// check if the user is part of the SSH white list
		ArrayList<Pattern> sshUsersRegList = sshUsersWhitelist.getSshUsersRegList();
		if (sshUsersRegList == null) {
			return true;
		} else {
			for (Pattern userPattern : sshUsersRegList) {
				if (userPattern.matcher(username).matches()) {
					return false;
				}
			}
		}


		return true;
	}

	@Override
	public String getUsernameAsNormalizedUsername(String username, String targetMachine, UsernameNormalizationConfig
			configuration) {

		if (usernameNormalizer == null) {
			return super.getUsernameAsNormalizedUsername(username, targetMachine, configuration);
		}

		// concat the target machine name to the username: user@target
		return usernameNormalizer.postNormalize(username, targetMachine, configuration.getClassifier(),
				configuration.getUpdateOnlyFlag());

	}

}
