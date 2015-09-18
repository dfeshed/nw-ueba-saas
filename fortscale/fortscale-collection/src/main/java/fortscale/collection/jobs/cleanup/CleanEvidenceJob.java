package fortscale.collection.jobs.cleanup;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.Notification;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.NotificationsRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fetch.FetchConfiguration;
import fortscale.domain.fetch.FetchConfigurationRepository;
import fortscale.services.EvidencesService;
import fortscale.services.impl.SamAccountNameService;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.*;

/**
 * Created by Amir Keren on 18/09/2015.
 *
 * This task clears evidence after a particular date
 *
 */
public class CleanEvidenceJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(CleanEvidenceJob.class);

	@Autowired
	private EvidencesService evidencesService;

	private long timeAfterWhichToDelete;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		// get parameters values from the job data map
		int hoursBack = jobDataMapExtension.getJobDataMapIntValue(map, "hoursBack");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -hoursBack);
		timeAfterWhichToDelete = calendar.getTimeInMillis();
	}

	@Override
	protected void runSteps() throws Exception {
		startNewStep("Running Clean Evidence job");
		long foundRecords = evidencesService.deleteEvidenceAfterTime(timeAfterWhichToDelete);
		logger.info("Deleted {} evidence", foundRecords);
		finishStep();
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}