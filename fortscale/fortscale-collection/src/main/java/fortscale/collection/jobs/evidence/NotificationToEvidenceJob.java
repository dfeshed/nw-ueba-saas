package fortscale.collection.jobs.evidence;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.Notification;
import fortscale.domain.core.StatefulInternalStash;
import fortscale.domain.core.dao.NotificationsRepository;
import fortscale.domain.core.dao.StatefulInternalStashRepository;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import java.util.Date;
import java.util.List;

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
	private final String TOPIC_NAME = "fortscale-notification-event-score";

	@Autowired
	private NotificationsRepository notificationsRepository;
	@Autowired
	private StatefulInternalStashRepository statefulInternalStashRepository;

	@Override protected void runSteps() throws Exception {
		logger.debug("Running notification to evidence job");
		StatefulInternalStash stash = statefulInternalStashRepository.findBySuuid(StatefulInternalStash.SUUID);
		Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, TIME_STAMP));
		logger.debug("Getting notifications after time {}", stash.getLatest_ts());
		KafkaEventsWriter streamWriter = new KafkaEventsWriter(TOPIC_NAME);
		List<Notification> notifications = notificationsRepository.findByTsGreaterThan(stash.getLatest_ts(), sort);
		logger.debug("Found {} notifications, starting to send", notifications.size());
		for (Notification notification: notifications) {
			JSONObject evidence = new JSONObject();
			//TODO - need to understand score better, put properties in xml file and investigate normalized_username
			evidence.put("notification_score", 80);
			evidence.put("notification_cause", notification.getCause());
			evidence.put("normalized_username", notification.getName());
			streamWriter.send(notification.getIndex(), evidence.toJSONString(JSONStyle.NO_COMPRESS));
		}
		Date date = new Date();
		logger.debug("Finished running notification to evidence job at {}, updating timestamp", date);
		statefulInternalStashRepository.updateLatestTS(stash.getSuuid(), date.getTime());
	}

	@Override protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {}

	@Override protected int getTotalNumOfSteps() { return 1; }

	@Override protected boolean shouldReportDataReceived() { return false; }

}