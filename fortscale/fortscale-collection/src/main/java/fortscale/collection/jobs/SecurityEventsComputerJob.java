package fortscale.collection.jobs;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import fortscale.collection.monitoring.ItemContext;
import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fortscale.collection.morphlines.MorphlinesItemsProcessor;
import org.springframework.beans.factory.annotation.Value;

@DisallowConcurrentExecution
public class SecurityEventsComputerJob extends GenericSecurityEventsJob {

	private static Logger logger = LoggerFactory.getLogger(SecurityEventsComputerJob.class);
	
	private Map<String, MorphlinesItemsProcessor> morphlineForEventCode = new HashMap<String, MorphlinesItemsProcessor>();
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		jobName = jobExecutionContext.getJobDetail().getKey().getName();
		sourceName = jobExecutionContext.getJobDetail().getKey().getGroup();
		filesFilter = jobDataMapExtension.getJobDataMapStringValue(map, "filesFilter");
		morphline = jobDataMapExtension.getMorphlinesItemsProcessor(map, "morphlineFile");

		initTimeStampField(map);

		for (String specificMorphlineKey : jobDataMapExtension.getJobDataMapKeysStartingWith(map, "eventMorphlineFile")) {
			// extract the event code and get the morphline processor
			String eventCode = specificMorphlineKey.substring(18);
			MorphlinesItemsProcessor processor = jobDataMapExtension.getMorphlinesItemsProcessor(map, specificMorphlineKey);
			morphlineForEventCode.put(eventCode, processor);
		}
		
	}
	
	@Override
	protected Record processLine(String line, ItemContext itemContext) throws IOException {
		// run the basic morphline to get the event code from the message
		Record record = super.processLine(line, itemContext);
		
		// run specific morphline for event code
		if (record!=null) {
			// get the event code
			Object eventCode = record.getFirstValue("eventCode");
			if (eventCode!=null) {
				MorphlinesItemsProcessor processor = morphlineForEventCode.get(eventCode.toString());
				if (processor!=null) {
					record = processor.process(record, itemContext);
				}
			}
		}
		return record;
	}
	
	@Override
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

					totalDone++;
					logger.info("{}/{} files processed - {}% done", totalDone, totalFiles,
							(totalDone / totalFiles) * 100);

				} catch (Exception e) {
					moveFileToFolder(file, errorPath);

					logger.error("error processing file " + file.getName(), e);
					taskMonitoringHelper.error(getStepName(), e.toString());
				}
			}
		} finally{
			morphline.close();
			for (MorphlinesItemsProcessor processor : morphlineForEventCode.values())
				processor.close();
		}
		
		finishStep();
	}

}
