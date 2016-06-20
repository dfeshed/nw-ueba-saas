package fortscale.collection.jobs.notifications;


import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * Created by shays on 14/03/2016.
 */
public abstract class NotificationGeneratorServiceAbstract implements  NotificationGeneratorService {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected static final int WEEK_IN_SECONDS = 604800;
	protected static final int DAY_IN_SECONDS = 86400;
	protected static final String LASTEST_TS = "latest_ts";

	@Autowired
	protected ApplicationConfigurationService applicationConfigurationService;

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
	@Value("${collection.evidence.notification.score}")
	protected double notificationFixedScore;

	protected long latestTimestamp = 0L;
	protected long currentTimestamp = 0L;

    protected abstract List<JSONObject> generateNotificationInternal() throws Exception;
	protected abstract long fetchEarliestEvent() throws InvalidQueryException;

    /**
     * @return boolean, true is completed successfuly, or false if some error took place
     * @throws Exception
     */
    public boolean generateNotification() throws Exception {
        figureLatestRunTime();
        if (latestTimestamp == 0L) {
            //No relevant data. Step out.
            logger.info("No data for nootification creation. Exit");
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
     * @param notifications
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
     * @return
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

    public long getLatestTimestamp() {
        return latestTimestamp;
    }

    public void setLatestTimestamp(long latestTimestamp) {
        this.latestTimestamp = latestTimestamp;
    }

}