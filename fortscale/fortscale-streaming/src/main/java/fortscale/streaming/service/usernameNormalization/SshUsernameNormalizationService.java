package fortscale.streaming.service.usernameNormalization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import fortscale.services.impl.UsernameNormalizer;
import fortscale.services.users.SSHUsersWhitelistService;
import net.minidev.json.JSONObject;
import java.util.ArrayList;
import java.util.regex.Pattern;
import static fortscale.utils.ConversionUtils.convertToString;

/**
* Date: 24/01/2015.
*/
public class SshUsernameNormalizationService extends UsernameNormalizationService {

	@Autowired
	SSHUsersWhitelistService sshUsersWhitelist;

	@Value("${impala.data.ssh.table.field.hostname}")
	private String sourceMachineField;

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
	public String getUsernameAsNormalizedUsername(String username, String targetMachine, JSONObject message,
			UsernameNormalizationConfig
			configuration) {

		if (usernameNormalizer == null) {
			return super.getUsernameAsNormalizedUsername(username, targetMachine, message, configuration);
		}

		// concat the target machine name to the username: user@target
		String sourceMachine = convertToString(message.get(sourceMachineField));
		return usernameNormalizer.postNormalize(username, sourceMachine, targetMachine, configuration.getClassifier(),
				configuration.getUpdateOnlyFlag());

	}

}