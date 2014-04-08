package fortscale.collection.jobs;

import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Value;

import fortscale.collection.morphlines.RecordExtensions;
import fortscale.services.fe.Classifier;
import fortscale.utils.hdfs.split.FileSplitStrategy;
import fortscale.utils.hdfs.split.WeeklyFileSplitStrategy;

@DisallowConcurrentExecution
public class SSHEventProcessJob extends EventProcessJob {
		
	@Value("${impala.data.ssh.table.field.status}")
	private String statusFieldName;
	
	@Value("${ssh.status.success.value:accepted}")
	private String sshStatusSuccessValue;
	
	
	
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
