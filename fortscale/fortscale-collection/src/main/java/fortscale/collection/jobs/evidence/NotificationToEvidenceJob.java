package fortscale.collection.jobs.evidence;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.Notification;
import fortscale.domain.core.StatefulInternalStash;
import fortscale.domain.core.dao.NotificationsRepository;
import fortscale.domain.core.dao.StatefulInternalStashRepository;
import fortscale.utils.logging.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.Date;

/**
 * Created by Amir Keren on 26/07/2015.
 *
 * This task runs in batches in a constant interval, collects all of the notifications from Mongo that were created
 * since its last run and converts them into Evidence objects. Finally, it pushes the Evidence objects to the proper
 * Kafka topic to be streamed into the system.
 *
 */
public class NotificationToEvidenceJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(NotificationToEvidenceJob.class);

	private final String TIME_STAMP = "ts";

	@Autowired
	private NotificationsRepository notificationsRepository;
	@Autowired
	private StatefulInternalStashRepository statefulInternalStashRepository;

	@Override protected void runSteps() throws Exception {
		logger.debug("Running notification to evidence job");
		StatefulInternalStash stash = statefulInternalStashRepository.findBySuuid(StatefulInternalStash.SUUID);
		Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, TIME_STAMP));
		logger.debug("Getting notifications after time {}", stash.getLatest_ts());
		for (Notification notification: notificationsRepository.findByTsGreaterThan(stash.getLatest_ts(), sort)) {
			//TODO - convert notification to event format, send it to kafka topic
		}
		logger.debug("Finished running notification to evidence job, updating timestamp");
		statefulInternalStashRepository.updateLatestTS(stash.getSuuid(), new Date().getTime());
	}

	@Override protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {}

	@Override protected int getTotalNumOfSteps() { return 1; }

	@Override protected boolean shouldReportDataReceived() { return false; }

}