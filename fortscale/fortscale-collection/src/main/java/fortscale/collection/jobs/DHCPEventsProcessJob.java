package fortscale.collection.jobs;


import java.io.IOException;

import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.JobDataMapExtension;
import fortscale.collection.mongo.DHCPEventsSink;

/**
 * Scheduled job to process dhcp events into mongodb
 */
@DisallowConcurrentExecution
public class DHCPEventsProcessJob extends EventProcessJob {

	private static Logger logger = LoggerFactory.getLogger(DHCPEventsProcessJob.class);
	
	@Autowired
	private DHCPEventsSink mongo;
	
	@Autowired
	private JobDataMapExtension jobDataMapExtension;
	
	@Override
	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();

		// get parameters values from the job data map
		inputPath = jobDataMapExtension.getJobDataMapStringValue(map, "inputPath");
		errorPath = jobDataMapExtension.getJobDataMapStringValue(map, "errorPath");
		finishPath = jobDataMapExtension.getJobDataMapStringValue(map, "finishPath");
		filesFilter = jobDataMapExtension.getJobDataMapStringValue(map, "filesFilter");
		
		// build record to items processor
		morphline = jobDataMapExtension.getMorphlinesItemsProcessor(map, "morphlineFile");
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
