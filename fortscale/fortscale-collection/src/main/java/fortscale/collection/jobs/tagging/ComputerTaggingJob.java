package fortscale.collection.jobs.tagging;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.tagging.service.SensitiveMachineService;

public class ComputerTaggingJob extends FortscaleJob{
	
	@Autowired
	private SensitiveMachineService sensitiveMachineService;
	
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
		sensitiveMachineService.updateSensitiveMachines();
	}

}
