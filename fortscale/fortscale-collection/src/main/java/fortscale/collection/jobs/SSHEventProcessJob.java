package fortscale.collection.jobs;

import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import fortscale.collection.morphlines.RecordExtensions;
import fortscale.services.fe.Classifier;
import fortscale.services.impl.UsernameNormalizer;
import fortscale.utils.hdfs.split.FileSplitStrategy;
import fortscale.utils.hdfs.split.WeeklyFileSplitStrategy;

@DisallowConcurrentExecution
public class SSHEventProcessJob extends EventProcessJob {
	
	@Autowired
	UsernameNormalizer sshUsernameNormalizer;
	
	@Value("${impala.data.ssh.table.field.status}")
	private String statusFieldName;
	
	@Value("${ssh.status.success.value:accepted}")
	private String sshStatusSuccessValue;
	
	@Value("${impala.data.ssh.table.field.target_machine}")
	private String targetMachineField;
	
	@Override
	protected String normalizeUsername(Record record){
		String username = extractUsernameFromRecord(record);
		String ret = sshUsernameNormalizer.normalize(username);
		if(ret == null){
			String targetMachine = RecordExtensions.getStringValue(record, targetMachineField);
			ret = String.format("%s@%s", username, targetMachine);
		}
		
		return ret;
	}
	
	@Override
	protected FileSplitStrategy getFileSplitStrategy(){
		return new WeeklyFileSplitStrategy();
	}

	@Override
	protected Classifier getClassifier(){
		return Classifier.ssh;
	}
	
	@Override
	protected boolean isOnlyUpdateUser(Record record){
		boolean ret = true;
		String status = RecordExtensions.getStringValue(record, statusFieldName);
		if(status != null && status.equalsIgnoreCase(sshStatusSuccessValue)){
			ret = false;
		}
		
		return ret;
	}
}
