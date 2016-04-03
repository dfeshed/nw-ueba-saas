package fortscale.collection.jobs.notifications;


import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.domain.fe.dao.EmptyTableException;
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

    private static final int WEEK_IN_SECONDS = 604800;


    @Value("${collection.evidence.notification.topic}")
    private String evidenceNotificationTopic;

    @Autowired
    protected ApplicationConfigurationService applicationConfigurationService;

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected abstract List<JSONObject>  generateNotificationInternal() throws Exception;

    protected long latestTimestamp = 0L;
    protected long currentTimestamp =0L;


    /**
     *
     * @return boolean, true is completed successfuly, or false if some error took place
     * @throws Exception
     */
    public boolean generateNotification() throws Exception {

        figureLatestRunTime();
        List<JSONObject> notifications = generateNotificationInternal();
        if(CollectionUtils.isNotEmpty(notifications)){
            sendNotificationsToKafka(notifications);
        }

        return true;

        }

    /**
     * Once new notification created, send it to kafka
     * @param notifications
     */
    private void sendNotificationsToKafka(List<JSONObject> notifications) {

        for (JSONObject notification: notifications){
            sendNotificationToKafka(notification);
        }
    }

    private void sendNotificationToKafka(JSONObject credsShare) {
        String messageToWrite = credsShare.toJSONString(JSONStyle.NO_COMPRESS);
        logger.info("Writing to topic evidence - {}", messageToWrite);

        KafkaEventsWriter streamWriter = new KafkaEventsWriter(evidenceNotificationTopic);
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

            long earliestEventTimestamp = fetchEarliesEvent();
            latestTimestamp = Math.min(earliestEventTimestamp, currentTimestamp - WEEK_IN_SECONDS);
            logger.info("latest run time was empty - setting latest timestamp to {}",latestTimestamp);
        }
    }


    protected abstract long fetchEarliesEvent()  throws  InvalidQueryException;


    public long getLatestTimestamp() {
        return latestTimestamp;
    }

    public void setLatestTimestamp(long latestTimestamp) {
        this.latestTimestamp = latestTimestamp;
    }
}
