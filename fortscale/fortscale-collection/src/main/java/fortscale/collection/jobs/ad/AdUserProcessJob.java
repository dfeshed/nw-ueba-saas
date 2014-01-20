package fortscale.collection.jobs.ad;

import java.io.File;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.JobDataMapExtension;
import fortscale.collection.jobs.FortscaleJob;

@DisallowConcurrentExecution
public class AdUserProcessJob extends FortscaleJob {

	@Autowired
	private JobDataMapExtension jobDataMapExtension;
	
	private File outputFile;
	
	//job parameters:
	protected String inputPath;
	protected String finishPath;
	
	private String outputPath;
	private String filesFilter;
	
	protected String hadoopFilePath;
	protected String impalaTableName;
	
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		// get parameters values from the job data map
		inputPath = jobDataMapExtension.getJobDataMapStringValue(map, "inputPath");
		finishPath = jobDataMapExtension.getJobDataMapStringValue(map, "finishPath");
		outputPath = jobDataMapExtension.getJobDataMapStringValue(map, "outputPath");
		filesFilter = jobDataMapExtension.getJobDataMapStringValue(map, "filesFilter");
		hadoopFilePath = jobDataMapExtension.getJobDataMapStringValue(map, "hadoopFilePath");
		impalaTableName = jobDataMapExtension.getJobDataMapStringValue(map, "impalaTableName");
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
