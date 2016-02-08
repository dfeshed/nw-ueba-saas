package fortscale.collection.jobs.event.process;

import fortscale.collection.JobDataMapExtension;
import fortscale.collection.io.BufferedLineReader;
import fortscale.collection.monitoring.ItemContext;
import fortscale.collection.morphlines.MorphlinesItemsProcessor;
import fortscale.collection.morphlines.RecordExtensions;
import fortscale.collection.morphlines.RecordToStringItemsProcessor;
import fortscale.services.UserService;
import fortscale.services.classifier.Classifier;
import fortscale.streaming.task.monitor.TaskMonitoringHelper;
import fortscale.utils.hdfs.BufferedHDFSWriter;
import fortscale.utils.hdfs.HDFSPartitionsWriter;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.hdfs.split.DailyFileSplitStrategy;
import fortscale.utils.hdfs.split.FileSplitStrategy;
import fortscale.utils.impala.ImpalaClient;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.kafka.KafkaEventsWriter;
import org.apache.commons.lang3.StringUtils;
import org.kitesdk.morphline.api.Record;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Job class to help build event process jobs from saved files into hadoop
 */
@DisallowConcurrentExecution
public class EventProcessJob implements Job {

	private static Logger logger = LoggerFactory.getLogger(EventProcessJob.class);
	
	@Value("${collection.fetch.data.path}")
	protected String inputPath;
	@Value("${collection.fetch.error.data.path}")
	protected String errorPath;
	@Value("${collection.fetch.finish.data.path}")
	protected String finishPath;
	
	@Value("${impala.data.table.fields.normalized_username}")
	private String normalizedUsernameField;
	@Value("${impala.table.fields.username}")
	private String usernameField;
	@Value("${hadoop.writer.buffer.size:10000}")
	protected int maxBufferSize;
	@Value("${etl.sendTo.kafka:true}")
	protected boolean defaultSendToKafka;
	protected boolean sendToKafka;

	protected String filesFilter;
	protected MorphlinesItemsProcessor morphline;
	protected MorphlinesItemsProcessor morphlineEnrichment;
	protected RecordToStringItemsProcessor recordToHadoopString;
	protected RecordToStringItemsProcessor recordKeyExtractor;
	protected RecordToStringItemsProcessor recordToMessageString;
	protected String hadoopPath;
	protected String hadoopFilename;
	protected String impalaTableName;
	protected BufferedHDFSWriter appender;
	protected String partitionType;
	protected String fileSplitType;
	protected String timestampField;
	protected String streamingTopic;
	protected KafkaEventsWriter streamWriter;
    protected PartitionStrategy partitionStrategy;

	String outputSeparator;

	@Autowired
	protected ImpalaClient impalaClient;
	
	@Autowired
	protected JobDataMapExtension jobDataMapExtension;
	
	@Autowired
	protected UserService userService;

	/**
	 * taskMonitoringHelper is holding all the steps, errors, arrived events, successfully processed events,
	 * and drop events and save all those details to mongo
	 */
	@Autowired
	protected TaskMonitoringHelper<String> taskMonitoringHelper;
	
	
		
	public String getUsernameField() {
		return usernameField;
	}

	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();

		// get parameters values from the job data map
		filesFilter = jobDataMapExtension.getJobDataMapStringValue(map, "filesFilter");
		hadoopPath = jobDataMapExtension.getJobDataMapStringValue(map, "hadoopPath");
		hadoopFilename = jobDataMapExtension.getJobDataMapStringValue(map, "hadoopFilename");
		impalaTableName = jobDataMapExtension.getJobDataMapStringValue(map, "impalaTableName");
		timestampField = jobDataMapExtension.getJobDataMapStringValue(map, "timestampField");
		streamingTopic = jobDataMapExtension.getJobDataMapStringValue(map, "streamingTopic", "");
		sendToKafka = jobDataMapExtension.getJobDataMapBooleanValue(map, "sendToKafka", defaultSendToKafka);
		
