package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;

import org.kitesdk.morphline.api.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import fortscale.collection.morphlines.RecordExtensions;
import fortscale.services.impl.UsernameNormalizer;


@Configurable(preConstruction=true)
public class SSHNormalizeUsernameMorphCmdBuilder extends	NormalizeUsernameMorphCmdBuilder {
	
	@Autowired
	UsernameNormalizer sshUsernameNormalizer;
	
	@Value("${impala.data.ssh.table.field.target_machine}")
	private String targetMachineField;

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
			String ret = sshUsernameNormalizer.normalize(username);
			if(ret == null){
				String targetMachine = RecordExtensions.getStringValue(record, targetMachineField);
				ret = String.format("%s@%s", username, targetMachine);
			}
			return ret;
		} else{
			return super.normalizeUsername(record);
		}
	}
	
	protected boolean toDropRecord(String normalizedUsername){
		 return false;
	}
}
