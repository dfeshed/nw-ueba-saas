package fortscale.collection.jobs.notifications;

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

    @Autowired
    private DataEntitiesConfig dataEntitiesConfig;

    @Value("${collection.evidence.notification.topic}")
    private String evidenceNotificationTopic;

    String hostnameField;
    String hostnameManipulateFunc;
    List<String> hostnameDomainMarkers;

    String tableName;
    String dataEntity;
    long latestTimestamp = 0L;
    long currentTimestamp =0L;

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

        JobDataMap map = context.getMergedJobDataMap();
        hostnameField = jobDataMapExtension.getJobDataMapStringValue(map,"hostnameField");
        hostnameManipulateFunc =  jobDataMapExtension.getJobDataMapStringValue(map,"hostnameManipulatorFunc");
        hostnameDomainMarkers =  jobDataMapExtension.getJobDataMapListOfStringsValue(map,"hostnameDomainMarkers",",");
        dataEntity = jobDataMapExtension.getJobDataMapStringValue(map,"dataEntity");
        tableName = dataEntitiesConfig.getEntityTable(dataEntity);
        numberOfConcurrentSessions = jobDataMapExtension.getJobDataMapIntValue(map,"numberOfConcurrentSessions");

        //fields for building the creds share notification
        notificationValueField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationValueField");
        normalizedUsernameField = jobDataMapExtension.getJobDataMapStringValue(map, "normalizedUsernameField");
        notificationDataSourceField = jobDataMapExtension.getJobDataMapStringValue(map, "dataSourceField");
        notificationStartTimestampField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationStartTimestampField");
        notificationEndTimestampField = jobDataMapExtension.getJobDataMapStringValue(map,"notificationEndTimestampField");
        notificationTypeField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationTypeField");
        notificationSupportingInformationField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationSupportingInformationField");
        notificationScoreField = jobDataMapExtension.getJobDataMapStringValue(map, "notificationScoreField");
        notificationFixedScore = jobDataMapExtension.getJobDataMapStringValue(map, "notificationScore");//TODO notification shouldn't have score at all


        // get the job group name to be used using monitoring
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

        logger.info("{} {} job started", jobName, sourceName);

        startNewStep("Get the latest run time");
         boolean tableHasData = figureLatestRunTime();
        if(!tableHasData){
            return;
        }
        finishStep();

        startNewStep("Query impala for creds share notifications");

        List<Map<String, Object>> credsShareEvents = new ArrayList<>();

        while(latestTimestamp <= currentTimestamp) {

            long upperLimit = latestTimestamp + DAY_IN_SECONDS; //one day a time
            credsShareEvents.addAll(getCredsShareEventsFromHDFS(upperLimit));

            latestTimestamp = upperLimit;
        }
        //save current timestamp in mongo application_configuration
        applicationConfigurationService.insertConfigItem(LASTEST_TS,String.valueOf(latestTimestamp));

        finishStep();

        startNewStep( String.format("found {} creds share notifications. creating indicators from them (not sending yet!)",credsShareEvents.size()));
        List<JSONObject> credsShareNotifications = createCredsShareNotificationsFromImpalaRawEvents(credsShareEvents);
        finishStep();

        startNewStep(" Adding supporting information (raw events) for indicators - query impala");
        credsShareNotifications = addRawEventsToCredsShare(credsShareNotifications);
        finishStep();

        startNewStep("Sends the indicators to evidence creation task");
        sendCredsShareNotificationsToKafka(credsShareNotifications);
        finishStep();

        logger.info("{} {} job finished", jobName, sourceName);
        }

    private void sendCredsShareNotificationsToKafka(List<JSONObject> credsShareNotifications) {

        for (JSONObject credsShare: credsShareNotifications){
            sendCredsShareNotificationToKafka(credsShare);
        }
    }

    private void sendCredsShareNotificationToKafka(JSONObject credsShare) {
        String messageToWrite = credsShare.toJSONString(JSONStyle.NO_COMPRESS);
        logger.info("Writing to topic evidence - {}", messageToWrite);

        KafkaEventsWriter streamWriter = new KafkaEventsWriter(evidenceNotificationTopic);
        streamWriter.send("VPN_user_creds_share", messageToWrite);

    }

    private List<JSONObject> addRawEventsToCredsShare( List<JSONObject> credsShareNotifications ) {

        for (JSONObject credsShare: credsShareNotifications){
            addRawEvents(credsShare);
        }
        return credsShareNotifications;
    }

    private void addRawEvents(JSONObject credsShare) {
        //select * from vpnsessiondatares where username='#{username}' and date_time_unix>=#{start_time} and date_time_unix<=#{end_time}
        List<Term> conditions = new ArrayList<>();
        conditions.add(dataQueryHelper.createUserTerm(dataEntity,credsShare.getAsString("normalized_username")));
        conditions.add(dataQueryHelper.createDateRangeTerm(dataEntity, (long) credsShare.get("date_time_unix_start"), (long)credsShare.get("date_time_unix_end")));
        DataQueryDTO dataQueryDTO = dataQueryHelper.createDataQuery(dataEntity, "", conditions, null, -1, DataQueryDTOImpl.class);

        DataQueryRunner dataQueryRunner = null;
        String rawEventsQuery = "";
        try {
            dataQueryRunner = dataQueryRunnerFactory.getDataQueryRunner(dataQueryDTO);
            rawEventsQuery = dataQueryRunner.generateQuery(dataQueryDTO);
            logger.info("Running the query: {}", rawEventsQuery);
        } catch (InvalidQueryException e) {
            logger.debug("bad supporting information query: ",e.getMessage());
        }
        // execute Query
        List<Map<String, Object>> queryList = dataQueryRunner.executeQuery(rawEventsQuery);

        //extract the supporting information
        List<VpnSessionOverlap> rawEvents = new ArrayList<>();
        for (Map<String, Object> rawEvent : queryList) { // each map is a single event, each pair is column and value
            rawEvents.add(createVpnSessionOverlapFromImpalaRow(rawEvent));
        }
        credsShare.put("raw_event", rawEvents);
    }


    private List<Map<String, Object>> getCredsShareEventsFromHDFS(long upperLimit) {
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
        return  queryRunner.executeQuery(query);

    }

    /**
     * gets the last run time of creds share. if the table is empty - return.
     * @return
     * @throws InvalidQueryException
     */
    private boolean figureLatestRunTime() throws InvalidQueryException {
        //read latestTimestamp from mongo collection application_configuration
        currentTimestamp = TimestampUtils.convertToSeconds(System.currentTimeMillis());
        if(applicationConfigurationService.getApplicationConfigurationByKey(LASTEST_TS) !=null) {
            latestTimestamp = Long.parseLong(applicationConfigurationService.getApplicationConfigurationByKey(LASTEST_TS).getValue());
        }
        if (latestTimestamp == 0L) {

            //create query to find the earliest event
            DataQueryDTO dataQueryDTO = dataQueryHelper.createDataQuery(dataEntity, null, new ArrayList<>(), new ArrayList<>(), -1, DataQueryDTOImpl.class);
            DataQueryField countField = dataQueryHelper.createMinFieldFunc("end_time", MIN_DATE_TIME_FIELD);
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
            latestTimestamp = Math.min(earliestEventTimestamp, currentTimestamp - WEEK_IN_SECONDS);
            logger.info("latest run time was empty - setting latest timestamp to {}",latestTimestamp);
        }
        return true;
    }

    private long extractEarliestEventFromDataQueryResult(List<Map<String, Object>> queryList) {
        for(Map<String, Object>  resultPair: queryList){
            if(resultPair.get(MIN_DATE_TIME_FIELD) != null){
                Timestamp timeToUnix =  (Timestamp)resultPair.get(MIN_DATE_TIME_FIELD);
                return  timeToUnix.getTime();
            }
        }
        return 0;
    }

    private List<JSONObject> createCredsShareNotificationsFromImpalaRawEvents(List<Map<String, Object>> credsShareEvents){

        List<JSONObject> evidences = new ArrayList<>();
        for (Map<String, Object> credsShareEvent : credsShareEvents) { // each map is a single event, each pair is column and value

            JSONObject evidence = createCredsShareFromImpalaRawEvent(credsShareEvent);
            evidences.add(evidence);
            }

        return evidences;
        }

    private JSONObject createCredsShareFromImpalaRawEvent(Map<String, Object> credsShareEvent) {

        //TODO delete the parallel code from notification to evidence job!

        JSONObject vpnCredsShare = new JSONObject();

        long startTime = getLongValueFromEvent(credsShareEvent, "start_time");
        long endTime = getLongValueFromEvent(credsShareEvent, "end_time");
        int sessionsCount = getIntegerValueFromEvent(credsShareEvent, "sessions_count");
        String normalizedUsername = getStringValueFromEvent(credsShareEvent, "normalized_username");

        vpnCredsShare.put(notificationScoreField, notificationFixedScore);
        vpnCredsShare.put(notificationStartTimestampField, startTime);
        vpnCredsShare.put(notificationEndTimestampField, endTime);
        vpnCredsShare.put(notificationTypeField, "VPN_user_creds_share");
        vpnCredsShare.put(notificationValueField, sessionsCount);
        vpnCredsShare.put(normalizedUsernameField, normalizedUsername);
        List<String> entities = new ArrayList();
        entities.add(dataEntity);
        vpnCredsShare.put(notificationDataSourceField, entities);

        return vpnCredsShare;
    }


    private VpnSessionOverlap createVpnSessionOverlapFromImpalaRow(Map<String, Object> impalaEvent){

        VpnSessionOverlap vpnSessionOverlap = new VpnSessionOverlap();

        vpnSessionOverlap.setCountry(getStringValueFromEvent(impalaEvent,"country"));
        vpnSessionOverlap.setDatabucket(getLongValueFromEvent(impalaEvent,"databucket"));
        vpnSessionOverlap.setDuration(getIntegerValueFromEvent(impalaEvent,"duration"));
        vpnSessionOverlap.setHostname(getStringValueFromEvent(impalaEvent,"hostname"));
        vpnSessionOverlap.setLocal_ip(getStringValueFromEvent(impalaEvent,"local_ip"));
        vpnSessionOverlap.setReadbytes(getLongValueFromEvent(impalaEvent,"readbytes"));
        vpnSessionOverlap.setSource_ip(getStringValueFromEvent(impalaEvent,"source_ip"));
        vpnSessionOverlap.setTotalbytes(getLongValueFromEvent(impalaEvent,"totalbytes"));
        return vpnSessionOverlap;
    }

    private String getStringValueFromEvent(Map<String, Object> impalaEvent,String field){
        if( impalaEvent.containsKey(field)){
            return  impalaEvent.get(field).toString();
        }
        else return "";
    }

    private int getIntegerValueFromEvent(Map<String, Object> impalaEvent,String field){
        if( impalaEvent.containsKey(field)){
            return  Integer.parseInt(impalaEvent.get(field).toString());
        }
        else return 0;
    }

    private long getLongValueFromEvent(Map<String, Object> impalaEvent,String field){
        if( impalaEvent.containsKey(field)){
            return  Long.parseLong(impalaEvent.get(field).toString());
        }
        else return 0L;
    }

}