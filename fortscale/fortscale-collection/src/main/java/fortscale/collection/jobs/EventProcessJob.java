package fortscale.collection.jobs;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import fortscale.collection.JobDataMapExtension;
import fortscale.utils.hdfs.HDFSPartitionsWriter;
import fortscale.collection.hadoop.ImpalaClient;
import fortscale.utils.hdfs.partition.MonthlyPartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.split.DailyFileSplitStrategy;
import fortscale.utils.hdfs.split.FileSplitStrategy;
import fortscale.utils.impala.ImpalaParser;
import fortscale.collection.io.BufferedLineReader;
import fortscale.collection.morphlines.MorphlinesItemsProcessor;
import fortscale.collection.morphlines.RecordExtensions;
import fortscale.collection.morphlines.RecordToStringItemsProcessor;
import fortscale.monitor.JobProgressReporter;
import fortscale.monitor.domain.JobDataReceived;

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
	
	
	protected String filesFilter;
	protected MorphlinesItemsProcessor morphline;
	protected RecordToStringItemsProcessor recordToString;
	protected String monitorId;
	protected String hadoopPath;
	protected String hadoopFilename;
	protected String impalaTableName;
	protected HDFSPartitionsWriter appender;
	protected String partitionType;
	protected String fileSplitType;
	protected String timestampField;
	
	@Autowired
	protected ImpalaClient impalaClient;
	
	@Autowired
	protected JobProgressReporter monitor;
	
	@Autowired
	protected JobDataMapExtension jobDataMapExtension;
	
	
	
	
	
		
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
		
		// build record to items processor
		String outputFields = jobDataMapExtension.getJobDataMapStringValue(map, "outputFields");
		String outputSeparator = jobDataMapExtension.getJobDataMapStringValue(map, "outputSeparator");
		recordToString = new RecordToStringItemsProcessor(outputSeparator, ImpalaParser.getTableFieldNamesAsArray(outputFields));
		
		morphline = jobDataMapExtension.getMorphlinesItemsProcessor(map, "morphlineFile");
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// get the job group name to be used using monitoring 
		String sourceName = context.getJobDetail().getKey().getGroup();
		String jobName = context.getJobDetail().getKey().getName();
		
		logger.info("{} {} job started", jobName, sourceName);
		monitorId = monitor.startJob(sourceName, jobName, 3);

		
		String currentStep = "Get Job Parameters";
		try {
			// get parameters from job data map
			monitor.startStep(monitorId, currentStep, 1);
			getJobParameters(context);
			monitor.finishStep(monitorId, currentStep);

			// list files in chronological order
			currentStep = "List Files";
			monitor.startStep(monitorId, currentStep, 2);
			File[] files = listFiles(inputPath);
			monitor.finishStep(monitorId, currentStep);

			// start process file stage
			logger.info("processing {} files in {}", files.length, inputPath);
			currentStep = "Process Files";
			monitor.startStep(monitorId, currentStep, 3);

			// get hadoop file writer
			createOutputAppender();
			
			// read each file and process lines
			try {
				for (File file : files) {
					logger.info("starting to process {}", file.getName()); 
					
					// transform events in file
					boolean success = processFile(file);
					
					if (success) {
						moveFileToFolder(file, finishPath);
					} else {
						moveFileToFolder(file, errorPath);
					}
		
					logger.info("finished processing {}", file.getName());
				}
			} catch (IOException e) {
				logger.error("error processing files", e);
				monitor.error(monitorId, currentStep, e.toString());
				throw new JobExecutionException("error processing files", e);
			} finally {
				closeOutputAppender();
			}
			
			refreshImpala();
			
			monitor.finishStep(monitorId, currentStep);
		} catch (JobExecutionException e) {
			monitor.error(monitorId, currentStep, e.toString());
			throw e;
		} catch (Exception exp) {
			logger.error("unexpected error during event process job: " + exp.toString());
			monitor.error(monitorId, currentStep, exp.toString());
			throw new JobExecutionException(exp);
		} finally {
			monitor.finishJob(monitorId);
			logger.info("{} {} job finished", jobName, sourceName);
		}
	}
	
	/**
	 * Gets list of files in the input folder sorted according to the time stamp
	 */
	private File[] listFiles(String inputPath) throws JobExecutionException {
		File inputDir = new File(inputPath);
		if (!inputDir.exists() || !inputDir.isDirectory()) {
			logger.error("input path {} does not exists", inputPath);
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
	
	
	
	protected boolean processFile(File file) throws IOException {
		
		BufferedLineReader reader = new BufferedLineReader();
		reader.open(file);
			
		try {
			int lineCounter = 0;
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (processLine(line))
					++lineCounter;
			}
			
			// flush hadoop
			flushOutputAppender();
			
			monitor.addDataReceived(monitorId, new JobDataReceived(file.getName(), lineCounter, "Events"));
		} catch (IOException e) {
			logger.error("error processing file " + file.getName(), e);
			monitor.error(monitorId, "Process Files", e.toString());
			return false;
		} finally {
			reader.close();
		}

		
		if (reader.HasErrors()) {
			logger.error("error processing file " + file.getName(), reader.getException());
			monitor.error(monitorId, "Process Files", reader.getException().toString());
			return false;
		} else {
			if (reader.hasWarnings()) {
				logger.warn("error processing file " + file.getName(), reader.getException());
				monitor.warn(monitorId, "Process Files", reader.getException().toString());
			}
			return true;
		}
	}

	
	protected boolean processLine(String line) throws IOException {
		// process each line
		Record record = morphline.process(line);
		if(record == null){
			return false;
		}
		addNormalizedUsernameField(record);
		String output = recordToString.process(record);
		
		// append to hadoop, if there is data to be written
		if (output!=null) {
			Long timestamp = RecordExtensions.getLongValue(record, timestampField);
			appender.writeLine(output, timestamp.longValue());
			return true;
		} else {
			return false;
		}
	}
	
	protected void addNormalizedUsernameField(Record record){
		record.put(normalizedUsernameField, normalizeUsername(record));
	}
	
	protected String normalizeUsername(Record record){
		return extractUsernameFromRecord(record);
	}
	
	protected String extractUsernameFromRecord(Record record){
		return RecordExtensions.getStringValue(record, getUsernameField());
	}
	
	protected void refreshImpala() throws JobExecutionException {

		List<JobExecutionException> exceptions = new LinkedList<JobExecutionException>();
		
		// declare new partitions for impala
		for (String partition : appender.getNewPartitions()) {
			try {
				impalaClient.addPartitionToTable(impalaTableName, partition); 
			} catch (JobExecutionException e) {
				exceptions.add(e);
			}
		}
		
		try {
			impalaClient.refreshTable(impalaTableName);
		} catch (JobExecutionException e) {
			exceptions.add(e);
		}
		
		// log all errors if any
		for (JobExecutionException e : exceptions) {
			logger.error("error refreshing impala", e);
			monitor.warn(monitorId, "Process Files", "error refreshing impala - " + e.toString());
		}
	}
	
	protected void createOutputAppender() throws JobExecutionException {
		try {
			logger.debug("initializing hadoop appender in {}", hadoopPath);

			// calculate file directory path according to partition strategy
			appender = new HDFSPartitionsWriter(hadoopPath, getPartitionStrategy(), getFileSplitStrategy());
			appender.open(hadoopFilename);

		} catch (IOException e) {
			logger.error("error creating hdfs partitions writer at " + hadoopPath, e);
			monitor.error(monitorId, "Process Files", String.format("error creating hdfs partitions writer at %s: \n %s",  hadoopPath, e.toString()));
			throw new JobExecutionException("error creating hdfs partitions writer at " + hadoopPath, e);
		}
	}
	
	protected PartitionStrategy getPartitionStrategy(){
		return new MonthlyPartitionStrategy();
	}
	
	protected FileSplitStrategy getFileSplitStrategy(){
		return new DailyFileSplitStrategy();
	}
	
	protected void flushOutputAppender() throws IOException {
		try {
			appender.flush();
		} catch (IOException e) {
			logger.error("error flushing hdfs partitions writer at " + hadoopPath, e);
			monitor.error(monitorId, "Process Files", String.format("error flushing partitions at %s: \n %s",  hadoopPath, e.toString()));
			throw e;
		}
	}
	
	protected void closeOutputAppender() throws JobExecutionException {
		try {
			logger.debug("flushing hdfs paritions at {}", hadoopPath);
			appender.close(); 
		} catch (IOException e) {
			logger.error("error closing hdfs partitions writer at " + hadoopPath, e);
			monitor.error(monitorId, "Process Files", String.format("error closing partitions at %s: \n %s",  hadoopPath, e.toString()));
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

