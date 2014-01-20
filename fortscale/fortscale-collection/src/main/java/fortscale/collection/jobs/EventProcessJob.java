package fortscale.collection.jobs;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;

import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.JobDataMapExtension;
import fortscale.collection.hadoop.HDFSLineAppender;
import fortscale.collection.hadoop.ImpalaClient;
import fortscale.collection.io.BufferedLineReader;
import fortscale.collection.morphlines.MorphlinesItemsProcessor;
import fortscale.collection.morphlines.RecordToStringItemsProcessor;
import fortscale.monitor.JobProgressReporter;
import fortscale.monitor.domain.JobDataReceived;

/**
 * Job class to help build event process jobs from saved files into hadoop
 */
@DisallowConcurrentExecution
public class EventProcessJob implements Job {

	private static Logger logger = LoggerFactory.getLogger(EventProcessJob.class);
	
	protected String inputPath;
	protected String errorPath;
	protected String finishPath;
	protected String filesFilter;
	protected MorphlinesItemsProcessor morphline;
	protected RecordToStringItemsProcessor recordToString;
	protected String monitorId;
	protected String hadoopFilePath;
	protected String impalaTableName;
	protected HDFSLineAppender appender;
	
	
	
	@Autowired
	protected ImpalaClient impalaClient;
	
	@Autowired
	protected JobProgressReporter monitor;
	
	@Autowired
	protected JobDataMapExtension jobDataMapExtension;
		
	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();

		// get parameters values from the job data map
		inputPath = jobDataMapExtension.getJobDataMapStringValue(map, "inputPath");
		errorPath = jobDataMapExtension.getJobDataMapStringValue(map, "errorPath");
		finishPath = jobDataMapExtension.getJobDataMapStringValue(map, "finishPath");
		filesFilter = jobDataMapExtension.getJobDataMapStringValue(map, "filesFilter");
		hadoopFilePath = jobDataMapExtension.getJobDataMapStringValue(map, "hadoopFilePath");
		impalaTableName = jobDataMapExtension.getJobDataMapStringValue(map, "impalaTableName");
		
		// build record to items processor
		String[] outputFields = jobDataMapExtension.getJobDataMapStringValue(map, "outputFields").split(",");
		String outputSeparator = jobDataMapExtension.getJobDataMapStringValue(map, "outputSeparator");
		recordToString = new RecordToStringItemsProcessor(outputSeparator, outputFields);
		
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
		}

		monitor.finishJob(monitorId);
		logger.info("{} {} job finished", jobName, sourceName);
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
		} finally {
			reader.close();
		}

		
		if (reader.HasErrors()) {
			monitor.error(monitorId, "Process Files", reader.getException().toString());
			return false;
		} else {
			if (reader.hasWarnings()) {
				monitor.warn(monitorId, "Process Files", reader.getException().toString());
			}
			return true;
		}
	}

	
	protected boolean processLine(String line) throws IOException {
		// process each line
		Record record = morphline.process(line);
		String output = recordToString.process(record);
		
		// append to hadoop, if there is data to be written
		
		if (output!=null) {
			appender.writeLine(output);
			return true;
		} else {
			return false;
		}
	}
	
	protected void refreshImpala() throws JobExecutionException {
		impalaClient.refreshTable(impalaTableName);
	}
	
	
	protected void createOutputAppender() throws JobExecutionException {
		try {
			logger.debug("opening hdfs file {} for append", hadoopFilePath);

			appender = new HDFSLineAppender();
			appender.open(hadoopFilePath);

		} catch (IOException e) {
			logger.error("error opening hdfs file for append at " + hadoopFilePath, e);
			monitor.error(monitorId, "Process Files", String.format("error appending to hdfs file %s: \n %s",  hadoopFilePath, e.toString()));
			throw new JobExecutionException("error opening hdfs file for append at " + hadoopFilePath, e);
		}
	}
	
	protected void flushOutputAppender() throws IOException {
		try {
			appender.flush();
		} catch (IOException e) {
			logger.error("error flushing hdfs file " + hadoopFilePath, e);
			monitor.error(monitorId, "Process Files", String.format("error flushing hdfs file %s: \n %s",  hadoopFilePath, e.toString()));
			throw e;
		}
	}
	
	protected void closeOutputAppender() throws JobExecutionException {
		try {
			logger.debug("closing hdfs file {}", hadoopFilePath);
			appender.close();
		} catch (IOException e) {
			logger.error("error closing hdfs file " + hadoopFilePath, e);
			monitor.error(monitorId, "Process Files", String.format("error closing hdfs file %s: \n %s",  hadoopFilePath, e.toString()));
			throw new JobExecutionException("error closing hdfs file " + hadoopFilePath, e);
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

