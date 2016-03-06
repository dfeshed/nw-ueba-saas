package fortscale.collection.jobs.notifications;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.common.dataqueries.querydto.*;
import fortscale.common.dataqueries.querygenerators.DataQueryRunner;
import fortscale.common.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.common.dataqueries.querygenerators.mysqlgenerator.MySqlQueryRunner;
import fortscale.domain.events.VpnSession;
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
import org.springframework.util.StringUtils;

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
public class VpnCredsShareNotificationJob extends FortscaleJob {

    private static Logger logger = LoggerFactory.getLogger(VpnCredsShareNotificationJob.class);
    private static final String LASTEST_TS = "creds_share_notification_latest_ts";
    private static final String MIN_DATE_TIME_FIELD = "min_ts";

    private static final int WEEK_IN_SECONDS = 604800;
    private static final int DAY_IN_SECONDS = 86400;
    @Autowired
    ApplicationConfigurationService applicationConfigurationService;

    @Autowired
    DataQueryHelper dataQueryHelper;
    @Autowired
    protected MySqlQueryRunner queryRunner;

    @Autowired
    private DataQueryRunnerFactory dataQueryRunnerFactory;

    @Autowired
    private HostnameManipulatorFactory hostnameManipulatorFactory;

    @Value("${collection.evidence.notification.topic}")
    private String evidenceNotificationTopic;

    String hostnameField;
    String hostnameManipulateFunc;
    List<String> hostnameDomainMarkers;

    String tableName;
    long latestTimestamp;
    long currentTimestamp;

    int numberOfConcurrentSessions;

    // fields for creating the notification
    private String notificationScoreField;
    private String notificationValueField;
    private String normalizedUsernameField;
    private String notificationDataSourceField;
    private String notificationStartTimestampField;
    private String notificationEndTimestampField;
    private String notificationTypeField;
    private String notificationSupportingInformationField;
    private String notificationFixedScore;

    String sourceName;
    String jobName;

    @Override
    protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {

        // params: table names, field names: field names for the select phrase and also for the conditions phrase,
        // hostname manipulation condition, number of concurrent session. those parameters should be saved in mongo.

        //get table name

        //host name condition:
        //host name field, function to normalize it - get it from some factory

        JobDataMap map = context.getMergedJobDataMap();
        hostnameField = jobDataMapExtension.getJobDataMapStringValue(map,"hostnameField");
        hostnameManipulateFunc =  jobDataMapExtension.getJobDataMapStringValue(map,"hostnameManipulatorFunc");
        hostnameDomainMarkers =  jobDataMapExtension.getJobDataMapListOfStringsValue(map,"hostnameDomainMarkers",",");

        numberOfConcurrentSessions = jobDataMapExtension.getJobDataMapIntValue(map,"numberOfConcurrentSessions");

        notificationScoreField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationScoreField");
        notificationValueField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationValueField");
        normalizedUsernameField = jobDataMapExtension.getJobDataMapStringValue(map, "normalizedUsernameField");
        notificationDataSourceField = jobDataMapExtension.getJobDataMapStringValue(map, "dataSourceField");
        notificationStartTimestampField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationStartTimestampField");
        notificationEndTimestampField = jobDataMapExtension.getJobDataMapStringValue(map,"notificationEndTimestampField");
        notificationTypeField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationTypeField");
        notificationSupportingInformationField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationSupportingInformationField");
        notificationFixedScore = jobDataMapExtension.getJobDataMapStringValue(map, "notificationTypeField");
        notificationSupportingInformationField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationScore"); //TODO notification shouldn't have score at all


        // get the job group name to be used using monitoring
        sourceName = context.getJobDetail().getKey().getGroup();
        jobName = context.getJobDetail().getKey().getName();


    }

    @Override
    protected int getTotalNumOfSteps() {
        //1. get the last run time. 2. query. 3. query supporting information. 4. send to kafka
        return 4;
    }

    @Override
    protected boolean shouldReportDataReceived() {
        return false; //TODO
    }


