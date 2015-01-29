package fortscale.streaming.service.usernameNormalization;

import fortscale.services.impl.UsernameNormalizer;
import fortscale.services.users.SSHUsersWhitelistService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static fortscale.utils.ConversionUtils.convertToString;

/**
* Date: 24/01/2015.
*/
public class SshUsernameNormalizationService extends UsernameNormalizationService {

	@Autowired
	SSHUsersWhitelistService sshUsersWhitelist;

	@Value("${impala.data.ssh.table.field.target_machine}")
	private String targetMachineField;


	@Override
	public UsernameNormalizer getUsernameNormalizer(){
		return usernameNormalizer;
	}

	@Override
	public String normalizeUsername(String username) {
		if(usernameNormalizer != null){
			return usernameNormalizer.normalize(username);
		} else{
			return super.normalizeUsername(username);
		}
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
	public String getUsernameAsNormalizedUsername(String username, JSONObject message) {

		if (usernameNormalizer == null) {
			return super.getUsernameAsNormalizedUsername(username, message);
		}

		// concat the target machine name to the username: user@target
		String targetMachine = convertToString(message.get(targetMachineField));
		return String.format("%s@%s", username, targetMachine);
	}

}
