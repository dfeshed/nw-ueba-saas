package fortscale.collection.jobs.ad;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.utils.hdfs.HDFSLineAppender;
import fortscale.collection.hadoop.ImpalaClient;
import fortscale.collection.io.BufferedLineReader;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.morphlines.MorphlinesItemsProcessor;
import fortscale.collection.morphlines.RecordToStringItemsProcessor;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.logging.Logger;

@DisallowConcurrentExecution
public abstract class AdProcessJob extends FortscaleJob {
	private static Logger logger = Logger.getLogger(AdProcessJob.class);

	@Autowired
	protected ImpalaClient impalaClient;
	
	@Autowired
	private ImpalaParser impalaParser;
	
	


	protected MorphlinesItemsProcessor morphline;
	protected RecordToStringItemsProcessor recordToString;

	protected HDFSLineAppender appender;
	String hadoopFilePath;

	// job parameters:
	private String ldiftocsv;
	protected String inputPath;
	protected String finishPath;

	private String filesFilter;

	protected String hadoopDirPath;
	private String filenameFormat;
	protected String impalaTableName;
	
	String[] outputFields;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		// get parameters values from the job data map
		ldiftocsv = jobDataMapExtension.getJobDataMapStringValue(map, "ldiftocsv");
		inputPath = jobDataMapExtension.getJobDataMapStringValue(map, "inputPath");
		finishPath = jobDataMapExtension.getJobDataMapStringValue(map, "finishPath");
		filesFilter = jobDataMapExtension.getJobDataMapStringValue(map, "filesFilter");
		hadoopDirPath = jobDataMapExtension.getJobDataMapStringValue(map, "hadoopDirPath");
		filenameFormat = jobDataMapExtension.getJobDataMapStringValue(map, "filenameFormat");
		impalaTableName = jobDataMapExtension.getJobDataMapStringValue(map, "impalaTableName");
		

		// build record to items processor
		outputFields = jobDataMapExtension.getJobDataMapStringValue(map, "outputFields").split(",");
		String outputSeparator = jobDataMapExtension.getJobDataMapStringValue(map, "outputSeparator");
		recordToString = new RecordToStringItemsProcessor(outputSeparator, outputFields);

		morphline = jobDataMapExtension.getMorphlinesItemsProcessor(map, "morphlineFile");
		
		// generate filename according to the job name and time
		String filename = String.format(filenameFormat, (new Date()).getTime()/1000);
		hadoopFilePath = String.format("%s/%s", hadoopDirPath, filename);
	}

	@Override
	protected int getTotalNumOfSteps() {
		return 2;
	}

	@Override
	protected void runSteps() throws Exception {
		// list files in chronological order
		startNewStep("List Files");
		File[] files = listFiles(inputPath, filesFilter);
		
		if (files.length == 0) {
			finishStep();
			return;
		}

		if (files.length > 1) {
			logger.warn("moving old files to {}", finishPath);
			for (int i = 0; i < files.length - 1; i++) {
				moveFileToFolder(files[i], finishPath);
				logger.info("moving {} to {}", files[i], finishPath);
				monitor.warn(getMonitorId(), getStepName(), String.format("moving old file %s to %s", files[i], finishPath));
			}
		}
		finishStep();
		
		startNewStep("create hadoop file writer");
		// get hadoop file writer
		createOutputAppender();
		
		finishStep();

		
		try {
			processFile(files[files.length - 1]);
		} finally{
			closeOutputAppender();
		}
		
		refreshImpala();

	}
	
	protected void processFile(File file) throws Exception {
		startNewStep("process file");

		BufferedLineReader reader = null;
		Date runtime = new Date();
		try {
			logger.info("starting to process {}", file.getName());

			Process pr =  runCmd(null, ldiftocsv, file.getAbsolutePath());
			reader = new BufferedLineReader( new BufferedReader(new InputStreamReader(pr.getInputStream())));
			// transform events in file
			processFile(reader, runtime);

			moveFileToFolder(file, finishPath);

			logger.info("finished processing {}", file.getName());

		} catch (IOException e) {
			logger.error("error processing files", e);
			throw new JobExecutionException("error processing files", e);
		} finally{
			if(reader != null){
				reader.close();
			}
		}
		
		finishStep();
	}

	protected boolean processFile(BufferedLineReader reader, Date runtime) throws Exception {
		if(isTimestampAlreadyProcessed(runtime)){
			logger.warn("the following runtime ({}) was already processed.", runtime);
			return false;
		}
		String timestamp = impalaParser.formatTimeDate(runtime);
		String timestampepoch = Long.toString(impalaParser.getRuntime(runtime));
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			Record record = morphlineProcessLine(line);
			if(record != null){
				record.put("timestamp", timestamp);
				record.put("timestampepoch", timestampepoch);
				writeToHdfs(record);
				updateDb(record);
			}
		}


		if (reader.HasErrors()) {
			monitor.error(getMonitorId(), getStepName(), reader.getException().toString());
			return false;
		} else {
			if (reader.hasWarnings()) {
				monitor.warn(getMonitorId(), getStepName(), reader.getException().toString());
			}
			return true;
		}
	}
	
	
	protected abstract boolean isTimestampAlreadyProcessed(Date runtime);
	protected abstract void updateDb(Record record) throws Exception;
	

	protected Record morphlineProcessLine(String line){
		return morphline.process(line);
	}
	
	protected boolean writeToHdfs(Record record) throws IOException{
		String output = recordToString.process(record);

		// append to hadoop, if there is data to be written

		if (output != null) {
			appender.writeLine(output);
			return true;
		} else {
			return false;
		}
	}

	protected void refreshImpala() throws JobExecutionException {
		startNewStep("impala refresh");
		impalaClient.refreshTable(impalaTableName);
		finishStep();
	}

	protected void createOutputAppender() throws JobExecutionException {
		
		try {
			logger.debug("opening hdfs file {} for append", hadoopDirPath);

			appender = new HDFSLineAppender();
			
			appender.open(hadoopFilePath);

		} catch (IOException e) {
			logger.error("error opening hdfs file for append at " + hadoopFilePath, e);
			monitor.error(getMonitorId(), getStepName(), String.format("error opening hdfs file %s: \n %s", hadoopFilePath, e.toString()));
			throw new JobExecutionException("error opening hdfs file for append at " + hadoopFilePath, e);
		}
	}

	protected void flushOutputAppender() throws IOException {
		try {
			appender.flush();
		} catch (IOException e) {
			logger.error("error flushing hdfs file " + hadoopFilePath, e);
			monitor.error(getMonitorId(), getStepName(), String.format("error flushing hdfs file %s: \n %s", hadoopFilePath, e.toString()));
			throw e;
		}
	}

	protected void closeOutputAppender() throws JobExecutionException {
		try {
			logger.debug("closing hdfs file {}", hadoopFilePath);
			appender.close();
		} catch (IOException e) {
			logger.error("error closing hdfs file " + hadoopFilePath, e);
			monitor.error(getMonitorId(), getStepName(), String.format("error closing hdfs file %s: \n %s", hadoopFilePath, e.toString()));
			throw new JobExecutionException("error closing hdfs file " + hadoopFilePath, e);
		}
	}

	protected String[] getOutputFields() {
		return outputFields;
	}
	
	

}
