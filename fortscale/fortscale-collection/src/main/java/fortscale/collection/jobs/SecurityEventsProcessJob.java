package fortscale.collection.jobs;

import fortscale.collection.io.KafkaEventsWriter;
import fortscale.collection.morphlines.MorphlinesItemsProcessor;
import fortscale.collection.morphlines.RecordExtensions;
import fortscale.collection.morphlines.RecordToStringItemsProcessor;
import fortscale.services.fe.Classifier;
import fortscale.utils.hdfs.BufferedHDFSWriter;
import fortscale.utils.hdfs.HDFSPartitionsWriter;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.hdfs.split.DailyFileSplitStrategy;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Scheduled job class for security events log
 */
@DisallowConcurrentExecution
public class SecurityEventsProcessJob extends EventProcessJob {

	private static Logger logger = Logger.getLogger(SecurityEventsProcessJob.class);
	
	private Map<String, EventTableHandlers> eventToTableHandlerMap;
	private Map<String, MorphlinesItemsProcessor> eventToMorphlineMap;
	
	@Value("${impala.data.security.events.4769.table.morphline.fields.username}")
	private String usernameField;
	
	
	@Override
	public String getUsernameField(){
		return usernameField;
	}
	
	protected Classifier getClassifier(){
		return Classifier.auth;
	}
	
	@Override protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();

		// get parameters values from the job data map
		filesFilter = jobDataMapExtension.getJobDataMapStringValue(map, "filesFilter");
		
		// load main morphline file
		morphline = jobDataMapExtension.getMorphlinesItemsProcessor(map, "morphlineFile");
		
