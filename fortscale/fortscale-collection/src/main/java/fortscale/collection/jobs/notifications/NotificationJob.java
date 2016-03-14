package fortscale.collection.jobs.notifications;

import fortscale.collection.BatchScheduler;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.common.dataqueries.querydto.*;
import fortscale.common.dataqueries.querygenerators.DataQueryRunner;
import fortscale.common.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.common.dataqueries.querygenerators.mysqlgenerator.MySqlQueryRunner;
import fortscale.domain.core.VpnSessionOverlap;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Creds share notification does the following:
 * It queries the relevant data source for multiple concurrent session of the same user from different hostnames.
 * if more than X concurrent sessions have interception with the same session S1, then a notification would be created,
 * and its anomaly value would be the session S1.
 * In the supporting information field, the raw events would be written - those are the session S2, S3 ,... Sx that
 * intercepted with session S1.
 *
 * example:
 * S1: a vpn session from 14:00 till 23:00
 * S2: a vpn session from 14:05 till 15:00
 * S3: a vpn session from 15:05 till 16:00
 * S4: a vpn session from 15:10 till 16:10
 * S5: a vpn session from 20:00 till 22:00
 * X: 4 (number of concurrent sessions needed to create creds share notification)
 *
 * then a creds share notification would be created, and its anomaly value would be S1
 *
 * Created by galiar on 01/03/2016.
 */
public class NotificationJob extends FortscaleJob {

    private static Logger logger = LoggerFactory.getLogger(NotificationJob.class);
  /*  private static final String LASTEST_TS = "creds_share_notification.latest_ts";
    private static final String MIN_DATE_TIME_FIELD = "min_ts";

    private static final int WEEK_IN_SECONDS = 604800;
    private static final int DAY_IN_SECONDS = 86400;*/
    @Autowired
    ApplicationConfigurationService applicationConfigurationService;



    private List<NotificationGeneratorService> generatorServices = new ArrayList<>();
    private String sourceName;
    private String jobName;


    @Override
    protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {



        JobDataMap map = context.getMergedJobDataMap();


        //Fetch the relevant service generators which should be executed in this execution time
        //ClassPathXmlApplicationContext springContext = (ClassPathXmlApplicationContext)context.get(BatchScheduler.SPRING_CONTEXT_VAR);
        ApplicationContext springContext = jobDataMapExtension.getSpringApplicationContext();
        List<String> notificationGeneratorsBeanNames = jobDataMapExtension.getJobDataMapListOfStringsValue(map,"notificationsServiceList",",");
        for (String notificationGeneratorsBeanName : notificationGeneratorsBeanNames){
            NotificationGeneratorService notificationGeneratorService = springContext.getBean(notificationGeneratorsBeanName,NotificationGeneratorService.class);
            generatorServices.add(notificationGeneratorService);
        }
        sourceName = context.getJobDetail().getKey().getGroup();
        jobName = context.getJobDetail().getKey().getName();

    }

    @Override
    protected int getTotalNumOfSteps() {
        //1. get the last run time. 2.  creds share query. 3. supporting information query . 4. send to kafka
        return 4;
    }

    @Override
    protected boolean shouldReportDataReceived() {
        return true;
    }


    /*
    fetch the last time this job has run
    query the creds share notification out of the relevant hdfs table (currently supporting only vpn_session)
    query for additional information - all the raw events
    send the notification to evidence creation task
    */
    @Override
    protected void runSteps() throws Exception {
        for (NotificationGeneratorService notificationGenerator : this.generatorServices){
            notificationGenerator.generateNotification();
        }

    }

//    private void sendCredsShareNotificationsToKafka(List<JSONObject> credsShareNotifications) {
//
//        for (JSONObject credsShare: credsShareNotifications){
//            sendCredsShareNotificationToKafka(credsShare);
//        }
//    }
//
//    private void sendCredsShareNotificationToKafka(JSONObject credsShare) {
//        String messageToWrite = credsShare.toJSONString(JSONStyle.NO_COMPRESS);
//        logger.info("Writing to topic evidence - {}", messageToWrite);
//
//        KafkaEventsWriter streamWriter = new KafkaEventsWriter(evidenceNotificationTopic);
//        streamWriter.send("VPN_user_creds_share", messageToWrite);
//
//    }
//
//    private List<JSONObject> addRawEventsToCredsShare( List<JSONObject> credsShareNotifications ) {
//
//        for (JSONObject credsShare: credsShareNotifications){
//            addRawEvents(credsShare);
//        }
//        return credsShareNotifications;
//    }


}