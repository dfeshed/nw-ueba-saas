package fortscale.collection.jobs;

import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.services.impl.UsernameNormalizer;
import fortscale.utils.hdfs.split.FileSplitStrategy;
import fortscale.utils.hdfs.split.WeeklyFileSplitStrategy;

@DisallowConcurrentExecution
public class SSHEventProcessJob extends EventProcessJob {
	
	@Autowired
	UsernameNormalizer sshUsernameNormalizer;
	
	@Override
	protected String normalizeUsername(Record record){
		String username = extractUsernameFromRecord(record);
		return sshUsernameNormalizer.normalize(username);
	}
	
	@Override
	protected FileSplitStrategy getFileSplitStrategy(){
		return new WeeklyFileSplitStrategy();
	}

}