		// get the list of events to continue process config data
		eventToMorphlineMap = new HashMap<String, MorphlinesItemsProcessor>();
		
		
		// get the list of events to continue process config data
		eventToTableHandlerMap = new HashMap<String, EventTableHandlers>();
		String[] impalaTablesList = jobDataMapExtension.getJobDataMapStringValue(map, "impalaTables").split(",");
		for (String impalaTable : impalaTablesList) {
			EventTableHandlers handler = new EventTableHandlers();
			
			handler.timestampField = jobDataMapExtension.getJobDataMapStringValue(map, "timestampField" + impalaTable);

			String outputFields = jobDataMapExtension.getJobDataMapStringValue(map, "outputFields" + impalaTable);
			String outputSeparator = jobDataMapExtension.getJobDataMapStringValue(map, "outputSeparator" + impalaTable);
			handler.recordToStringProcessor = new RecordToStringItemsProcessor(outputSeparator, ImpalaParser.getTableFieldNamesAsArray(outputFields));
			
			handler.hadoopPath = jobDataMapExtension.getJobDataMapStringValue(map, "hadoopPath" + impalaTable);
			handler.hadoopFilename = jobDataMapExtension.getJobDataMapStringValue(map, "hadoopFilename" + impalaTable);
			handler.impalaTableName = jobDataMapExtension.getJobDataMapStringValue(map, "impalaTableName" + impalaTable);

            String strategy = jobDataMapExtension.getJobDataMapStringValue(map, "partitionStrategy"+ impalaTable);
            handler.partitionStrategy = PartitionsUtils.getPartitionStrategy(strategy);
			
			String streamingTopic = jobDataMapExtension.getJobDataMapStringValue(map, "streamingTopic" + impalaTable, "");
			if (StringUtils.isNotEmpty(streamingTopic))
				handler.streamWriter = new KafkaEventsWriter(streamingTopic);
			
			String[] eventsToProcessList = jobDataMapExtension.getJobDataMapStringValue(map, "eventsToProcess" + impalaTable).split(",");
			for (String eventToProcess : eventsToProcessList) {
				if(!eventToMorphlineMap.containsKey(eventToProcess)){
					eventToMorphlineMap.put(eventToProcess, jobDataMapExtension.getMorphlinesItemsProcessor(map, "morphlineFile" + eventToProcess));
				}
				eventToTableHandlerMap.put(eventToProcess, handler);
			}
			
				
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
			EventTableHandlers handler = eventToTableHandlerMap.get(eventCode);
			MorphlinesItemsProcessor eventMorphlinesItemsProcessor = eventToMorphlineMap.get(eventCode);
			if (handler!=null) {
				Record processedRecord = eventMorphlinesItemsProcessor.process(record);
				if (processedRecord!=null) {
					Boolean isComputer = (Boolean) record.getFirstValue("isComputer");
					if(isComputer == null || !isComputer){
					
						String output = handler.recordToStringProcessor.process(processedRecord);
					
						if (output!=null) {
							// append to hadoop
							Long timestamp = RecordExtensions.getLongValue(processedRecord, handler.timestampField);
							handler.appender.writeLine(output, timestamp.longValue());
							
							// ensure user exists in mongodb
							updateOrCreateUserWithClassifierUsername(processedRecord);
							
							// output event to streaming platform
							if (handler.streamWriter!=null)
								handler.streamWriter.send(handler.recordToStringProcessor.toJSON(processedRecord));
							
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	@Override
	protected boolean isUpdateAppUsername(){
		return false;
	}

	@Override protected void createOutputAppender() throws JobExecutionException {
		// go over the events map and create an appender for each event
		for (EventTableHandlers handler : eventToTableHandlerMap.values()) {
			// create partition strategy
			logger.debug("opening hdfs file {} for append", handler.hadoopPath);
			HDFSPartitionsWriter writer = new HDFSPartitionsWriter(handler.hadoopPath,handler.partitionStrategy, new DailyFileSplitStrategy());
			handler.appender = new BufferedHDFSWriter(writer, handler.hadoopFilename, maxBufferSize);
		}
	}
	
	@Override protected void flushOutputAppender() throws IOException {
		// try and flush all appender that we can, if one of them 
		// throws exception than continue flushing the rest and throw
		// the exception at the end
		IOException exception = null;
		for (EventTableHandlers handlers : eventToTableHandlerMap.values()) {
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
		Exception exception = null;
		for (EventTableHandlers handlers : eventToTableHandlerMap.values()) {
			try {
				if (handlers.appender!=null)
					handlers.appender.close();
			} catch (Exception e) {
				logger.error("error closing hdfs partitions writer at " + handlers.hadoopPath, e);
				monitor.error(monitorId, "Process Files", String.format("error closing hdfs partitions writer at %s: \n %s",  handlers.hadoopPath, e.toString()));
				exception = e;
			}
		}
		
		Iterator<Entry<String, MorphlinesItemsProcessor>> iters = eventToMorphlineMap.entrySet().iterator();
		while (iters.hasNext()) {
			Entry<String, MorphlinesItemsProcessor> iter = iters.next();
			MorphlinesItemsProcessor processor = iter.getValue();
			try {
				processor.close();					
			} catch (Exception e) {
				logger.error(String.format("error closing morphline processor for event %s", iter.getKey()), e);
				monitor.error(monitorId, "Process Files", String.format("error closing morphline processor for event %s. exception: %s", iter.getKey(), e.toString()));
				exception = e;
			}
		}
		
		if (exception!=null)
			throw new JobExecutionException("error closing hdfs partitions writer", exception);
	}
	
	@Override protected void refreshImpala() throws JobExecutionException {
		
		List<Exception> exceptions = new LinkedList<Exception>();
		
		// refresh all events tables
		for (EventTableHandlers handlers : eventToTableHandlerMap.values()) {
			// declare new partitions in impala
			HDFSPartitionsWriter partitionsWriter = (HDFSPartitionsWriter)handlers.appender.getWriter();
			for (String partition : partitionsWriter.getNewPartitions()) {
				try {
					impalaClient.addPartitionToTable(handlers.impalaTableName, partition);
				} catch (Exception e) {
					exceptions.add(e);
				}
			}
			partitionsWriter.clearNewPartitions();
			
			try {
				impalaClient.refreshTable(handlers.impalaTableName);
			} catch (Exception e) {
				exceptions.add(e);
			}
		}
		
		// log all errors if any
		if (!exceptions.isEmpty()) {
			for (Exception e : exceptions) {
				logger.error("", e);
				monitor.warn(monitorId, "Process Files", "error refreshing impala - " + e.toString());
			}
			throw new JobExecutionException("got error while refreshing impala", exceptions.get(0));
		}
	}
	
	/*** Initialize the streaming appender upon job start to be able to produce messages to */ 
	@Override protected void initializeStreamingAppender() throws JobExecutionException {}
	
	/*** Send the message produced by the morphline ETL to the streaming platform */
	@Override protected void streamMessage(String message) throws IOException {}
	
	/*** Close the streaming appender upon job finish to free resources */
	@Override protected void closeStreamingAppender() throws JobExecutionException {
		for (EventTableHandlers handlers : eventToTableHandlerMap.values()) {
			if (handlers.streamWriter!=null)
				handlers.streamWriter.close();
		}
	}
	
	/**
	 * Helper class to contain handlers for specific event type in the security event log
	 */
	class EventTableHandlers {
		public String hadoopPath;
		public String hadoopFilename;
		public String impalaTableName;
		public String timestampField;
		public BufferedHDFSWriter appender;
		public RecordToStringItemsProcessor recordToStringProcessor;
		public KafkaEventsWriter streamWriter;
        public PartitionStrategy partitionStrategy;
	}
	
}
