package fortscale.collection.jobs.cleanup;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.services.EvidencesService;
import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;
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

	private final String START_TIME = "start_time";
	private final String END_TIME = "end_time";

	@Autowired
	private EvidencesService evidencesService;

	private DateTime startTime;
	private DateTime endTime;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		// get parameters values from the job data map
		String startStr = jobDataMapExtension.getJobDataMapStringValue(map, START_TIME);
		String endStr = jobDataMapExtension.getJobDataMapStringValue(map, END_TIME);
		startTime = new DateTime(startStr);
		endTime = new DateTime(endStr);
	}

	@Override
	protected void runSteps() throws Exception {
		startNewStep("Running Clean Indicators job");
		long foundRecords = evidencesService.deleteEvidenceBetween(startTime.toDate(), endTime.toDate());
		logger.info("Deleted {} indicators", foundRecords);
		finishStep();
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}