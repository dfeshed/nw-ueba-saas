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
	
	@Value("${impala.ssh.table.fields.status}")
	private String statusFieldName;
	
	@Value("${ssh.status.success.value:accepted}")
	private String sshStatusSuccessValue;
	
	@Override
	protected String normalizeUsername(Record record){
		String username = extractUsernameFromRecord(record);
		return sshUsernameNormalizer.normalize(username);
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