    //fetch the last time this job has run
    //query the creds share notification out of the relevant hdfs table (currently supporting only vpn_session)
    //query for additional information - all the raw events
    //send the notification to evidence creation task
    @Override
    protected void runSteps() throws Exception {

        logger.info("{} {} job started", jobName, sourceName);

        startNewStep("Get the latest run time");
         boolean tableHasData = figureLatestRunTime();
        if(!tableHasData){
            return;
        }

        finishStep();

            startNewStep("Query impala for creds share notifications");

        while(latestTimestamp <= currentTimestamp){

         //one day a time
         long upperLimit = latestTimestamp + DAY_IN_SECONDS;

         //create ConditionTerm for the hostname condition
            HostnameManipulator hostnameManipulator = hostnameManipulatorFactory.getHostnameManilpulator(hostnameManipulateFunc);
            String hostnameCondition = hostnameManipulator.getManipulatedHostname(hostnameField,hostnameDomainMarkers);


            //create dataQuery for the overlapping sessions - use impalaJDBC and not dataQuery mechanism since
            // some features of the query aren't supported in dataQuery: e.g. CASE WHEN , or SQL functions: lpad, instr
            String query = "select" +
                    " username ,normalized_username,id, "+ hostnameField+", count(*) as sessions_count ,min(start_session_time) as start_time ,max(end_session_time) as end_time" +
                    " from" +
                    " (select t1.username,t1.normalized_username,t1.hostname, u.id, unix_timestamp(seconds_sub(t1.date_time, t1.duration)) as start_session_time ,unix_timestamp(t1.date_time)" +
                    " as end_session_time  " +
                    "from "+tableName+" t1 inner join "+tableName+" t2 " +
                    "on t1.username = t2.username and t1.source_ip!=t2.source_ip  and  seconds_sub(t2.date_time,t2.duration) between seconds_sub(t1.date_time,t1.duration) and t1.date_time  " +
                    "inner join users u on t1.normalized_username = u.username where t1.source_ip !='' and t2.source_ip !='' and t1.country = 'Reserved Range' and t2.country='Reserved Range'" +
                    " and "+hostnameCondition+" and t1.date_time_unix >= "+latestTimestamp+" and t1.date_time_unix < "+upperLimit+" " +
                    "group by t1.username,t1.normalized_username,t1."+hostnameField+",t1.source_ip ,seconds_sub(t1.date_time, t1.duration) ,t1.date_time,u.id " +
                    "having count(t2.source_ip) >= "+numberOfConcurrentSessions+"  )" +
                    " t group by username,normalized_username,"+hostnameField+",id";

         //run the query
            List<Map<String, Object>> results= queryRunner.executeQuery(query);

            //if we found something, get the raw data.

            if(results.isEmpty()) {
                logger.info( String.format("no creds share notification were found between dates: {} and {}"),latestTimestamp,upperLimit);
                continue;
            }

            startNewStep( String.format("found {} creds share notifications. fetching the supporting information for each.",results.size()));

                for (Map<String, Object> event : results){ // each map is a single event, each pair is column and value
                    String username = "";
                    String startTime ="";
                    String endTime ="";
                    String sessionsCount ="";
                    String normalizedUsername ="";
                    String userID ="";


                    for(Map.Entry<String,Object> entry : event.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue().toString();

                        switch (key){
                            case "username": {
                             username = value;
                                break;
                            }
                            case "start_time":{
                                startTime = value;
                                break;
                            }
                            case "end_time":{
                                endTime = value;
                                break;
                            }
                            case "sessions_count":{
                                sessionsCount = value;
                                break;
                            }
                            case "normalized_username":{
                                normalizedUsername = value;
                                break;
                            }
                            case "id":{
                                userID = value;
                                break;
                            }

                            default: break;
                        }
                    }

                    startNewStep("Query impala for supporting information - raw events");
                    //"select * from vpnsessiondatares where username='#{username}' and date_time_unix>=#{start_time} and date_time_unix<=#{end_time}"
                    List<Term> conditions = new ArrayList<>();
                    conditions.add(createDataQueryConditions("username",username));
                    conditions.add(getDateRangeTerm(Long.parseLong(startTime),Long.parseLong(endTime)));

                    DataQueryDTO dataQueryDTO = dataQueryHelper.createDataQuery("vpn_session", "", conditions, null, -1, DataQueryDTOImpl.class);
                    DataQueryRunner dataQueryRunner = dataQueryRunnerFactory.getDataQueryRunner(dataQueryDTO);
                    String rawEventsQuery = dataQueryRunner.generateQuery(dataQueryDTO);
                    logger.info("Running the query: {}", rawEventsQuery);
                    // execute Query
                    List<Map<String, Object>> queryList = dataQueryRunner.executeQuery(rawEventsQuery);
                    //extract the supporting information

                    List<VpnSession> rawEvents = new ArrayList<>();
                    for (Map<String, Object> rawEvent : queryList) { // each map is a single event, each pair is column and value
                        rawEvents.add(createVpnSessionFromImpalaRow(rawEvent));
                    }

                    //create new notification / evidence to send to topic

                    //TODO delete the parallel code from notification to evidence job!
                    //convert each notification to evidence and send it to the appropriate Kafka topic
                    JSONObject evidence = new JSONObject();
                    evidence.put(notificationScoreField, notificationFixedScore);
                    evidence.put(notificationStartTimestampField, startTime);
                    evidence.put(notificationEndTimestampField, endTime);
                    evidence.put(notificationTypeField, "VPN_user_creds_share");
                    evidence.put(notificationValueField, sessionsCount);
                    List<String> entities = new ArrayList();
                    entities.add("vpn_session");
                    evidence.put(notificationDataSourceField, entities);
                    evidence.put(normalizedUsernameField, normalizedUsername);
                    evidence.put(notificationSupportingInformationField, rawEvents);


                    String messageToWrite = evidence.toJSONString(JSONStyle.NO_COMPRESS);
                    logger.info("Writing to topic evidence - {}", messageToWrite);

                    KafkaEventsWriter streamWriter = new KafkaEventsWriter(evidenceNotificationTopic);
                    streamWriter.send("VPN_user_creds_share", messageToWrite);

                    startNewStep("Sends the results to evidence creation task");
                    //do stuff
                    finishStep();
            }

            latestTimestamp = upperLimit;
        }

            finishStep();

         //save current timestamp in mongo application_configuration
        applicationConfigurationService.insertConfigItem(LASTEST_TS,String.valueOf(currentTimestamp));

            logger.info("{} {} job finished", jobName, sourceName);

        }


