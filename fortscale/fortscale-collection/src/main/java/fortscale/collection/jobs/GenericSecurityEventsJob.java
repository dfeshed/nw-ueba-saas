package fortscale.collection.jobs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import fortscale.collection.monitoring.ItemContext;
import fortscale.collection.morphlines.RecordExtensions;
import fortscale.streaming.task.monitor.TaskMonitoringHelper;
import org.kitesdk.morphline.api.Record;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import fortscale.collection.io.BufferedLineReader;
import fortscale.collection.morphlines.MorphlinesItemsProcessor;
import fortscale.monitor.domain.JobDataReceived;


public class GenericSecurityEventsJob extends FortscaleJob{
	private static Logger logger = LoggerFactory.getLogger(GenericSecurityEventsJob.class);
	
	@Value("${collection.fetch.data.path}")
	protected String inputPath;
	@Value("${collection.fetch.error.data.path}")
	protected String errorPath;
	@Value("${collection.fetch.finish.data.path}")
	protected String finishPath;
	@Value("${collection.lines.print.skip}")
	protected int linesPrintSkip;
	@Value("${collection.lines.print.enabled}")
	protected boolean linesPrintEnabled;

	/**
	 * taskMonitoringHelper is holding all the steps, errors, arrived events, successfully processed events,
	 * and drop events and save all those details to mongo
	 */
	@Autowired
	protected TaskMonitoringHelper<String> taskMonitoringHelper;

	protected String filesFilter;
	protected MorphlinesItemsProcessor morphline;
	protected List<String> timestampField; //Might be more the one security field
	protected String jobName;
	protected String sourceName;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		jobName = jobExecutionContext.getJobDetail().getKey().getName();
		sourceName = jobExecutionContext.getJobDetail().getKey().getGroup();
		filesFilter = jobDataMapExtension.getJobDataMapStringValue(map, "filesFilter");
		morphline = jobDataMapExtension.getMorphlinesItemsProcessor(map, "morphlineFile");
		initTimeStampField(map);

	}

	//Get timestamp field name.
	//May be more the one, so keep it in list.
	//Use empty list if possible
	protected void initTimeStampField(JobDataMap map) {

		try {
			timestampField= jobDataMapExtension.getJobDataMapListOfStringsValue(map, "timestampField", ",");
		} catch (JobExecutionException e){
			timestampField = new ArrayList<>();
		}
	}

	@Override
	protected int getTotalNumOfSteps() {
		return 2;
	}

	@Override
	protected boolean shouldReportDataReceived() {
		return true;
	}

	@Override
	protected void runSteps() throws Exception {
		startNewStep("listFiles");
		File[] files = listFiles(inputPath, filesFilter);
		finishStep("listFiles");
		
		runProcessFilesStep(files);
	}


	public void finishStep(String stepName){
		taskMonitoringHelper.finishStep(stepName);
	}

	@Override
	public void startNewStep(String stepName) {
		logger.info("Running {} ", stepName);
		taskMonitoringHelper.startStep(stepName);
	}

	protected void runProcessFilesStep(File[] files) throws IOException, JobExecutionException{
		startNewStep("Process files");

		float totalFiles = files.length;
		float totalDone = 0;

		try{
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
					taskMonitoringHelper.error(getStepName(), e.toString());
				}
				totalDone++;
				logger.info("{}/{} files processed - {}% done", totalDone, totalFiles,
						Math.round((totalDone / totalFiles) * 100));
			}
		} finally{
			morphline.close();
		}
		
		finishStep("Process files");
	}
	
	protected boolean processFile(File file) throws IOException, JobExecutionException {

		ItemContext itemContext = new ItemContext(file.getName(),taskMonitoringHelper);
		BufferedLineReader reader = new BufferedLineReader();
		reader.open(file);

		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		lnr.skip(Long.MAX_VALUE);
		float totalLines = lnr.getLineNumber() + 1; //Add 1 because line index starts at 0
		float numOfLines = 0;
		lnr.close();

		try {

			String line = null;
			while ((line = reader.readLine()) != null) {
				numOfLines++;
				taskMonitoringHelper.handleNewEvent(file.getName());
				Record record = processLine(line,itemContext);
				//If record parsed, Log the event as unfiltered events
				if (record != null){
					//If success - write the event to monitoring. filed event monitoing handled by monitoring
					Long  timestamp = null;
					//There might be different timestamps fields for different events
					//Look for the first timestampField exists
					for (String timestampFieldName : timestampField) {
							//Use default value to aviod exception if the timestamp field is not exists
							timestamp = RecordExtensions.getLongValue(record, timestampFieldName, -1L);
							if (timestamp >=0){
								break;
							}
					}

					if (timestamp != null) {
						taskMonitoringHelper.handleUnFilteredEvents(itemContext.getSourceName(), timestamp);
					}
				}
				if (linesPrintEnabled && numOfLines % linesPrintSkip == 0) {
					logger.info("{}/{} lines processed - {}% done", numOfLines, totalLines,
							Math.round((numOfLines / totalLines) * 100));
				}
			}			
			

		} catch (IOException e) {
			logger.error("error processing file " + file.getName(), e);
			//monitor.error(getMonitorId(), getStepName(), e.toString());
			return false;
		} finally {
			taskMonitoringHelper.saveJobStatusReport(jobName,false,sourceName);
			reader.close();
		}

		
		if (reader.HasErrors()) {
			logger.error("error processing file " + file.getName(), reader.getException());
			taskMonitoringHelper.error(getStepName(), reader.getException().toString());
			return false;
		} else {
			if (reader.hasWarnings()) {
				logger.warn("error processing file " + file.getName(), reader.getException());
				taskMonitoringHelper.error(getStepName(), reader.getException().toString());
			}
			return true;
		}
	}
		
	protected Record processLine(String line, ItemContext itemContext) throws IOException {

		return morphline.process(line, itemContext);
	}

	@Override
	protected boolean useOldMonitoring() {
		return false;
	}
}
