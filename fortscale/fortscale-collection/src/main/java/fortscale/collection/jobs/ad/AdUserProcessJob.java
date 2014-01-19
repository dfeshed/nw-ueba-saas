package fortscale.collection.jobs.ad;

import java.io.File;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.JobDataMapExtension;
import fortscale.collection.jobs.FortscaleJob;

public class AdUserProcessJob extends FortscaleJob {

	@Autowired
	private JobDataMapExtension jobDataMapExtension;
	
	protected String inputPath;
	private String outputPath;
	private String filesFilter;
	
	private File outputFile;
	
	
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		// get parameters values from the job data map
		inputPath = jobDataMapExtension.getJobDataMapStringValue(map, "inputPath");
		outputPath = jobDataMapExtension.getJobDataMapStringValue(map, "outputPath");
		filesFilter = jobDataMapExtension.getJobDataMapStringValue(map, "filesFilter");
	}

	@Override
	protected int getTotalNumOfSteps(){
		return 2;
	}

	@Override
	protected void runSteps() throws Exception {
		// list files in chronological order
		startNewStep("List Files");
		File[] files = listFiles(inputPath,filesFilter);
		finishStep();

	}

}
