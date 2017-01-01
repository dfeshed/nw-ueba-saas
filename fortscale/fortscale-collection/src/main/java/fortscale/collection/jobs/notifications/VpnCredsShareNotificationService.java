package fortscale.collection.jobs.notifications;

import fortscale.common.dataqueries.querydto.DataQueryDTO;
import fortscale.common.dataqueries.querydto.DataQueryDTOImpl;
import fortscale.common.dataqueries.querydto.DataQueryField;
import fortscale.common.dataqueries.querydto.Term;
import fortscale.common.dataqueries.querygenerators.DataQueryRunner;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.domain.core.VpnSessionOverlap;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

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
public class VpnCredsShareNotificationService extends NotificationGeneratorServiceAbstract implements ApplicationContextAware{

    private static final String SERVICE_NAME = "VpnCredsShareNotifications";

    private static final String APP_CONF_PREFIX = "creds_share_notification";
    private static final String MIN_DATE_TIME_FIELD = "min_ts";

    private static final long MINIMAL_PROCESSING_PERIOD_IN_SEC = 10 * 60;

    private  ApplicationContext applicationContext;


    private String fieldManipulatorBeanName;



    private String hostnameField;
    private String hostnameDomainMarkersString;
    private Set<String> hostnameDomainMarkers;

    private String tableName;

    int numberOfConcurrentSessions;

    private String hostnameCondition;

    protected List<JSONObject>  generateNotificationInternal() throws Exception {


        List<Map<String, Object>> credsShareEvents = new ArrayList<>();

        logger.info("Generating notification of {}. latest time: {} ({}) current time: {} ({})", SERVICE_NAME,
                Instant.ofEpochSecond(latestTimestamp), latestTimestamp,
                Instant.ofEpochSecond(currentTimestamp), currentTimestamp);

        // Process events occurred until now.
        // Do not process periods shorter than MINIMAL_PROCESSING_PERIOD_IN_SEC. Protects from periodic execution jitter
        while(latestTimestamp <= currentTimestamp - MINIMAL_PROCESSING_PERIOD_IN_SEC) {

            // Calc the processing end time: process up to one day. Never process post the current time because the
            // those event simply does not exist yet

            long upperLimit = Math.min(latestTimestamp + DAY_IN_SECONDS, currentTimestamp);

            logger.info("Processing {} from {} ({}) to {} ({})", SERVICE_NAME,
                    Instant.ofEpochSecond(latestTimestamp), latestTimestamp,
                    Instant.ofEpochSecond(upperLimit), upperLimit);

            credsShareEvents.addAll(getCredsShareEventsFromHDFS(upperLimit));

            latestTimestamp = upperLimit;
        }

        List<JSONObject> credsShareNotifications = createCredsShareNotificationsFromImpalaRawEvents(credsShareEvents);
        credsShareNotifications = addRawEventsToCredsShare(credsShareNotifications);

        // Save latest processed timestamp in mongo application_configuration.
        // Do that at the end in case there is an error before
        Map<String, String> updateLastTimestamp = new HashMap<>();
        updateLastTimestamp.put(APP_CONF_PREFIX+"."+LASTEST_TS,String.valueOf(latestTimestamp));
        applicationConfigurationService.updateConfigItems(updateLastTimestamp);

        logger.info("Processing of {} done. {} events, {} notifications.Latest process time {} ({})", SERVICE_NAME,
                credsShareEvents.size(), credsShareNotifications.size(),
                Instant.ofEpochSecond(latestTimestamp), latestTimestamp);

        return  credsShareNotifications;
    }

    /**
     * resolve and init some attributes from other attributes
     */
    @PostConstruct
    public void init() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        initConfigurationFromApplicationConfiguration(APP_CONF_PREFIX, Arrays.asList(
				new ImmutablePair(LASTEST_TS, TS_PARAM),
                new ImmutablePair("hostnameDomainMarkersString", "hostnameDomainMarkersString"),
                new ImmutablePair("numberOfConcurrentSessions", "numberOfConcurrentSessions"),
                new ImmutablePair("fieldManipulatorBeanName", "fieldManipulatorBeanName")));

        this.hostnameDomainMarkers = new HashSet<>(Arrays.asList(this.hostnameDomainMarkersString.split(",")));
        this.tableName = dataEntitiesConfig.getEntityTable(dataEntity);
        //Init from bean name after fetch from configuration
        FieldManipulator fielManipulator = applicationContext.getBean(fieldManipulatorBeanName,FieldManipulator.class);
        this.hostnameCondition = fielManipulator.getManipulatedFieldCondition(hostnameField,hostnameDomainMarkers);


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

        conditions.add(dataQueryHelper.createDateRangeTermByOtherTimeField(dataEntity, "start_time_utc", (Long) credsShare.get(notificationStartTimestampField), (Long) credsShare.get(notificationEndTimestampField)));

