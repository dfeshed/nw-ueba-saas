package fortscale.collection.jobs;

import static fortscale.collection.JobDataMapExtension.getJobDataMapStringValue;
import static fortscale.collection.JobDataMapExtension.getMorphlinesItemsProcessor;

import java.io.IOException;

import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import fortscale.collection.mongo.DHCPEventsSink;
import fortscale.monitor.JobProgressReporter;

import org.slf4j.*;

/**
 * Scheduled job to process dhcp events into mongodb
 */
@DisallowConcurrentExecution
public class DHCPEventsProcessJob extends EventProcessJob {

	private static Logger logger = LoggerFactory.getLogger(DHCPEventsProcessJob.class);
	
	@Autowired
	private DHCPEventsSink mongo;
	
	@Override
	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();

		// get parameters values from the job data map
		inputPath = getJobDataMapStringValue(map, "inputPath");
		errorPath = getJobDataMapStringValue(map, "errorPath");
		finishPath = getJobDataMapStringValue(map, "finishPath");
		filesFilter = getJobDataMapStringValue(map, "filesFilter");
		
		// build record to items processor
		morphline = getMorphlinesItemsProcessor(resourceLoader, map, "morphlineFile");
	}
	
	@Override
	protected boolean processLine(String line) throws IOException {
		// process each line
		Record record = morphline.process(line);
		
		// write data to mongodb
		if (record==null) 
			return false;
		
		try {
			mongo.writeToMongo(record);
			return true;
		} catch (Exception e) {
			logger.warn(String.format("error writing record %s to mongo", record.toString()));
			return false;
		}			
	}
	
	@Override protected void createHDFSLineAppender() throws JobExecutionException {
		try {
			mongo.connect();
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}
	@Override protected void flushHDFSAppender() throws IOException {}
	@Override protected void closeHDFSAppender() throws JobExecutionException {
		try {
			mongo.postProcessIndexes();
			mongo.close();
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}
	@Override protected void refreshImpala() throws JobExecutionException {}
}
