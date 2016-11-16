package fortscale.collection.jobs.ad;

import fortscale.collection.io.BufferedLineReader;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.morphlines.MorphlinesItemsProcessor;
import fortscale.collection.morphlines.RecordToStringItemsProcessor;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.utils.impala.ImpalaClient;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.Date;

@DisallowConcurrentExecution
public abstract class AdProcessJob extends FortscaleJob {
	private static Logger logger = Logger.getLogger(AdProcessJob.class);

	@Autowired
	protected ImpalaClient impalaClient;
	
	@Autowired
	private ImpalaParser impalaParser;
	
	@Value("${collection.lines.print.skip}")
	protected int linesPrintSkip;
	@Value("${collection.lines.print.enabled}")
	protected boolean linesPrintEnabled;


	protected MorphlinesItemsProcessor morphline;
	protected RecordToStringItemsProcessor recordToString;

	// job parameters:
	private String ldiftocsv;
	protected String inputPath;
	protected String finishPath;
	protected String errorPath;

	private String filesFilter;

	@Autowired
	protected StatsService statsService;
	
	String[] outputFields;

	String outputSeparator;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		// get parameters values from the job data map
		ldiftocsv = jobDataMapExtension.getJobDataMapStringValue(map, "ldiftocsv");
		inputPath = jobDataMapExtension.getJobDataMapStringValue(map, "inputPath");
		finishPath = jobDataMapExtension.getJobDataMapStringValue(map, "finishPath");
		errorPath = jobDataMapExtension.getJobDataMapStringValue(map, "errorPath");
		filesFilter = jobDataMapExtension.getJobDataMapStringValue(map, "filesFilter");

		// build record to items processor
		outputFields = ImpalaParser.getTableFieldNamesAsArray(jobDataMapExtension.getJobDataMapStringValue(map, "outputFields"));
		outputSeparator = jobDataMapExtension.getJobDataMapStringValue(map, "outputSeparator");
		recordToString = new RecordToStringItemsProcessor(outputSeparator, statsService, "AdProcessJob", outputFields);

		morphline = jobDataMapExtension.getMorphlinesItemsProcessor(map, "morphlineFile");

	}

	@Override
	protected int getTotalNumOfSteps() {
		return 4;
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
		
		finishStep();

		
		try {
			processFile(files[files.length - 1]);
		} finally{
			morphline.close();
		}

		
		runFinalStep();

	}
	
	protected void runFinalStep() throws Exception{
		//by default do nothing
	}
	
	
	protected void processFile(File file) throws Exception {
		startNewStep("process file");

		BufferedLineReader reader = null;
		Date runtime = new Date();
		try {
            logger.info("starting to process {}", file.getName());
            if(ldiftocsv == null){
                reader = new BufferedLineReader( new BufferedReader(new FileReader(file)));
                processFile(file, reader, runtime);
            }else{
                Process pr =  runCmd(null, ldiftocsv, file.getAbsolutePath());
                reader = new BufferedLineReader( new BufferedReader(new InputStreamReader(pr.getInputStream())));
                // transform events in file
                processFile(null, reader, runtime);

                if(pr.waitFor() != 0){
                    handleCmdFailure(pr, ldiftocsv);
                    throw new JobExecutionException(String.format("got error while running shell command %s", ldiftocsv));
                }
            }
            logger.info("finished processing {}", file.getName());

		} catch (Exception e) {
			moveFileToFolder(file, errorPath);
			logger.error("error processing files", e);
			throw new JobExecutionException("error processing files", e);
		} finally{
			if(reader != null){
				reader.close();
			}
		}
		
		moveFileToFolder(file, finishPath);
		
		finishStep();
	}

	protected boolean processFile(File file, BufferedLineReader reader, Date runtime) throws Exception {
		if(isTimestampAlreadyProcessed(runtime)){
			logger.warn("the following runtime ({}) was already processed.", runtime);
			return false;
		}
		String runtimeString = impalaParser.formatTimeDate(runtime);
		String timestampepoch = Long.toString(impalaParser.getRuntime(runtime));

		long totalLines = 0;

		if (file != null) {
			LineNumberReader lnr = new LineNumberReader(new FileReader(file));
			lnr.skip(Long.MAX_VALUE);
			totalLines = lnr.getLineNumber() + 1; //Add 1 because line index starts at 0
			lnr.close();
		}
		long numOfLines = 0;
		
		String line = null;
		int counter = 0;
		while ((line = reader.readLine()) != null) {
			numOfLines++;
			Record record = morphlineProcessLine(line);
			if(record != null){
				if(updateDb(record)){
					counter++;
				}
			}
			if (linesPrintEnabled && numOfLines % linesPrintSkip == 0) {
				if (totalLines > 0) {
					logger.info("{}/{} lines processed - {}% done", numOfLines, totalLines,
							Math.round(((float) numOfLines / (float) totalLines) * 100));
				} else {
					logger.info("{} lines processed", numOfLines);
				}
			}
		}
		
		monitor.addDataReceived(getMonitorId(), new JobDataReceived(getDataRecievedType(), new Integer(counter), ""));
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
	
	protected abstract String getDataRecievedType();
	protected abstract boolean isTimestampAlreadyProcessed(Date runtime);
	protected abstract boolean updateDb(Record record) throws Exception;
	

	protected Record morphlineProcessLine(String line){
		return morphline.process(line, null);
	}

	protected String[] getOutputFields() {
		return outputFields;
	}
	
	@Override
	protected boolean shouldReportDataReceived() {
		return true;
	}


}
