package fortscale.collection.jobs;

import java.io.File;
import java.io.IOException;

import org.kitesdk.morphline.api.Record;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import fortscale.collection.io.BufferedLineReader;
import fortscale.collection.morphlines.MorphlinesItemsProcessor;
import fortscale.monitor.domain.JobDataReceived;


public class GenericSecurityEventsJob extends FortscaleJob{
	private static Logger logger = LoggerFactory.getLogger(GenericSecurityEventsJob.class);
	
	@Value("${collection.fetch.data.path}")
	protected String inputPath;
	@Value("${collection.fetch.error.data.path}")
	private String errorPath;
	@Value("${collection.fetch.finish.data.path}")
	private String finishPath;
	
	protected String filesFilter;
	protected MorphlinesItemsProcessor morphline;
		
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		
		filesFilter = jobDataMapExtension.getJobDataMapStringValue(map, "filesFilter");
		morphline = jobDataMapExtension.getMorphlinesItemsProcessor(map, "morphlineFile");
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
		finishStep();
		
		runProcessFilesStep(files);
	}

	private void runProcessFilesStep(File[] files) throws IOException, JobExecutionException{
		startNewStep("Process files");
		
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
					monitor.error(getMonitorId(), getStepName(), e.toString());
				}
			}
		} finally{
			morphline.close();
		}
		
		finishStep();
	}
	
	protected boolean processFile(File file) throws IOException, JobExecutionException {
		
		BufferedLineReader reader = new BufferedLineReader();
		reader.open(file);
		
		try {
			int lineCounter = 0;
			String line = null;
			while ((line = reader.readLine()) != null) {
				Record record = processLine(line);
				if (record != null){
					++lineCounter;
				}
			}			
			
			monitor.addDataReceived(getMonitorId(), new JobDataReceived(file.getName(), lineCounter, "Events"));
		} catch (IOException e) {
			logger.error("error processing file " + file.getName(), e);
			monitor.error(getMonitorId(), getStepName(), e.toString());
			return false;
		} finally {
			reader.close();
		}

		
		if (reader.HasErrors()) {
			logger.error("error processing file " + file.getName(), reader.getException());
			monitor.error(getMonitorId(), getStepName(), reader.getException().toString());
			return false;
		} else {
			if (reader.hasWarnings()) {
				logger.warn("error processing file " + file.getName(), reader.getException());
				monitor.warn(getMonitorId(), getStepName(), reader.getException().toString());
			}
			return true;
		}
	}
		
	protected Record processLine(String line) throws IOException {
		return morphline.process(line);		
	}
}
