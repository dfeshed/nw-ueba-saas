package fortscale.collection.jobs.notifications;

import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.common.dataqueries.querydto.DataQueryDTO;
import fortscale.common.dataqueries.querydto.DataQueryField;
import fortscale.common.dataqueries.querydto.DataQueryHelper;
import fortscale.common.dataqueries.querygenerators.DataQueryRunner;
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
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.min;
import static java.time.Instant.ofEpochSecond;

public abstract class NotificationGeneratorServiceAbstract implements NotificationGeneratorService {
	private static final String END_TIME_FIELD_NAME = "end_time";
	private static final String MIN_TS_FIELD_ALIAS = "min_ts";
	private static final String MAX_TS_FIELD_ALIAS = "max_ts";
	private static final String NEXT_EPOCHTIME_KEY = "next_epochtime";
	private static final String NEXT_EPOCHTIME_VALUE = "nextEpochtime";
	private static final DateTimeFormatter yearMonthDayFormatter =
			DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.of("Z"));
	private static final long MIN_PROCESSING_PERIOD_IN_SEC = TimeUnit.MINUTES.toSeconds(10); // 10 minutes
	private static final long MAX_UPPER_LIMIT_INC_DELTA = TimeUnit.DAYS.toSeconds(1) - 1; // 23:59:59

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
	@Value("${collection.evidence.notification.topic}")
	private String evidenceNotificationTopic;

	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected String dataEntity;
	private long nextEpochtime = 0;

	/**
	 * @return true if completed successfully, false if an error occurred
	 * @throws Exception
	 */
	public boolean generateNotification() throws Exception {
		if (nextEpochtime == 0) {
			nextEpochtime = getEarliestEpochtime();
			logger.info("Epochtime of next event to process was 0, so it was set to the earliest epochtime ({}).", nextEpochtime);
		}

		if (nextEpochtime == 0) {
			logger.info("No data to create notifications. Exiting.");
			return true;
		}

		long lastEpochtime = getLatestEpochtime();
		logger.info("{} is going to generate notifications. Next time {} ({}), last time {} ({}).", getClass().getSimpleName(),
				ofEpochSecond(nextEpochtime), nextEpochtime, ofEpochSecond(lastEpochtime), lastEpochtime);

		while (nextEpochtime <= lastEpochtime - MIN_PROCESSING_PERIOD_IN_SEC) {
			Instant lowerLimitInc = ofEpochSecond(nextEpochtime);
			Instant upperLimitInc = ofEpochSecond(min(nextEpochtime + MAX_UPPER_LIMIT_INC_DELTA, lastEpochtime));
			logger.info("{} is going to process events from {} ({}) to {} ({}).", getClass().getSimpleName(),
					lowerLimitInc, lowerLimitInc.getEpochSecond(), upperLimitInc, upperLimitInc.getEpochSecond());
			List<JSONObject> notifications = generateNotificationsInternal(lowerLimitInc, upperLimitInc);
			if (CollectionUtils.isNotEmpty(notifications)) sendNotificationsToKafka(notifications);
			nextEpochtime = upperLimitInc.getEpochSecond() + 1;
			Map<String, String> updateNextTimestamp = new HashMap<>();
			updateNextTimestamp.put(getAppConfPrefix() + "." + NEXT_EPOCHTIME_KEY, String.valueOf(nextEpochtime));
			applicationConfigurationService.updateConfigItems(updateNextTimestamp);
		}

		return true;
	}

	private Long getEpochtime(DataQueryDTO dataQueryDto, String key) throws InvalidQueryException {
		DataQueryRunner dataQueryRunner = dataQueryRunnerFactory.getDataQueryRunner(dataQueryDto);
		String query = dataQueryRunner.generateQuery(dataQueryDto);
		logger.info("Running the query {}.", query);
		List<Map<String, Object>> queryResults = dataQueryRunner.executeQuery(query);

		if (CollectionUtils.isEmpty(queryResults)) {
			logger.info("The table is empty. Quitting.");
			return null;
		}

		for (Map<String, Object> queryResult : queryResults) {
			Object timestamp = queryResult.get(key);
			if (timestamp instanceof Timestamp) return TimestampUtils.convertToSeconds((Timestamp)timestamp);
		}

		return null;
	}

	private long getEarliestEpochtime() throws InvalidQueryException {
		DataQueryField minFieldFunc = dataQueryHelper.createMinFieldFunc(END_TIME_FIELD_NAME, MIN_TS_FIELD_ALIAS);
		DataQueryDTO dataQueryDto = dataQueryHelper.createDataQuery(dataEntity);
		dataQueryHelper.setFuncFieldToQuery(minFieldFunc, dataQueryDto);
		Long earliest = getEpochtime(dataQueryDto, MIN_TS_FIELD_ALIAS);
		return earliest == null ? 0 : earliest;
	}

	private long getLatestEpochtime() throws InvalidQueryException {
		DataQueryField maxFieldFunc = dataQueryHelper.createMaxFieldFunc(END_TIME_FIELD_NAME, MAX_TS_FIELD_ALIAS);
		DataQueryDTO dataQueryDto = dataQueryHelper.createDataQuery(dataEntity);
		dataQueryHelper.setFuncFieldToQuery(maxFieldFunc, dataQueryDto);
		Long latest = getEpochtime(dataQueryDto, MAX_TS_FIELD_ALIAS);
		return latest == null ? 0 : latest;
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
		parameters.add(new ImmutablePair<>(NEXT_EPOCHTIME_KEY, NEXT_EPOCHTIME_VALUE));
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

	protected abstract List<JSONObject> generateNotificationsInternal(Instant lowerLimitInc, Instant upperLimitInc);

	protected abstract String getAppConfPrefix();

	protected void doneGeneratingNotifications(int numOfEvents, int numOfNotifications) {
		logger.info("{} finished generating notifications. {} events, {} notifications, next process time {} ({}).",
				getClass().getSimpleName(), numOfEvents, numOfNotifications,
				ofEpochSecond(nextEpochtime), nextEpochtime);
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
	public long getNextEpochtime() {
		return nextEpochtime;
	}

	@SuppressWarnings("unused")
	public void setNextEpochtime(long nextEpochtime) {
		this.nextEpochtime = nextEpochtime;
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
