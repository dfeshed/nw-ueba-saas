package fortscale.collection.jobs.ad;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.services.ComputerService;

/**
 * Goes over the computers in the mongodb and classify each computer 
 */
public class ClassifyComputersJob extends FortscaleJob {

	private static final Logger logger = LoggerFactory.getLogger(ClassifyComputersJob.class);
	
	@Autowired
	private ComputerService computerService;
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		// no parameters
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
		startNewStep("classify");
		
		try {
			computerService.classifyAllComputers();
		} catch (Exception e) {
			logger.error("error classifying computers", e);
			this.addError(e.getMessage());
		}
		
		finishStep();
	}

}
