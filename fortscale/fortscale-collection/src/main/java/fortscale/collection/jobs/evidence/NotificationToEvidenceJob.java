package fortscale.collection.jobs.evidence;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.Notification;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.NotificationsRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fetch.FetchConfiguration;
import fortscale.domain.fetch.FetchConfigurationRepository;
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
	private String notificationsToIgnore;
	private String fetchType;
	private String topicName;
	private String notificationScoreField;
	private String notificationCauseField;
	private String normalizedUsernameField;
	private String notificationEntityField;
	private String notificationTimestampField;
	private String score;

	@Autowired
	private NotificationsRepository notificationsRepository;
	@Autowired
	private FetchConfigurationRepository fetchConfigurationRepository;
	@Autowired
	private UserRepository userRepository;

	@Override
	protected void runSteps() throws Exception {
		logger.debug("Running notification to evidence job");
		//get the last runtime from the fetchConfiguration Mongo repository
		FetchConfiguration fetchConfiguration = fetchConfigurationRepository.findByType(fetchType);
		if (fetchConfiguration == null) {
			//if no last runtime - create a one and save it in the collection
			fetchConfiguration = new FetchConfiguration(fetchType, new Date(0L).getTime() + "");
		}
		//TODO - do we really need sort?
		Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "ts"));
		long lastFetchTime = Long.parseLong(fetchConfiguration.getLastFetchTime());
		logger.debug("Getting notifications after time {}", lastFetchTime);
		KafkaEventsWriter streamWriter = new KafkaEventsWriter(topicName);
		//get all notifications that occurred after the last runtime of the job
		List<Notification> notifications = notificationsRepository.findByTsGreaterThan(lastFetchTime, sort);
		if (notifications.size() > 0) {
			logger.debug("Found {} notifications, starting to send", notifications.size());
		} else {
			logger.debug("No new notifications found");
		}
		for (Notification notification: notifications) {
			if (notificationsToIgnore.contains(notification.getCause())) {
				continue;
			}
			//convert each notification to evidence and send it to the appropriate Kafka topic
			JSONObject evidence = new JSONObject();
			evidence.put(notificationScoreField, score);
			evidence.put(notificationTimestampField, notification.getTs());
			evidence.put(notificationCauseField, notification.getCause());
			evidence.put(notificationEntityField, getEntity(notification));
			evidence.put(normalizedUsernameField, getNormalizedUsername(notification));
			String messageToWrite = evidence.toJSONString(JSONStyle.NO_COMPRESS);
			logger.debug("Writing to topic evidence - {}", messageToWrite);
			streamWriter.send(notification.getIndex(), messageToWrite);
		}
		Date date = new Date();
		logger.debug("Finished running notification to evidence job at {}, updating timestamp in Mongo", date);
		fetchConfiguration.setLastFetchTime(date.getTime() + "");
		fetchConfigurationRepository.save(fetchConfiguration);
	}

	private String getEntity(Notification notification) {
		if (notification.getCause().toLowerCase().contains("amt")) {
			return "amt";
		}
		if (notification.getCause().toLowerCase().contains("vpn")) {
			return "vpn";
		}
		//TODO - what type of entity should AD notifications have? what about other types?
		return "user";
	}

	private String getNormalizedUsername(Notification notification) {
		//TODO - save this notification name somewhere?
		if (notification.getCause().equals("VPN_user_creds_share")) {
			return notification.getDisplayName();
		}
		//attempt to normalize username
		String normalizedUsername = notification.getName();
		//TODO - what about cache?
		//if username is an active directory distinguished name
		if (normalizedUsername.toLowerCase().contains("dc=")) {
			User user = userRepository.findByAdDn(normalizedUsername);
			if (user != null) {
				normalizedUsername = user.getUsername();
			}
		//if username is a short name
		} else if (!normalizedUsername.contains("@")) {
			List<User> users = userRepository.findUsersBysAMAccountName(normalizedUsername);
			if (users.size() == 1) {
				normalizedUsername = users.get(0).getUsername();
			}
		}
		return normalizedUsername;
	}

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.debug("Initializing NotificationToEvidence job - getting job parameters");
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		notificationsToIgnore = jobDataMapExtension.getJobDataMapStringValue(map, "notificationsToIgnore");
		fetchType = jobDataMapExtension.getJobDataMapStringValue(map, "fetchType");
		topicName = jobDataMapExtension.getJobDataMapStringValue(map, "topicName");
		notificationScoreField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationScoreField");
		notificationCauseField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationCauseField");
		normalizedUsernameField = jobDataMapExtension.getJobDataMapStringValue(map, "normalizedUsernameField");
		notificationEntityField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationEntityField");
		notificationTimestampField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationTimestampField");
		score = jobDataMapExtension.getJobDataMapStringValue(map, "score");
		logger.debug("Job initialized");
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}