    private boolean figureLatestRunTime() throws InvalidQueryException {
        //read latestTimestamp from mongo collection application_configuration
        latestTimestamp = Long.parseLong(applicationConfigurationService.getApplicationConfigurationByKey(LASTEST_TS).getValue());
        if (StringUtils.isEmpty(latestTimestamp)) {

            //create query to find the earliest event
            DataQueryDTO dataQueryDTO = dataQueryHelper.createDataQuery("vpn_session", "", null, null, -1, DataQueryDTOImpl.class);
            DataQueryField countField = dataQueryHelper.createMinFunc("date_time", MIN_DATE_TIME_FIELD);
            dataQueryHelper.setFuncFieldToQuery(countField, dataQueryDTO);
            DataQueryRunner dataQueryRunner = dataQueryRunnerFactory.getDataQueryRunner(dataQueryDTO);
            String query = dataQueryRunner.generateQuery(dataQueryDTO);
            logger.info("Running the query: {}", query);
            // execute Query
            List<Map<String, Object>> queryList = dataQueryRunner.executeQuery(query);
            if (queryList.isEmpty()) {
                //no data in table
                logger.info("Table is empty. Quit...");
                return false;
            }

            long earliestEventTimestamp = extractEarliestEventFromDataQueryResult(queryList);
            currentTimestamp = System.currentTimeMillis();
            latestTimestamp = Math.min(earliestEventTimestamp, currentTimestamp - WEEK_IN_SECONDS);
            logger.info("latest run time was empty - setting latest timestamp to {}",latestTimestamp);
        }
        return true;
    }

    private long extractEarliestEventFromDataQueryResult(List<Map<String, Object>> queryList) {
        for(Map<String, Object>  resultPair: queryList){
            if(resultPair.get(MIN_DATE_TIME_FIELD) != null){
                return Long.parseLong(resultPair.get(MIN_DATE_TIME_FIELD).toString());
            }
        }

        return 0;
    }

    private Term getDateRangeTerm(long startTime, long endTime) { //TODO from supporting information histogram by single events populator
        return dataQueryHelper.createDateRangeTerm("vpn_session", TimestampUtils.convertToSeconds(startTime), TimestampUtils.convertToSeconds(endTime));
    }

    private Term createDataQueryConditions(String dataEntityField,String value){ //TODO from forward events

        ConditionField term = new ConditionField();
        DataQueryField dataQueryField = new DataQueryField();
        dataQueryField.setId(dataEntityField);
        term.setField(dataQueryField);
        term.setQueryOperator(QueryOperator.equals);
        term.setValue(value);

        return term;
    }

    private VpnSession createVpnSessionFromImpalaRow(Map<String, Object> impalaEvent){

        VpnSession vpnSession = new VpnSession();

        //TODO

        return vpnSession;
    }

}