package fortscale.collection.jobs.cleanup;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.fetch.FetchConfiguration;
import fortscale.domain.fetch.FetchConfigurationRepository;
import fortscale.services.EvidencesService;
import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Amir Keren on 18/09/2015.
 *
 * This task clears indicators after a particular date
 *
 */
public class CleanIndicatorsJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(CleanIndicatorsJob.class);

	private final String FETCH_TYPE = "fetch_type";

	private String fetchConfigurationType;

	@Autowired
	private EvidencesService evidencesService;
	@Autowired
	private FetchConfigurationRepository fetchConfigurationRepository;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		// get parameters values from the job data map
		fetchConfigurationType = jobDataMapExtension.getJobDataMapStringValue(map, FETCH_TYPE);
	}

	@Override
	protected void runSteps() throws Exception {
		startNewStep("Running Clean Indicators job");
		//get the last runtime from the fetchConfiguration Mongo repository
		FetchConfiguration fetchConfiguration = fetchConfigurationRepository.findByType(fetchConfigurationType);
		if (fetchConfiguration != null) {
			long lastFetchTime = Long.parseLong(fetchConfiguration.getLastFetchTime());
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(lastFetchTime);
			long deletedRecords = evidencesService.deleteEvidenceAfter(calendar.getTime());
			logger.info("Deleted {} indicators", deletedRecords);
		} else {
			logger.warn("No step configuration found");
		}
		finishStep();
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}