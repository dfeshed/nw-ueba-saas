package fortscale.collection.jobs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import fortscale.collection.hadoop.HDFSLineAppender;
import fortscale.collection.morphlines.MorphlinesItemsProcessor;
import fortscale.collection.morphlines.RecordToStringItemsProcessor;
import fortscale.utils.logging.Logger;

/**
 * Scheduled job class for security events log
 */
@DisallowConcurrentExecution
public class SecurityEventsProcessJob extends EventProcessJob {

	private static Logger logger = Logger.getLogger(SecurityEventsProcessJob.class);
	
	private Map<String, EventProcessHandlers> eventsMap;
	
	@Override protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();

		// get parameters values from the job data map
		inputPath = jobDataMapExtension.getJobDataMapStringValue(map, "inputPath");
		errorPath = jobDataMapExtension.getJobDataMapStringValue(map, "errorPath");
		finishPath = jobDataMapExtension.getJobDataMapStringValue(map, "finishPath");
		filesFilter = jobDataMapExtension.getJobDataMapStringValue(map, "filesFilter");
		
		// load main morphline file
		morphline = jobDataMapExtension.getMorphlinesItemsProcessor(map, "morphlineFile");
		
		// get the list of events to continue process config data
		eventsMap = new HashMap<String, EventProcessHandlers>();
		String[] eventsToProcessList = jobDataMapExtension.getJobDataMapStringValue(map, "eventsToProcess").split(",");
		for (String eventToProcess : eventsToProcessList) {
			EventProcessHandlers handler = new EventProcessHandlers();
			handler.morphline = jobDataMapExtension.getMorphlinesItemsProcessor(map, "morphlineFile" + eventToProcess);

			String[] outputFields = jobDataMapExtension.getJobDataMapStringValue(map, "outputFields" + eventToProcess).split(",");
			String outputSeparator = jobDataMapExtension.getJobDataMapStringValue(map, "outputSeparator" + eventToProcess);
			handler.recordToStringProcessor = new RecordToStringItemsProcessor(outputSeparator, outputFields);
			
			handler.hadoopFilePath = jobDataMapExtension.getJobDataMapStringValue(map, "hadoopFilePath" + eventToProcess);
			handler.impalaTableName = jobDataMapExtension.getJobDataMapStringValue(map, "impalaTableName" + eventToProcess);
			
			eventsMap.put(eventToProcess, handler);		
		}
	}

	@Override protected boolean processLine(String line) throws IOException {
		// process each line
		Record record = morphline.process(line);
		if (record==null)
			return false;
		
		// treat the event according to the event type
		Object eventCodeObj = record.getFirstValue("eventCode");
		if (eventCodeObj!=null) {
			String eventCode = eventCodeObj.toString();
			
			// get the event process handlers from the map
			EventProcessHandlers handlers = eventsMap.get(eventCode);
			if (handlers!=null) {
				Record processedRecord = handlers.morphline.process(record);
				String output = handlers.recordToStringProcessor.process(processedRecord);
				
				if (output!=null) {
					handlers.appender.writeLine(output);
					return true;
				}
			}
		}
		return false;
	}

	@Override protected void createOutputAppender() throws JobExecutionException {
		// go over the events map and create an appender for each event
		try {
			for (EventProcessHandlers handlers : eventsMap.values()) {
				try {
					logger.debug("opening hdfs file {} for append", handlers.hadoopFilePath);
					handlers.appender = new HDFSLineAppender();
					handlers.appender.open(handlers.hadoopFilePath);
				} catch (IOException e) {
					logger.error("error opening hdfs file for append at " + handlers.hadoopFilePath, e);
					monitor.error(monitorId, "Process Files", String.format("error appending to hdfs file %s: \n %s",  handlers.hadoopFilePath, e.toString()));
					throw new JobExecutionException("error opening hdfs file for append at " + handlers.hadoopFilePath, e);
				}
			}
		} catch (JobExecutionException e) {
			// close appenders that were opened if we throw exception
			for (EventProcessHandlers handlers : eventsMap.values()) {
				try {
					if (handlers.appender!=null)
						handlers.appender.close();
				} catch (IOException e1) { 
					logger.warn(String.format("error closing handler for $s", handlers.hadoopFilePath));
				}
			}
			throw e;
		}
	}
	
	@Override protected void flushOutputAppender() throws IOException {
		// try and flush all appender that we can, if one of them 
		// throws exception than continue flushing the rest and throw
		// the exception at the end
		IOException exception = null;
		for (EventProcessHandlers handlers : eventsMap.values()) {
			try {
				if (handlers.appender!=null) 
					handlers.appender.flush();
			} catch (IOException e) {
				logger.error("error flushing hdfs file " + handlers.hadoopFilePath, e);
				monitor.error(monitorId, "Process Files", String.format("error flushing hdfs file %s: \n %s",  handlers.hadoopFilePath, e.toString()));
				exception = e;
			}
		}
		
		if (exception!=null)
			throw exception;
	}
	
	@Override protected void closeOutputAppender() throws JobExecutionException {
		// try and close all appender that we can, if one of them 
		// throws exception than continue closing the rest and throw
		// the exception at the end
		IOException exception = null;
		for (EventProcessHandlers handlers : eventsMap.values()) {
			try {
				if (handlers.appender!=null)
					handlers.appender.close();
			} catch (IOException e) {
				logger.error("error closing hdfs file " + handlers.hadoopFilePath, e);
				monitor.error(monitorId, "Process Files", String.format("error closing hdfs file %s: \n %s",  handlers.hadoopFilePath, e.toString()));
				exception = e;
			}
		}
		
		if (exception!=null) {
			throw new JobExecutionException("error closing hdfs file ", exception);
		}
	}
	
	@Override protected void refreshImpala() throws JobExecutionException {
		// refresh all events tables
		for (EventProcessHandlers handlers : eventsMap.values()) {
			impalaClient.refreshTable(handlers.impalaTableName);
		}
	}
	
	/**
	 * Helper class to contain handlers for specific event type in the security event log
	 */
	class EventProcessHandlers {
		public MorphlinesItemsProcessor morphline;
		public String hadoopFilePath;
		public String impalaTableName;
		public HDFSLineAppender appender;
		public RecordToStringItemsProcessor recordToStringProcessor;
	}
	
}