        DataQueryDTO dataQueryDTO = dataQueryHelper.createDataQuery(dataEntity, "*", conditions, new ArrayList<>(), -1, DataQueryDTOImpl.class);

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
        credsShare.put(notificationSupportingInformationField, rawEvents);
        credsShare.put(notificationNumOfEventsField, rawEvents.size());
    }


    private List<Map<String, Object>> getCredsShareEventsFromHDFS(long upperLimit) {
        //create ConditionTerm for the hostname condition

		//TODO  - NEED TO DEVELOP THE UNSUPPORTED SQL FUNCTION AND TO REPLACE THIS CODE TO SUPPORT DATA QUERY
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

            JSONObject evidence = createCredsShareNotificationFromCredsShareQueryEvent(credsShareEvent);
            evidences.add(evidence);
            }

        return evidences;
        }

    /**
     * creates a creds share notification object from raw event returned from impala creds share query.
     * creds share notification object - a json object to send to evidence creation task as notification.
     * @param credsShareEvent
     * @return
     */
    private JSONObject createCredsShareNotificationFromCredsShareQueryEvent(Map<String, Object> credsShareEvent) {

        //TODO delete the parallel code from notification to evidence job!

        long startTime = getLongValueFromEvent(credsShareEvent, "start_time");
        long endTime = getLongValueFromEvent(credsShareEvent, "end_time");
        int sessionsCount = getIntegerValueFromEvent(credsShareEvent, "sessions_count");
        String normalizedUsername = getStringValueFromEvent(credsShareEvent, "normalized_username");

		return createNotification(startTime, endTime, normalizedUsername, "VPN_user_creds_share",
                Integer.toString(sessionsCount));

    }

    /**
     * creates supporting information single event for creds share - a vpnSessionOverlap object.
     * @param impalaEvent
     * @return
     */
    private VpnSessionOverlap createVpnSessionOverlapFromImpalaRow(Map<String, Object> impalaEvent){

        VpnSessionOverlap vpnSessionOverlap = new VpnSessionOverlap();

        vpnSessionOverlap.setCountry(getStringValueFromEvent(impalaEvent,"country"));
        vpnSessionOverlap.setDatabucket(getLongValueFromEvent(impalaEvent,"data_bucket"));
        vpnSessionOverlap.setDuration(getIntegerValueFromEvent(impalaEvent,"duration"));
        vpnSessionOverlap.setHostname(getStringValueFromEvent(impalaEvent,"source_machine"));
        vpnSessionOverlap.setLocal_ip(getStringValueFromEvent(impalaEvent,"local_ip"));
        vpnSessionOverlap.setReadbytes(getLongValueFromEvent(impalaEvent,"read_bytes"));
        vpnSessionOverlap.setSource_ip(getStringValueFromEvent(impalaEvent,"source_ip"));
        vpnSessionOverlap.setTotalbytes(getLongValueFromEvent(impalaEvent,"totalbytes"));
        vpnSessionOverlap.setDate_time_unix(getLongValueFromEvent(impalaEvent, "end_time_utc"));
        vpnSessionOverlap.setUsername(getStringValueFromEvent(impalaEvent,"normalized_username"));

        return vpnSessionOverlap;
    }


    public String getHostnameField() {
        return hostnameField;
    }

    public void setHostnameField(String hostnameField) {
        this.hostnameField = hostnameField;
    }

    public String getHostnameDomainMarkersString() {
        return hostnameDomainMarkersString;
    }

    public void setHostnameDomainMarkersString(String hostnameDomainMarkersString) {
        this.hostnameDomainMarkersString = hostnameDomainMarkersString;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDataEntity() {
        return dataEntity;
    }

    public void setDataEntity(String dataEntity) {
        this.dataEntity = dataEntity;
    }


    public int getNumberOfConcurrentSessions() {
        return numberOfConcurrentSessions;
    }

    public void setNumberOfConcurrentSessions(int numberOfConcurrentSessions) {
        this.numberOfConcurrentSessions = numberOfConcurrentSessions;
    }

    public String getFieldManipulatorBeanName() {
        return fieldManipulatorBeanName;
    }

    public void setFieldManipulatorBeanName(String fieldManipulatorBeanName) {
        this.fieldManipulatorBeanName = fieldManipulatorBeanName;
    }

    /**
	 * This method responsible on the fetching of the earliest event that this notification based on i.e - for cred sharing the base data source is vpnsession , in case of the first run we want to start executing the heuristic from the first event time
	 * @return
	 * @throws InvalidQueryException
	 */
    protected long fetchEarliestEvent() throws  InvalidQueryException{
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
            return Long.MAX_VALUE;
        }

        return extractEarliestEventFromDataQueryResult(queryList);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
