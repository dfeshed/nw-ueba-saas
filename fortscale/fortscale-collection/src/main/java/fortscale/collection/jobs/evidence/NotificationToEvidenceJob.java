package fortscale.collection.jobs.evidence;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.Notification;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.NotificationsRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fetch.FetchConfiguration;
import fortscale.domain.fetch.FetchConfigurationRepository;
import fortscale.services.impl.SamAccountNameService;
import fortscale.services.notifications.VpnGeoHoppingNotificationGenerator;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

	private static final int DAYS_BACK = 1;
	private final String SORT_FIELD = "ts";
	private final String VPN_OVERLAPPING = "VPN_user_creds_share";
	private final String START_DATE = "start_date";
	private final String END_DATE = "end_date";
	private final String MIN_DATE = "minwhen";
	private final String MAX_DATE = "maxwhen";
	private final String DATE_TIME_UNIX = "date_time_unix";

	private final static String LAST_STATE_FIELD = "last_state";

	// job parameters:
	private String notificationsToIgnore;
	private String fetchType;
	private String topicName;
	private String notificationScoreField;
	private String notificationValueField;
	private String normalizedUsernameField;
	private String notificationDataSourceField;
	private String notificationStartTimestampField;
	private String notificationEndTimestampField;
	private String notificationTypeField;
	private String notificationSupportingInformationField;
	private String score;
	private Map<String, List<String>> notificationAnomalyMap;

	private Long startTime;
	private Long endTime;

	@Value("${start.time.param}")
	private String startTimeParam;
	@Value("${end.time.param}")
	private String endTimeParam;

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
		logger.info("Initializing NotificationToEvidence job - getting job parameters");
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		Set<String> keys = map.keySet();
		notificationsToIgnore = jobDataMapExtension.getJobDataMapStringValue(map, "notificationsToIgnore");
		fetchType = jobDataMapExtension.getJobDataMapStringValue(map, "fetchType");
		topicName = jobDataMapExtension.getJobDataMapStringValue(map, "topicName");
		notificationScoreField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationScoreField");
		notificationValueField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationValueField");
		normalizedUsernameField = jobDataMapExtension.getJobDataMapStringValue(map, "normalizedUsernameField");
		notificationDataSourceField = jobDataMapExtension.getJobDataMapStringValue(map, "dataSourceField");
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
		DateFormat sdf = new SimpleDateFormat(jobDataMapExtension.getJobDataMapStringValue(map, "datesFormat"));
		// get parameters values from the job data map
		try {
			if (keys.contains(startTimeParam)) {
				startTime = sdf.parse(jobDataMapExtension.getJobDataMapStringValue(map, startTimeParam)).getTime();
				//convert to seconds
				startTime /= 1000;
			}
			if (keys.contains(endTimeParam)) {
				endTime = sdf.parse(jobDataMapExtension.getJobDataMapStringValue(map, endTimeParam)).getTime();
				//convert to seconds
				endTime /= 1000;
			}
		} catch (ParseException ex) {
			logger.error("Bad date format - {}", ex.getMessage());
			throw new JobExecutionException(ex);
		}
		logger.info("Job initialized");
	}

	@Override
	protected void runSteps() throws Exception {
		logger.info("Running notification to evidence job");
		boolean workWithFetchConfiguration = false;
		String dateStr = null;
		FetchConfiguration fetchConfiguration = null;
		List<Notification> notifications;
		Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, SORT_FIELD));
		if (startTime == null && endTime == null) {
			workWithFetchConfiguration = true;
		}
		if (workWithFetchConfiguration) {
			dateStr = new Date().getTime() + "";
			//get the last runtime from the fetchConfiguration Mongo repository
			fetchConfiguration = fetchConfigurationRepository.findByType(fetchType);
			if (fetchConfiguration == null) {
				//if no last runtime - create a one and save it in the collection
				fetchConfiguration = new FetchConfiguration(fetchType, new Date(0L).getTime() + "");
			}
			long lastFetchTime = Long.parseLong(fetchConfiguration.getLastFetchTime());
			logger.info("Getting notifications after time {}", lastFetchTime);
			//get all notifications that occurred after the last runtime of the job
			notifications = notificationsRepository.findByTsBetweenExcludeComments(lastFetchTime, null, sort);
		} else {
			notifications = notificationsRepository.findByTsBetweenExcludeComments(startTime, endTime, sort);
		}
		if (notifications.size() > 0) {
			logger.info("Found {} notifications, starting to send", notifications.size());
		} else {
			logger.info("No new notifications found");
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
				evidence.put(notificationStartTimestampField, getStartTimeStamp(notification));
				evidence.put(notificationEndTimestampField, getEndTimeStamp(notification));
				evidence.put(notificationTypeField, notification.getCause());
				evidence.put(notificationValueField, getAnomalyField(notification));
				evidence.put(notificationDataSourceField, Collections.singletonList(notification.getDataSource()));
				evidence.put(normalizedUsernameField, getNormalizedUsername(notification));
				evidence.put(notificationSupportingInformationField, getSupportingInformation(notification));

				evidence.put(LAST_STATE_FIELD, this.getClass().getSimpleName());

				String messageToWrite = evidence.toJSONString(JSONStyle.NO_COMPRESS);
				logger.info("Writing to topic evidence - {}", messageToWrite);
				streamWriter.send(notification.getIndex(), messageToWrite);
			}
		} finally{
			if(streamWriter != null){
				streamWriter.close();
			}
		}
		logger.info("Finished running notification to evidence job at {}", new Date());
		if (workWithFetchConfiguration) {
			logger.info("Updating timestamp in Mongo");
			fetchConfiguration.setLastFetchTime(dateStr);
			fetchConfigurationRepository.save(fetchConfiguration);
		}
		finishStep();
	}

	private long getStartTimeStamp(Notification notification) {
		Map<String, String> attributes = notification.getAttributes();
		switch (notification.getCause()) {
			case VPN_OVERLAPPING: {
				return attributes != null && attributes.containsKey(START_DATE) ?
				   Long.parseLong(attributes.get(START_DATE)) : notification.getTs();
			} case VpnGeoHoppingNotificationGenerator.VPN_GEO_HOPPING_CAUSE: {
				return attributes != null && attributes.containsKey(VpnGeoHoppingNotificationGenerator.START_TIME) ?
				   Long.parseLong(attributes.get(VpnGeoHoppingNotificationGenerator.START_TIME)) : notification.getTs();
			} default: return notification.getTs();
		}
	}

	private long getEndTimeStamp(Notification notification) {
		Map<String, String> attributes = notification.getAttributes();
		switch (notification.getCause()) {
			 case VPN_OVERLAPPING: {
				return attributes != null && attributes.containsKey(END_DATE) ?
					Long.parseLong(attributes.get(END_DATE)) : notification.getTs();
			} case VpnGeoHoppingNotificationGenerator.VPN_GEO_HOPPING_CAUSE: {
				return attributes != null && attributes.containsKey(VpnGeoHoppingNotificationGenerator.END_TIME) ?
					Long.parseLong(attributes.get(VpnGeoHoppingNotificationGenerator.END_TIME)) : notification.getTs();
			} default: return notification.getTs();
		}
	}

	private String getSupportingInformation(Notification notification) {
		Map<String, String> attributes = notification.getAttributes();

		if (attributes != null && attributes.containsKey("raw_events")) {
			return  attributes.get("raw_events");

		}
		logger.warn("no raw events - and therefore no supporting information for notification: ", notification.toString());
		return "";
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

	private String getNormalizedUsername(Notification notification) {
		if (notification.getCause().equals(VPN_OVERLAPPING)) {
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