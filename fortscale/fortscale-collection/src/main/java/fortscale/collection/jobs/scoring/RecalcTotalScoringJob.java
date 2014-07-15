package fortscale.collection.jobs.scoring;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.services.UserServiceFacade;
import fortscale.utils.logging.Logger;

@DisallowConcurrentExecution
public class RecalcTotalScoringJob extends FortscaleJob {
	
	private static Logger logger = Logger.getLogger(RecalcTotalScoringJob.class);

	@Autowired
	private UserServiceFacade userServiceFacade;
		

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
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
		logger.info("recalculateTotalScore");
		userServiceFacade.recalculateTotalScore();
	}
	
	
}
