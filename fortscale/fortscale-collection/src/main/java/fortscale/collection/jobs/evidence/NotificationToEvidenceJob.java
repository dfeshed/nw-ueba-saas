package fortscale.collection.jobs.evidence;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.Notification;
import fortscale.domain.core.StatefulInternalStash;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.NotificationsRepository;
import fortscale.domain.core.dao.StatefulInternalStashRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.quartz.JobDataMap;
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

	// job parameters:
	private String topicName;
	private String timestampField;
	private String notificationScoreField;
	private String notificationCauseField;
	private String normalizedUsernameField;
	private int score;

	@Autowired
	private NotificationsRepository notificationsRepository;
	@Autowired
	private StatefulInternalStashRepository statefulInternalStashRepository;
	@Autowired
	private UserRepository userRepository;

	@Override
	protected void runSteps() throws Exception {
		logger.debug("Running notification to evidence job");
		//get the last runtime from the stateful_internal_stash Mongo repository
		StatefulInternalStash stash = statefulInternalStashRepository.findBySuuid(StatefulInternalStash.SUUID);
		Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, timestampField));
		logger.debug("Getting notifications after time {}", stash.getLatest_ts());
		KafkaEventsWriter streamWriter = new KafkaEventsWriter(topicName);
		//get all notifications that occurred after the last runtime of the job
		List<Notification> notifications = notificationsRepository.findByTsGreaterThan(stash.getLatest_ts(), sort);
		logger.debug("Found {} notifications, starting to send", notifications.size());
		for (Notification notification: notifications) {
			//convert each notification to evidence and send it to the appropriate Kafka topic
			JSONObject evidence = new JSONObject();
			//TODO - need to understand score better
			evidence.put(notificationScoreField, score);
			evidence.put(notificationCauseField, notification.getCause());
			evidence.put(normalizedUsernameField, getNormalizedUsername(notification));
			String messageToWrite = evidence.toJSONString(JSONStyle.NO_COMPRESS);
			logger.debug("Writing to topic evidence - {}", messageToWrite);
			streamWriter.send(notification.getIndex(), messageToWrite);
		}
		Date date = new Date();
		logger.debug("Finished running notification to evidence job at {}, updating timestamp", date);
		statefulInternalStashRepository.updateLatestTS(stash.getSuuid(), date.getTime());
	}

	private String getNormalizedUsername(Notification notification) {
		if (notification.getCause().equals("VPN_user_creds_share")) {
			return notification.getDisplayName();
		}
		//attempt to normalize username
		String normalizedUsername = notification.getName();
		//if username is an active directory distinguished name
		if (normalizedUsername.toLowerCase().contains("dc=")) {
			User user = userRepository.findByAdDn(normalizedUsername);
			if (user != null) {
				normalizedUsername = user.getUsername();
			}
		//if username is a short name
		} else if (!normalizedUsername.contains("@")) {
			//TODO - what about cache?
			List<User> users = userRepository.findUsersBysAMAccountName(normalizedUsername);
			if (users.size() == 1) {
				normalizedUsername = users.get(0).getUsername();
			}
		}
		return normalizedUsername;
	}

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.debug("Initializing Notification to Evidence job, getting job parameters");
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		topicName = jobDataMapExtension.getJobDataMapStringValue(map, "topicName");
		timestampField = jobDataMapExtension.getJobDataMapStringValue(map, "timestampField");
		notificationScoreField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationScoreField");
		notificationCauseField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationCauseField");
		normalizedUsernameField = jobDataMapExtension.getJobDataMapStringValue(map, "normalizedUsernameField");
		score = Integer.parseInt(jobDataMapExtension.getJobDataMapStringValue(map, "score"));
		logger.debug("Job initialized");
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}