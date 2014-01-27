package fortscale.collection.jobs;

import java.io.IOException;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fortscale.utils.hdfs.HDFSPartitionsWriter;
import fortscale.utils.hdfs.partition.MonthlyPartitionStrategy;
import fortscale.utils.hdfs.split.WeeklyFileSplitStrategy;

@DisallowConcurrentExecution
public class SSHEventProcessJob extends EventProcessJob {
	
	private static final Logger logger = LoggerFactory.getLogger(SSHEventProcessJob.class);
	
	@Override
	protected void createOutputAppender() throws JobExecutionException {
		try {
			logger.debug("initializing hadoop appender in {}", hadoopPath);

			// calculate file directory path according to partition strategy
			appender = new HDFSPartitionsWriter(hadoopPath, new MonthlyPartitionStrategy(), new WeeklyFileSplitStrategy());
			appender.open(hadoopFilename);

		} catch (IOException e) {
			logger.error("error creating hdfs partitions writer at " + hadoopPath, e);
			monitor.error(monitorId, "Process Files", String.format("error creating hdfs partitions writer at %s: \n %s",  hadoopPath, e.toString()));
			throw new JobExecutionException("error creating hdfs partitions writer at " + hadoopPath, e);
		}
	}

}
