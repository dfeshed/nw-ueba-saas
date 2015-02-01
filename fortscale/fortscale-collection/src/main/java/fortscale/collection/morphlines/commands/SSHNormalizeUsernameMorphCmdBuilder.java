package fortscale.collection.morphlines.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import org.kitesdk.morphline.api.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;



import fortscale.collection.morphlines.RecordExtensions;
import fortscale.services.users.SSHUsersWhitelistService;
import fortscale.services.impl.UsernameNormalizer;

@Deprecated
@Configurable(preConstruction=true)
public class SSHNormalizeUsernameMorphCmdBuilder extends	NormalizeUsernameMorphCmdBuilder {
	
	@Autowired
	UsernameNormalizer sshUsernameNormalizer;
	
	@Autowired
	SSHUsersWhitelistService sshUsersWhitelist;
	
	@Value("${impala.data.ssh.table.field.target_machine}")
	private String targetMachineField;

	@Value("${normalizedUser.fail.filter:false}")
    private boolean dropOnFail;

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("SSHNormalizeUsername");
	}
	
	@Override
	protected UsernameNormalizer getUsernameNormalizer(){
		return sshUsernameNormalizer;
	}

	@Override
	protected String normalizeUsername(Record record){
		if(sshUsernameNormalizer != null){
			String username = RecordExtensions.getStringValue(record, usernameField);
			return sshUsernameNormalizer.normalize(username);
		} else{
			return super.normalizeUsername(record);
		}
	}
	
	protected boolean toDropRecord(String normalizedUsername, Record inputRecord) {
		if (sshUsernameNormalizer == null) {
			return super.toDropRecord(normalizedUsername, inputRecord);
		}
		if(dropOnFail == false || normalizedUsername != null){
			return false;
		}
		ArrayList<Pattern> sshUsersRegList = sshUsersWhitelist.getSshUsersRegList();
		if (sshUsersRegList == null) {
			return true;
		}
		String username = RecordExtensions.getStringValue(inputRecord, usernameField);
		for (Pattern userPattern : sshUsersRegList) {
			if (userPattern.matcher(username).matches()) {
				return false;
			}
		}
		return true;
	}
	
	protected String getFinalNormalizedUserName(Record inputRecord, String normalizedUserName) {

		if (sshUsernameNormalizer == null) {
			return super.getFinalNormalizedUserName(inputRecord, normalizedUserName);
		}
		if (normalizedUserName != null) {
			return normalizedUserName;
		}
		String username = RecordExtensions.getStringValue(inputRecord, usernameField);
		String targetMachine = RecordExtensions.getStringValue(inputRecord, targetMachineField);
		return String.format("%s@%s", username, targetMachine);
	}
}
