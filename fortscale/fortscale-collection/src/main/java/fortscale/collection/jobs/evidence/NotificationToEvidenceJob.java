package fortscale.collection.jobs.evidence;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.Notification;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.NotificationsRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fetch.FetchConfiguration;
import fortscale.domain.fetch.FetchConfigurationRepository;
import fortscale.services.impl.SamAccountNameService;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;

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

	private String SORT_FIELD = "ts";
	private String SPECIAL_NOTIFICATION = "VPN_user_creds_share";

	// job parameters:
	private String notificationsToIgnore;
	private String fetchType;
	private String topicName;
	private String notificationScoreField;
	private String notificationValueField;
	private String normalizedUsernameField;
	private String notificationEntityField;
	private String notificationStartTimestampField;
	private String notificationEndTimestampField;
	private String notificationTypeField;
	private String notificationSupportingInformationField;
	private String score;
	private Map<String, List<String>> notificationAnomalyMap;

	@Autowired
	private NotificationsRepository notificationsRepository;
	@Autowired
	private FetchConfigurationRepository fetchConfigurationRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SamAccountNameService samAccountNameService;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.debug("Initializing NotificationToEvidence job - getting job parameters");
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		notificationsToIgnore = jobDataMapExtension.getJobDataMapStringValue(map, "notificationsToIgnore");
		fetchType = jobDataMapExtension.getJobDataMapStringValue(map, "fetchType");
		topicName = jobDataMapExtension.getJobDataMapStringValue(map, "topicName");
		notificationScoreField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationScoreField");
		notificationValueField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationValueField");
		normalizedUsernameField = jobDataMapExtension.getJobDataMapStringValue(map, "normalizedUsernameField");
		notificationEntityField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationEntityField");
		notificationStartTimestampField = jobDataMapExtension.getJobDataMapStringValue(map,
				"notificationStartTimestampField");
		notificationEndTimestampField = jobDataMapExtension.getJobDataMapStringValue(map,
				"notificationEndTimestampField");
		notificationTypeField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationTypeField");
		notificationSupportingInformationField = jobDataMapExtension.getJobDataMapStringValue(map,
				"notificationSupportingInformationField");
		score = jobDataMapExtension.getJobDataMapStringValue(map, "score");
		notificationAnomalyMap = createAnomalyMap(jobDataMapExtension.getJobDataMapStringValue(map,
				"notificationAnomalyMap"));
		logger.debug("Job initialized");
	}

	@Override
	protected void runSteps() throws Exception {
		logger.debug("Running notification to evidence job");
		Date date = new Date();
		String dateStr = date.getTime() + "";
		//get the last runtime from the fetchConfiguration Mongo repository
		FetchConfiguration fetchConfiguration = fetchConfigurationRepository.findByType(fetchType);
		if (fetchConfiguration == null) {
			//if no last runtime - create a one and save it in the collection
			fetchConfiguration = new FetchConfiguration(fetchType, new Date(0L).getTime() + "");
		}
		Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, SORT_FIELD));
		long lastFetchTime = Long.parseLong(fetchConfiguration.getLastFetchTime());
		logger.debug("Getting notifications after time {}", lastFetchTime);
		//get all notifications that occurred after the last runtime of the job
		List<Notification> notifications = notificationsRepository.findByTsGreaterThanExcludeComments(lastFetchTime,
				sort);
		if (notifications.size() > 0) {
			logger.debug("Found {} notifications, starting to send", notifications.size());
		} else {
			logger.debug("No new notifications found");
		}
		KafkaEventsWriter streamWriter = new KafkaEventsWriter(topicName);
		try{
			for (Notification notification: notifications) {
				if (notificationsToIgnore.contains(notification.getCause())) {
					continue;
				}
				//convert each notification to evidence and send it to the appropriate Kafka topic
				JSONObject evidence = new JSONObject();
				evidence.put(notificationScoreField, score);
				evidence.put(notificationStartTimestampField, notification.getTs());
				evidence.put(notificationEndTimestampField, notification.getTs());
				evidence.put(notificationTypeField, notification.getCause());
				evidence.put(notificationValueField, getAnomalyField(notification));
				evidence.put(notificationEntityField, getEntity(notification));
				evidence.put(normalizedUsernameField, getNormalizedUsername(notification));
				evidence.put(notificationSupportingInformationField, getSupportingInformation(notification));
				String messageToWrite = evidence.toJSONString(JSONStyle.NO_COMPRESS);
				logger.debug("Writing to topic evidence - {}", messageToWrite);
				streamWriter.send(notification.getIndex(), messageToWrite);
			}
		} finally{
			if(streamWriter != null){
				streamWriter.close();
			}
		}
		logger.debug("Finished running notification to evidence job at {}, updating timestamp in Mongo", dateStr);
		fetchConfiguration.setLastFetchTime(dateStr);
		fetchConfigurationRepository.save(fetchConfiguration);
		finishStep();
	}

	private JSONObject getSupportingInformation(Notification notification) {
		/*Map<String, String> attributes = notification.getAttributes();
		if (attributes != null && attributes.containsKey("raw_events")) {
			String json = "[" + attributes.get("raw_events") + "]";
			return json.replace("$", "");
		}
		return "";*/
		return notification.hasAttributes() ? new JSONObject(notification.getAttributes()) : new JSONObject();
	}

	private String getAnomalyField(Notification notification) {
		List<String> values = notificationAnomalyMap.get(notification.getCause());
		//TODO - allow for taking more than one of the values as anomaly fields
		if (values != null && values.size() > 0 && notification.getAttributes() != null &&
				notification.getAttributes().containsKey(values.get(0))) {
			return notification.getAttributes().get(values.get(0));
		}
		//default value
		return notification.getCause();
	}

	private List<String> getEntity(Notification notification) {
		List<String> result = new ArrayList<String>();
		if (notification.getCause().toLowerCase().contains("amt")) {
			result.add("amt");
		}
		else if (notification.getCause().toLowerCase().contains("vpn")) {
			result.add("vpn");
		} else {
			result.add("active_directory");
		}
		return result;
	}

	private String getNormalizedUsername(Notification notification) {
		if (notification.getCause().equals(SPECIAL_NOTIFICATION)) {
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
			List<String> usernames = samAccountNameService.getUsersBysAMAccountName(normalizedUsername);
			if (usernames.size() == 1) {
				normalizedUsername = usernames.get(0);
			}
		}
		return normalizedUsername;
	}

	private Map<String, List<String>> createAnomalyMap(String notificationAnomalyString) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		//notificationAnomalyString is of format - notification1:[value1|value2...],notification2:[value1|value2...],...
		for (String pair: notificationAnomalyString.split(",")) {
			String notification = pair.split(":")[0];
			String valuesString = pair.split(":")[1];
			//get list of values inside []
			valuesString = valuesString.substring(1, valuesString.length() - 1);
			List<String> tempList = new ArrayList<String>();
			for (String value: valuesString.split("\\|")) {
				tempList.add(value);
			}
			result.put(notification, tempList);
		}
		return result;
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}