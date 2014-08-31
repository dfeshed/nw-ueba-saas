package fortscale.collection.morphlines.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Objects;


import fortscale.collection.morphlines.RecordExtensions;
import fortscale.services.impl.UsernameNormalizer;


@Configurable(preConstruction=true)
public class SSHNormalizeUsernameMorphCmdBuilder extends	NormalizeUsernameMorphCmdBuilder {
	
	@Autowired
	UsernameNormalizer sshUsernameNormalizer;
	
	@Value("${impala.data.ssh.table.field.target_machine}")
	private String targetMachineField;

	@Value("${normalizedUser.fail.filter:false}")
    private boolean dropOnFail;
	
	@Value("${user.ssh.list:}")
	private String sshUsersFile;
	private ArrayList<Pattern> sshUsersRegList;
	
	private static Logger logger = LoggerFactory.getLogger(SSHNormalizeUsernameMorphCmdBuilder.class);
	
	public SSHNormalizeUsernameMorphCmdBuilder() throws IOException{

		super();
		if (!StringUtils.isEmpty(sshUsersFile)) {
			File f = new File(sshUsersFile);
			if (f.exists() && f.isFile()) {
				ArrayList<String> usersRegex = new ArrayList<String>(FileUtils.readLines(f));
				for(String regex : usersRegex){
					sshUsersRegList.add(Pattern.compile(regex));
				}
			}else {
				logger.warn("Ssh users file not found in path: {}", sshUsersFile);
			}
		}
	}

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
	
	protected boolean toDropRecord(String normalizedUsername) {

		if (sshUsersRegList == null ||  dropOnFail == false) {
			return false;
		}
		String username = normalizedUsername.split("@")[0];
		for (Pattern userPattern : sshUsersRegList) {
			if (userPattern.matcher(username).matches()) {
				return false;
			}
		}
		return true;
	}
	
	protected String getFinalNormalizedUserName(Record inputRecord, String normalizedUserName){
		String username = RecordExtensions.getStringValue(inputRecord, usernameField);
		String targetMachine = RecordExtensions.getStringValue(inputRecord, targetMachineField);
		return Objects.firstNonNull(normalizedUserName, String.format("%s@%s", username, targetMachine));
	}
}