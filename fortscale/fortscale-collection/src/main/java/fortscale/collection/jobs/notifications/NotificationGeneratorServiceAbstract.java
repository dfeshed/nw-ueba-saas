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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class NotificationGeneratorServiceAbstract implements  NotificationGeneratorService {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected static final int WEEK_IN_SECONDS = 604800;
	protected static final int DAY_IN_SECONDS = 86400;
	protected static final String LATEST_TS = "latest_ts";
	protected static final String TS_PARAM = "latestTimestamp";
	protected static final DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	protected static final long MINIMAL_PROCESSING_PERIOD_IN_SEC = TimeUnit.MINUTES.toSeconds(10);

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
        for (JSONObject notification: notifications){
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
     * @throws InvalidQueryException
     */
    protected void figureLatestRunTime() throws InvalidQueryException {
        //read latestTimestamp from mongo collection application_configuration
        currentTimestamp = TimestampUtils.convertToSeconds(System.currentTimeMillis());
        if (latestTimestamp == 0L) {
            //create query to find the earliest event
            long earliestEventTimestamp = fetchEarliestEvent();
            latestTimestamp = Math.min(earliestEventTimestamp, currentTimestamp - WEEK_IN_SECONDS);
            logger.info("latest run time was empty - setting latest timestamp to {}",latestTimestamp);
        }
    }

	protected void initConfigurationFromApplicationConfiguration(String configurationPrefix,
                                                                 List<Pair<String, String>> list)
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

	protected JSONObject createNotification(long startTime, long endTime, String normalizedUsername,
			String notificationType, String notificationValue) {
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

	protected String getStringValueFromEvent(Map<String, Object> impalaEvent,String field) {
		if (impalaEvent.containsKey(field)) {
			return impalaEvent.get(field).toString();
		}
		return "";
	}

	protected int getIntegerValueFromEvent(Map<String, Object> impalaEvent,String field) {
		if (impalaEvent.containsKey(field)) {
			return Integer.parseInt(impalaEvent.get(field).toString());
		}
		return 0;
	}

	protected long getLongValueFromEvent(Map<String, Object> impalaEvent,String field) {
		if (impalaEvent.containsKey(field)) {
			return Long.parseLong(impalaEvent.get(field).toString());
		}
		return 0L;
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

	/** Unused getters and setters. */
	
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