package fortscale.collection.jobs;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import fortscale.utils.hdfs.HDFSPartitionsWriter;
import fortscale.utils.hdfs.partition.MonthlyPartitionStrategy;
import fortscale.utils.hdfs.split.DailyFileSplitStrategy;
import fortscale.collection.morphlines.MorphlinesItemsProcessor;
import fortscale.collection.morphlines.RecordExtensions;
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
			handler.timestampField = jobDataMapExtension.getJobDataMapStringValue(map, "timestampField" + eventToProcess);

			String[] outputFields = jobDataMapExtension.getJobDataMapStringValue(map, "outputFields" + eventToProcess).split(",");
			String outputSeparator = jobDataMapExtension.getJobDataMapStringValue(map, "outputSeparator" + eventToProcess);
			handler.recordToStringProcessor = new RecordToStringItemsProcessor(outputSeparator, outputFields);
			
			handler.hadoopPath = jobDataMapExtension.getJobDataMapStringValue(map, "hadoopPath" + eventToProcess);
			handler.hadoopFilename = jobDataMapExtension.getJobDataMapStringValue(map, "hadoopFilename" + eventToProcess);
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
			EventProcessHandlers handler = eventsMap.get(eventCode);
			if (handler!=null) {
				Record processedRecord = handler.morphline.process(record);
				String output = handler.recordToStringProcessor.process(processedRecord);
				
				if (output!=null) {
					Long timestamp = RecordExtensions.getLongValue(processedRecord, handler.timestampField);
					handler.appender.writeLine(output, timestamp.longValue());
					return true;
				}
			}
		}
		return false;
	}

	@Override protected void createOutputAppender() throws JobExecutionException {
		// go over the events map and create an appender for each event
		try {
			for (EventProcessHandlers handler : eventsMap.values()) {
				try {
					// create partition strategy
					logger.debug("opening hdfs file {} for append", handler.hadoopPath);
					handler.appender = new HDFSPartitionsWriter(handler.hadoopPath, new MonthlyPartitionStrategy(), new DailyFileSplitStrategy());
					handler.appender.open(handler.hadoopFilename);
				} catch (IOException e) {
					logger.error("error creating hdfs partition writer at " + handler.hadoopPath, e);
					monitor.error(monitorId, "Process Files", String.format("error creating hdfs partition writer at  %s: \n %s",  handler.hadoopPath, e.toString()));
					throw new JobExecutionException("error creating hdfs partition writer at " + handler.hadoopPath, e);
				}
			}
		} catch (JobExecutionException e) {
			// close appenders that were opened if we throw exception
			for (EventProcessHandlers handlers : eventsMap.values()) {
				try {
					if (handlers.appender!=null)
						handlers.appender.close();
				} catch (IOException e1) { 
					logger.warn(String.format("error closing handler for $s", handlers.hadoopPath));
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
				logger.error("error flushing hdfs partitions at " + handlers.hadoopPath, e);
				monitor.error(monitorId, "Process Files", String.format("error flushing hdfs partitions at %s: \n %s",  handlers.hadoopPath, e.toString()));
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
				logger.error("error closing hdfs partitions writer at " + handlers.hadoopPath, e);
				monitor.error(monitorId, "Process Files", String.format("error closing hdfs partitions writer at %s: \n %s",  handlers.hadoopPath, e.toString()));
				exception = e;
			}
		}
		
		if (exception!=null)
			throw new JobExecutionException("error closing hdfs partitions writer", exception);
	}
	
	@Override protected void refreshImpala() throws JobExecutionException {
		
		List<JobExecutionException> exceptions = new LinkedList<JobExecutionException>();
		
		// refresh all events tables
		for (EventProcessHandlers handlers : eventsMap.values()) {
			// declare new partitions in impala
			for (String partition : handlers.appender.getNewPartitions()) {
				try {
					impalaClient.addPartitionToTable(handlers.impalaTableName, partition);
				} catch (JobExecutionException e) {
					exceptions.add(e);
				}
			}
			
			try {
				impalaClient.refreshTable(handlers.impalaTableName);
			} catch (JobExecutionException e) {
				exceptions.add(e);
			}
		}
		
		// log all errors if any
		for (JobExecutionException e : exceptions) {
			logger.error("", e);
			monitor.warn(monitorId, "Process Files", "error refreshing impala - " + e.toString());
		}
	}
	
	/**
	 * Helper class to contain handlers for specific event type in the security event log
	 */
	class EventProcessHandlers {
		public MorphlinesItemsProcessor morphline;
		public String hadoopPath;
		public String hadoopFilename;
		public String impalaTableName;
		public String timestampField;
		public HDFSPartitionsWriter appender;
		public RecordToStringItemsProcessor recordToStringProcessor;
	}
	
}
