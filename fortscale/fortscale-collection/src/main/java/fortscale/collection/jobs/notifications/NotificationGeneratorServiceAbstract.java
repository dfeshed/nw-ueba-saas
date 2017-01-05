package fortscale.collection.jobs.notifications;

import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.common.dataqueries.querydto.DataQueryHelper;
import fortscale.common.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.common.dataqueries.querygenerators.mysqlgenerator.MySqlQueryRunner;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.impl.ApplicationConfigurationHelper;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class NotificationGeneratorServiceAbstract implements NotificationGeneratorService {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected static final String LATEST_TS = "latest_ts";
	protected static final String TS_PARAM = "latestTimestamp";
	protected static final long DAY_IN_SECONDS = TimeUnit.DAYS.toSeconds(1);
	protected static final long MINIMAL_PROCESSING_PERIOD_IN_SEC = TimeUnit.MINUTES.toSeconds(10);
	private static final DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	@Autowired
	protected ApplicationConfigurationService applicationConfigurationService;
	@Autowired
	protected ApplicationConfigurationHelper applicationConfigurationHelper;
	@Autowired
	protected DataQueryHelper dataQueryHelper;
	@Autowired
	protected MySqlQueryRunner queryRunner;
	@Autowired
	protected DataQueryRunnerFactory dataQueryRunnerFactory;
	@Autowired
	protected DataEntitiesConfig dataEntitiesConfig;

	@Value("${collection.evidence.notification.topic}")
	private String evidenceNotificationTopic;
	@Value("${collection.evidence.notification.score.field}")
	protected String notificationScoreField;
	@Value("${collection.evidence.notification.value.field}")
	protected String notificationValueField;
	@Value("${collection.evidence.notification.normalizedusername.field}")
	protected String normalizedUsernameField;
	@Value("${collection.evidence.notification.datasource.field}")
	protected String notificationDataSourceField;
	@Value("${collection.evidence.notification.starttimestamp.field}")
	protected String notificationStartTimestampField;
	@Value("${collection.evidence.notification.endtimestamp.field}")
	protected String notificationEndTimestampField;
	@Value("${collection.evidence.notification.type.field}")
	protected String notificationTypeField;
	@Value("${collection.evidence.notification.supportinginformation.field}")
	protected String notificationSupportingInformationField;
	@Value("${collection.evidence.notification.numofevents.field}")
	protected String notificationNumOfEventsField;
	@Value("${collection.evidence.notification.score}")
	protected double notificationFixedScore;

	protected long latestTimestamp = 0L;
	protected long currentTimestamp = 0L;
	protected String dataEntity;

	protected abstract List<JSONObject> generateNotificationInternal() throws Exception;
	protected abstract long fetchEarliestEvent() throws InvalidQueryException;

	/**
	 * @return boolean, true is completed successfully, or false if some error took place
	 * @throws Exception
	 */
	public boolean generateNotification() throws Exception {
		figureLatestRunTime();
		if (latestTimestamp == 0L) {
			//No relevant data. Step out.
			logger.info("No data for notification creation. Exit");
			return true;
		}
		List<JSONObject> notifications = generateNotificationInternal();
		if (CollectionUtils.isNotEmpty(notifications)) {
			sendNotificationsToKafka(notifications);
		}
		return true;
	}

	/**
	 * Once new notification created, send it to kafka
	 */
	private void sendNotificationsToKafka(List<JSONObject> notifications) {
		logger.info("Create writer to topic evidence ");
		KafkaEventsWriter streamWriter = new KafkaEventsWriter(evidenceNotificationTopic);
		for (JSONObject notification : notifications) {
			sendNotificationToKafka(notification, streamWriter);
		}
		logger.info("Close writer to topic evidence ");
		streamWriter.close();
	}

	private void sendNotificationToKafka(JSONObject credsShare, KafkaEventsWriter streamWriter) {
		String messageToWrite = credsShare.toJSONString(JSONStyle.NO_COMPRESS);
		logger.info("Writing to topic evidence - {}", messageToWrite);
		streamWriter.send("VPN_user_creds_share", messageToWrite);
	}

	/**
	 * check if there is new data to process, newest data then last execution
	 *
	 * @throws InvalidQueryException
	 */
	protected void figureLatestRunTime() throws InvalidQueryException {
		//read latestTimestamp from mongo collection application_configuration
		currentTimestamp = TimestampUtils.convertToSeconds(System.currentTimeMillis());
		if (latestTimestamp == 0L) {
			//create query to find the earliest event
			latestTimestamp = fetchEarliestEvent();
			logger.info("Latest run time was empty. Latest timestamp was set to {}.", latestTimestamp);
		}
	}

	protected void initConfigurationFromApplicationConfiguration(String configurationPrefix, List<Pair<String, String>> list)
			throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

		List<Pair<String, String>> parameters = new ArrayList<>();
		parameters.add(new ImmutablePair<>("notificationScoreField", "notificationScoreField"));
		parameters.add(new ImmutablePair<>("notificationTypeField", "notificationTypeField"));
		parameters.add(new ImmutablePair<>("notificationValueField", "notificationValueField"));
		parameters.add(new ImmutablePair<>("notificationStartTimestampField", "notificationStartTimestampField"));
		parameters.add(new ImmutablePair<>("normalizedUsernameField", "normalizedUsernameField"));
		parameters.add(new ImmutablePair<>("notificationSupportingInformationField", "notificationSupportingInformationField"));
		parameters.add(new ImmutablePair<>("notificationDataSourceField", "notificationDataSourceField"));
		parameters.add(new ImmutablePair<>("notificationFixedScore", "notificationFixedScore"));
		parameters.addAll(list);
		applicationConfigurationHelper.syncWithConfiguration(configurationPrefix, this, parameters);
	}

	protected JSONObject createNotification(long startTime, long endTime, String normalizedUsername, String notificationType, String notificationValue) {
		JSONObject notification = new JSONObject();
		notification.put(notificationScoreField, notificationFixedScore);
		notification.put(notificationStartTimestampField, startTime);
		notification.put(notificationEndTimestampField, endTime);
		notification.put(notificationTypeField, notificationType);
		notification.put(notificationValueField, notificationValue);
		notification.put(normalizedUsernameField, normalizedUsername);
		List<String> entities = new ArrayList<>();
		entities.add(dataEntity);
		notification.put(notificationDataSourceField, entities);
		return notification;
	}

	protected String getStringValueFromEvent(Map<String, Object> impalaEvent, String field) {
		if (impalaEvent.containsKey(field)) {
			return impalaEvent.get(field).toString();
		}
		return "";
	}

	protected int getIntegerValueFromEvent(Map<String, Object> impalaEvent, String field) {
		if (impalaEvent.containsKey(field)) {
			return Integer.parseInt(impalaEvent.get(field).toString());
		}
		return 0;
	}

	protected long getLongValueFromEvent(Map<String, Object> impalaEvent, String field) {
		if (impalaEvent.containsKey(field)) {
			return Long.parseLong(impalaEvent.get(field).toString());
		}
		return 0L;
	}

	/**
	 * Create and retrieve the following SQL condition phrase:
	 * "[partition field name] between [partition of lowerLimitIncluding] and [partition of upperLimitIncluding]
	 * and [epochtime field name] between [epochtime of lowerLimitIncluding] and [epochtime of upperLimitIncluding]".
	 * The partition and epochtime field names are deduced from tableName, and the method calculates the limit
	 * partitions according to lowerLimitIncluding and upperLimitIncluding. The idea is to add a condition on the
	 * epochtime field, but only query the relevant partitions for good performance.
	 * ================================================================================================================
	 * TODO: The method assumes that the partition is "yearmonthday", and that the epochtime field is "date_time_unix".
	 * ================================================================================================================
	 *
	 * @param tableName           the name of the table that should be queried
	 * @param lowerLimitIncluding an {@link Instant} of the lower limit (epochtime should be gte to it)
	 * @param upperLimitIncluding an {@link Instant} of the upper limit (epochtime should be lte to it)
	 * @return the corresponding SQL condition phrase
	 */
	protected String getEpochtimeBetweenCondition(
			String tableName, Instant lowerLimitIncluding, Instant upperLimitIncluding) {

		// Note that the formatter is specific to the "yearmonthday" partition
		return String.format("yearmonthday between %s and %s and date_time_unix between %d and %d",
				yearMonthDayFormatter.format(lowerLimitIncluding), yearMonthDayFormatter.format(upperLimitIncluding),
				lowerLimitIncluding.getEpochSecond(), upperLimitIncluding.getEpochSecond());
	}

	/**
	 * Create and retrieve the following SQL condition phrase:
	 * "[partition field name] >= [partition of lowerLimitIncluding]
	 * and [epochtime field name] >= [epochtime of lowerLimitIncluding]".
	 * The partition and epochtime field names are deduced from tableName, and the method calculates the lower limit
	 * partition according to lowerLimitIncluding. The idea is to add a condition on the epochtime field, but only
	 * query the relevant partitions for good performance.
	 * ================================================================================================================
	 * TODO: The method assumes that the partition is "yearmonthday", and that the epochtime field is "date_time_unix".
	 * ================================================================================================================
	 *
	 * @param tableName           the name of the table that should be queried
	 * @param lowerLimitIncluding an {@link Instant} of the lower limit (epochtime should be gte to it)
	 * @return the corresponding SQL condition phrase
	 */
	protected String getEpochtimeGteCondition(String tableName, Instant lowerLimitIncluding) {
		// Note that the formatter is specific to the "yearmonthday" partition
		return String.format("yearmonthday >= %s and date_time_unix >= %d",
				yearMonthDayFormatter.format(lowerLimitIncluding), lowerLimitIncluding.getEpochSecond());
	}

	public String getNormalizedUsernameField() {
		return normalizedUsernameField;
	}

	public void setNormalizedUsernameField(String normalizedUsernameField) {
		this.normalizedUsernameField = normalizedUsernameField;
	}

	public void setDataEntity(String dataEntity) {
		this.dataEntity = dataEntity;
	}

	public String getDataEntity() {
		return dataEntity;
	}

	/**
	 * Unused getters and setters.
	 */

	@SuppressWarnings("unused")
	public long getLatestTimestamp() {
		return latestTimestamp;
	}

	@SuppressWarnings("unused")
	public void setLatestTimestamp(long latestTimestamp) {
		this.latestTimestamp = latestTimestamp;
	}

	@SuppressWarnings("unused")
	public String getNotificationScoreField() {
		return notificationScoreField;
	}

	@SuppressWarnings("unused")
	public void setNotificationScoreField(String notificationScoreField) {
		this.notificationScoreField = notificationScoreField;
	}

	@SuppressWarnings("unused")
	public String getNotificationValueField() {
		return notificationValueField;
	}

	@SuppressWarnings("unused")
	public void setNotificationValueField(String notificationValueField) {
		this.notificationValueField = notificationValueField;
	}

	@SuppressWarnings("unused")
	public String getNotificationDataSourceField() {
		return notificationDataSourceField;
	}

	@SuppressWarnings("unused")
	public void setNotificationDataSourceField(String notificationDataSourceField) {
		this.notificationDataSourceField = notificationDataSourceField;
	}

	@SuppressWarnings("unused")
	public String getNotificationStartTimestampField() {
		return notificationStartTimestampField;
	}

	@SuppressWarnings("unused")
	public void setNotificationStartTimestampField(String notificationStartTimestampField) {
		this.notificationStartTimestampField = notificationStartTimestampField;
	}

	@SuppressWarnings("unused")
	public String getNotificationEndTimestampField() {
		return notificationEndTimestampField;
	}

	@SuppressWarnings("unused")
	public void setNotificationEndTimestampField(String notificationEndTimestampField) {
		this.notificationEndTimestampField = notificationEndTimestampField;
	}

	@SuppressWarnings("unused")
	public String getNotificationTypeField() {
		return notificationTypeField;
	}

	@SuppressWarnings("unused")
	public void setNotificationTypeField(String notificationTypeField) {
		this.notificationTypeField = notificationTypeField;
	}

	@SuppressWarnings("unused")
	public String getNotificationSupportingInformationField() {
		return notificationSupportingInformationField;
	}

	@SuppressWarnings("unused")
	public void setNotificationSupportingInformationField(String notificationSupportingInformationField) {
		this.notificationSupportingInformationField = notificationSupportingInformationField;
	}

	@SuppressWarnings("unused")
	public void setNotificationFixedScore(double notificationFixedScore) {
		this.notificationFixedScore = notificationFixedScore;
	}

	@SuppressWarnings("unused")
	public double getNotificationFixedScore() {
		return notificationFixedScore;
	}
}