		// build record to items processor
		String outputFields = jobDataMapExtension.getJobDataMapStringValue(map, "outputFields");
		String messageOutputFields = jobDataMapExtension.getJobDataMapStringValue(map,"messageOutputFields");
		outputSeparator = jobDataMapExtension.getJobDataMapStringValue(map, "outputSeparator");
		recordToHadoopString = new RecordToStringItemsProcessor(outputSeparator, ImpalaParser.getTableFieldNamesAsArray(outputFields));
		recordToMessageString = new RecordToStringItemsProcessor(outputSeparator,ImpalaParser.getTableFieldNamesAsArray(messageOutputFields));
		recordKeyExtractor = new RecordToStringItemsProcessor(outputSeparator, jobDataMapExtension.getJobDataMapStringValue(map, "partitionKeyFields"));

		morphline = jobDataMapExtension.getMorphlinesItemsProcessor(map, "morphlineFile");
		morphlineEnrichment = jobDataMapExtension.getMorphlinesItemsProcessor(map, "morphlineEnrichment");

        String strategy = jobDataMapExtension.getJobDataMapStringValue(map, "partitionStrategy");
        partitionStrategy = PartitionsUtils.getPartitionStrategy(strategy);
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// get the job group name to be used using monitoring 
		String sourceName = context.getJobDetail().getKey().getGroup();
		String jobName = context.getJobDetail().getKey().getName();
		
