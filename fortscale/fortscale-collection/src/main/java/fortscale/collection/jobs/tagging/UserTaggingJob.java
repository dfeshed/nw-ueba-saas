package fortscale.collection.jobs.tagging;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.tagging.service.UserTaggingService;

public class UserTaggingJob extends FortscaleJob{
	
	@Autowired
	private UserTaggingService userTaggingService;
	
	//Job parameters:
	private String tag;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		// get parameters values from the job data map		
        tag = jobDataMapExtension.getJobDataMapStringValue(map, "tag", null);
	}

	@Override
	protected int getTotalNumOfSteps() {
		return 1;
	}

	@Override
	protected boolean shouldReportDataReceived() {
		return false;
	}

	@Override
	protected void runSteps() throws Exception {
		if(tag != null){
			userTaggingService.update(tag);
		} else{
			userTaggingService.updateAll();
		}
	}

}
