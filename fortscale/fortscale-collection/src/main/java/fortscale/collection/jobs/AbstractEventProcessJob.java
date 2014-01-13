package fortscale.collection.jobs;

import static fortscale.collection.JobDataMapExtension.getJobDataMapStringValue;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;

import org.kitesdk.morphline.api.Record;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

import fortscale.collection.hadoop.HDFSLineAppender;
import fortscale.collection.io.BufferedLineReader;
import fortscale.collection.morphlines.MorphlinesItemsProcessor;
import fortscale.collection.morphlines.RecordToStringItemsProcessor;
import fortscale.monitor.JobProgressReporter;
import fortscale.monitor.domain.JobDataReceived;

/**
 * Abstract class to help build event process jobs from saved files
 * to hadoop
 */
public class AbstractEventProcessJob implements Job {

	private static Logger logger = LoggerFactory.getLogger(AbstractEventProcessJob.class);
	
	protected String jobName;
	protected String dataSourceName;
	
	protected String inputPath;
	protected String errorPath;
	protected String finishPath;
	protected String filesFilter;
	protected MorphlinesItemsProcessor morphline;
	protected RecordToStringItemsProcessor recordToString;
	protected String monitorId;
	protected String hadoopFilePath;
	protected String impalaTableName;
	
	@Autowired
	protected ResourceLoader resourceLoader;
	
	@Autowired
	protected JobProgressReporter monitor;
	
	public AbstractEventProcessJob(String jobName, String dataSourceName) {
		Assert.notNull(jobName);
		Assert.notNull(dataSourceName);
		this.jobName = jobName;
		this.dataSourceName = dataSourceName;
	}
	
	
	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();

		// get parameters values from the job data map
		inputPath = getJobDataMapStringValue(map, "inputPath");
		errorPath = getJobDataMapStringValue(map, "errorPath");
		finishPath = getJobDataMapStringValue(map, "finishPath");
		filesFilter = getJobDataMapStringValue(map, "filesFilter");
		hadoopFilePath = getJobDataMapStringValue(map, "hadoopFilePath");
		impalaTableName = getJobDataMapStringValue(map, "impalaTableName");
		
		// build record to items processor
		String[] outputFields = getJobDataMapStringValue(map, "outputFields").split(",");
		String outputSeparator = getJobDataMapStringValue(map, "outputSeparator");
		recordToString = new RecordToStringItemsProcessor(outputSeparator, outputFields);
		
		try {
			Resource morphlineConf = resourceLoader.getResource(getJobDataMapStringValue(map, "morphlineFile"));
			morphline = new MorphlinesItemsProcessor(morphlineConf);
		} catch (IOException e) {
			logger.error("error loading morphline processor", e);
			throw new JobExecutionException("error loading morphline processor", e);
		}
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("{} {} job started", jobName, dataSourceName);
		monitorId = monitor.startJob(dataSourceName, jobName, 3);

		// get parameters from job data map
		monitor.startStep(monitorId, "Get Job Parameters", 1);
		getJobParameters(context);
		monitor.finishStep(monitorId, "Get Job Parameters");

		// list files in chronological order
		monitor.startStep(monitorId, "List Files", 2);
		File[] files = listFiles(inputPath);
		monitor.finishStep(monitorId, "List Files");

		// start process file stage
		logger.info("processing {} files in {}", files.length, inputPath);
		monitor.startStep(monitorId, "Process Files", 3);
		
		// get hadoop file writer
		HDFSLineAppender appender = createHDFSLineAppender();
		
		// read each file and process lines
		try {
			for (File file : files) {
				logger.info("starting to process {}", file.getName()); 
				
				// transform events in file
				boolean success = processFile(file, appender);
				
				if (success) {
					moveFileToFolder(file, finishPath);
				} else {
					moveFileToFolder(file, errorPath);
				}
	
				logger.info("finished processing {}", file.getName());
			}
		} catch (IOException e) {
			logger.error("error processing files", e);
			monitor.error(monitorId, "Process Files", e.toString());
		}
		
		closeHDFSAppender(appender);	
		refreshImpala(impalaTableName);
		
		monitor.finishStep(monitorId, "Process Files");

		monitor.finishJob(monitorId);
		logger.info("{} {} job started", jobName, dataSourceName);
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
	
	
	protected boolean processFile(File file, HDFSLineAppender hadoop) throws IOException {
		
		BufferedLineReader reader = new BufferedLineReader();
		reader.open(file);
			
		try {
			int lineCounter = 0;
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (processLine(line, hadoop))
					++lineCounter;
			}
			
			monitor.addDataReceived(monitorId, new JobDataReceived(file.getName(), lineCounter, "Events"));
		
			// flush hadoop
			hadoop.flush();
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

	protected boolean processLine(String line, HDFSLineAppender hadoop) throws IOException {
		// process each line
		Record record = morphline.process(line);
		String output = recordToString.process(record);
		
		// append to hadoop, if there is data to be written
		if (record!=null) {
			hadoop.writeLine(output);
			return true;
		} else {
			return false;
		}
	}
	
	protected void refreshImpala(String tableName) throws JobExecutionException {
		
	}
	
	protected HDFSLineAppender createHDFSLineAppender() throws JobExecutionException {
		try {
			logger.debug("opening hdfs file {} for append", hadoopFilePath);
			HDFSLineAppender appender = new HDFSLineAppender();
			appender.open(hadoopFilePath);
			return appender;
		} catch (IOException e) {
			logger.error("error opening hdfs file for append at " + hadoopFilePath, e);
			monitor.error(monitorId, "Process Files", String.format("error appending to hdfs file %s: \n %s",  hadoopFilePath, e.toString()));
			throw new JobExecutionException("error opening hdfs file for append at " + hadoopFilePath, e);
		}
	}
	
	protected void closeHDFSAppender(HDFSLineAppender appender) throws JobExecutionException {
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