		logger.info("{} {} job started", jobName, sourceName);


		
		String currentStep = "Get Job Parameters";
		try {
			// get parameters from job data map
			taskMonitoringHelper.startStep(currentStep);
			getJobParameters(context);
			taskMonitoringHelper.finishStep(currentStep);

			// list files in chronological order
			currentStep = "List Files";
			taskMonitoringHelper.startStep(currentStep);
			File[] files = listFiles(inputPath);
			taskMonitoringHelper.finishStep(currentStep);

			// start process file stage
			logger.info("processing {} files in {}", files.length, inputPath);
			currentStep = "Process Files";

			taskMonitoringHelper.startStep(currentStep);
			// get hadoop file writer and streaming sink
			createOutputAppender();
			initializeStreamingAppender();
			
			// read each file and process lines
			try {
				for (File file : files) {
					try {
						logger.info("starting to process {}", file.getName()); 
						
						// transform events in file
						boolean success = processFile(file);
						
						if (success) {
							moveFileToFolder(file, finishPath);
						} else {
							moveFileToFolder(file, errorPath);	
						}
			
						logger.info("finished processing {}", file.getName());
					} catch (Exception e) {
						moveFileToFolder(file, errorPath);

						logger.error("error processing file " + file.getName(), e);
						taskMonitoringHelper.error(currentStep, e.toString());
					}
				}
			} finally {
				// make sure all close are called, hence the horror below of nested finally blocks
				try {
					morphline.close();
				} finally {
					try {
						if (morphlineEnrichment != null) {
							morphlineEnrichment.close();
						}
					} finally {
						try {
							closeOutputAppender();
						} finally {
							closeStreamingAppender();
						}
					}
				}
			}

			refreshImpala();

			taskMonitoringHelper.finishStep(currentStep);
		} catch (JobExecutionException e) {
			taskMonitoringHelper.error(currentStep, e.toString());
			throw e;
		} catch (Exception exp) {
			logger.error("unexpected error during event process job: " + exp.toString());
			taskMonitoringHelper.error(currentStep, exp.toString());
			throw new JobExecutionException(exp);
		} finally {
			//Before job goes down - all monitoring details will be saved to mongo
			taskMonitoringHelper.saveJobStatusReport(jobName,false,sourceName);
			logger.info("{} {} job finished", jobName, sourceName);
		}
	}
	
	/**
	 * Gets list of files in the input folder sorted according to the time stamp
	 */
	private File[] listFiles(String inputPath) throws JobExecutionException {
		File inputDir = new File(inputPath);
		if (!inputDir.exists() || !inputDir.isDirectory()) {
			logger.error("input path {} does not exists", inputDir.getAbsolutePath());
			throw new JobExecutionException(String.format("input path %s does not exists", inputPath));
		}

		FileFilter filter = new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().matches(filesFilter);
			}
		};

		File[] files = inputDir.listFiles(filter);
		Arrays.sort(files);
		return files;
	}


	/**
	 * Iterate each line of the file and process each line.
	 *
	 * Pay attention - if override the method, make sure to set updateItemContext
	 * @param file
	 * @return
	 * @throws IOException
	 */
	protected boolean processFile(File file) throws IOException {

		BufferedLineReader reader = new BufferedLineReader();
		reader.open(file);
		ItemContext itemContext = new ItemContext(file.getName(),taskMonitoringHelper);

			
		try {
			int numOfLines = 0;
			int numOfSuccessfullyProcessedLines = 0;
			String line;
			while ((line = reader.readLine()) != null) {
				if (StringUtils.isNotBlank(line)) {
					numOfLines++;
					//count that new event trying to processed from specific file
					taskMonitoringHelper.handleNewEvent(file.getName());
					Record record = processLine(line, itemContext);
					if (record != null){
						numOfSuccessfullyProcessedLines++;
						//If success - write the event to monitoring. filed event monitoing handled by monitoring
						Long timestamp = RecordExtensions.getLongValue(record, timestampField);
						taskMonitoringHelper.handleUnFilteredEvents(itemContext.getSourceName(),timestamp);
					}
				}
			}

			logger.info("Successfully processed {} out of {} lines in file {}", numOfSuccessfullyProcessedLines, numOfLines, file.getName());
			
			// flush hadoop
			flushOutputAppender();
		} catch (IOException e) {
			logger.error("error processing file " + file.getName(), e);
			taskMonitoringHelper.error("Process Files", e.toString());
			return false;
		} finally {
			reader.close();
		}

		
		if (reader.HasErrors()) {
			logger.error("error processing file " + file.getName(), reader.getException());
			taskMonitoringHelper.error("Process Files", reader.getException().toString());
			return false;
		} else {
			if (reader.hasWarnings()) {
				logger.warn("error processing file " + file.getName(), reader.getException());
				taskMonitoringHelper.error("Process Files warning", reader.getException().toString());
			}
			return true;
		}
	}



	protected Record processLine(String line, ItemContext itemContext) throws IOException {
		// process each line

		//I assume that this.itemContext updated once for each file.
		Record rec = morphline.process(line, itemContext);
		Record record = null;
		if(rec == null){
			return null;
		}
		if (morphlineEnrichment != null) {
			record = morphlineEnrichment.process(rec, itemContext);
			if (record == null) {
				return null;
			}
		} else {
			record = rec;
		}

		//divide to two outputs:
		//1. longer one - including data_source and last_state
		//2. shorter one - without them - to hadoop

		String outputToHadoop = recordToHadoopString.process(record);

		
		// append to hadoop, if there is data to be written
		if (outputToHadoop!=null) {
			// append to hadoop
			Long timestamp = RecordExtensions.getLongValue(record, timestampField);
			appender.writeLine(outputToHadoop, timestamp.longValue());

			// output event to streaming platform
			streamMessage(recordKeyExtractor.process(record),recordToMessageString.toJSON(record));


			return record;
		} else {
			return null;
		}
	}
	
	protected Classifier getClassifier(){
		return null;
	}
	
	protected boolean isOnlyUpdateUser(Record record){
		return true;
	}
	
	protected boolean isUpdateAppUsername(){
		return true;
	}
		
	protected String extractUsernameFromRecord(Record record){
		return RecordExtensions.getStringValue(record, getUsernameField());
	}
	
	protected String extractNormalizedUsernameFromRecord(Record record){
		return RecordExtensions.getStringValue(record, normalizedUsernameField);
	}
	
	protected void refreshImpala() throws JobExecutionException {

		List<Exception> exceptions = new LinkedList<Exception>();
		
		// declare new partitions for impala
		HDFSPartitionsWriter partitionsWriter = (HDFSPartitionsWriter)appender.getWriter();
		for (String partition : partitionsWriter.getNewPartitions()) {
			try {
				impalaClient.addPartitionToTable(impalaTableName, partition); 
			} catch (Exception e) {
				exceptions.add(e);
			}
		}
		partitionsWriter.clearNewPartitions();
		try {
			partitionsWriter.close();
		} catch (Exception e) {
			exceptions.add(e);
		}


		try {
			impalaClient.refreshTable(impalaTableName);
		} catch (Exception e) {
			exceptions.add(e);
		}
		
		// log all errors if any
		for (Exception e : exceptions) {
			logger.error("error refreshing impala", e);
			taskMonitoringHelper.error("Process Files warning", "error refreshing impala - " + e.toString());
		}
		if (!exceptions.isEmpty())
			throw new JobExecutionException("got exception while refreshing impala", exceptions.get(0));
	}
	
	protected void createOutputAppender() throws JobExecutionException {
		logger.debug("initializing hadoop appender in {}", hadoopPath);

		// calculate file directory path according to partition strategy
		HDFSPartitionsWriter writer = new HDFSPartitionsWriter(hadoopPath, getPartitionStrategy(), getFileSplitStrategy(), outputSeparator);
		appender = new BufferedHDFSWriter(writer, hadoopFilename, maxBufferSize);
	}
	
	/*** Initialize the streaming appender upon job start to be able to produce messages to */ 
	protected void initializeStreamingAppender() throws JobExecutionException {
		if (StringUtils.isNotEmpty(streamingTopic))
			streamWriter = new KafkaEventsWriter(streamingTopic);
	}
	
	/*** Send the message produced by the morphline ETL to the streaming platform */
	protected void streamMessage(String key, String message) throws IOException {
		if (streamWriter!=null && sendToKafka == true)
			streamWriter.send(key, message);
	}
	
	/*** Close the streaming appender upon job finish to free resources */
	protected void closeStreamingAppender() throws JobExecutionException {
		if (streamWriter!=null)
			streamWriter.close();
	}
	
	protected PartitionStrategy getPartitionStrategy(){
		return this.partitionStrategy;
	}
	
	protected FileSplitStrategy getFileSplitStrategy(){
		return new DailyFileSplitStrategy();
	}
	
	protected void flushOutputAppender() throws IOException {
		try {
			logger.info("Flushing output to HDFS partition (" + hadoopPath + ")..");
			appender.flush();
			logger.info("Finished flushing output to HDFS partition (" + hadoopPath + ")");
		} catch (IOException e) {
			logger.error("error flushing hdfs partitions writer at " + hadoopPath, e);
			taskMonitoringHelper.error("Process Files", String.format("error flushing partitions at %s: \n %s",  hadoopPath, e.toString()));
			throw e;
		}
	}
	
	protected void closeOutputAppender() throws JobExecutionException {
		try {
			logger.debug("flushing hdfs paritions at {}", hadoopPath);
			appender.close(); 
		} catch (IOException e) {
			logger.error("error closing hdfs partitions writer at " + hadoopPath, e);
			taskMonitoringHelper.error("Process Files", String.format("error closing partitions at %s: \n %s",  hadoopPath, e.toString()));
			throw new JobExecutionException("error closing partitions at " + hadoopPath, e);
		}
	}
	
	protected void moveFileToFolder(File file, String path) {
		File renamed = null;
		if (path.endsWith(File.separator))
			renamed = new File(path + file.getName());
		else
			renamed = new File(path + File.separator + file.getName());

		// create parent file if not exists
		if (!renamed.getParentFile().exists()) {
			if (!renamed.getParentFile().mkdirs()) {
				logger.error("cannot create path {}", path);
				return;
			}
		}
		
		if (!file.renameTo(renamed))
			logger.error("failed moving file {} to path {}", file.getName(), path);
	}
}

