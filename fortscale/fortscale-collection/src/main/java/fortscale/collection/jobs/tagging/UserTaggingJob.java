package fortscale.collection.jobs.tagging;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.services.UserTagService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

public class UserTaggingJob extends FortscaleJob{
	
	@Autowired
	private UserTagService userTagService;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {}

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
		userTagService.update();
	}

}
