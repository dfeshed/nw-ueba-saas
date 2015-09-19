package fortscale.collection.jobs.cleanup;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.services.EvidencesService;
import fortscale.utils.logging.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by Amir Keren on 18/09/2015.
 *
 * This task clears indicators after a particular date
 *
 */
public class CleanIndicatorsJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(CleanIndicatorsJob.class);

	@Autowired
	private EvidencesService evidencesService;

	private Date timeAfterWhichToDelete;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		// get parameters values from the job data map
		int hoursBack = jobDataMapExtension.getJobDataMapIntValue(map, "hoursBack");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -hoursBack);
		timeAfterWhichToDelete = calendar.getTime();
	}

	@Override
	protected void runSteps() throws Exception {
		startNewStep("Running Clean Indicators job");
		long foundRecords = evidencesService.deleteEvidenceAfterTime(timeAfterWhichToDelete);
		logger.info("Deleted {} indicators", foundRecords);
		finishStep();
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}