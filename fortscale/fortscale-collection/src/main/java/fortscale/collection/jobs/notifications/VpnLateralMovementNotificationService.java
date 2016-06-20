package fortscale.collection.jobs.notifications;

import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.common.dataqueries.querydto.*;
import fortscale.common.dataqueries.querygenerators.DataQueryRunner;
import fortscale.common.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.common.dataqueries.querygenerators.mysqlgenerator.MySqlQueryRunner;
import fortscale.domain.core.VpnSessionOverlap;
import fortscale.services.impl.ApplicationConfigurationHelper;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.*;

/**
 * VPN Lateral Movement notification does the following:
 * Querying to find the following scenario -
 * User1 is connecting via VPN and receives a local IP address IP1.
 * During the VPN session, we see an activity of User2 from IP1 to TargetDevice1.
 *
 * Created by Amir Keren on 06/20/2016.
 */
public class VpnLateralMovementNotificationService extends NotificationGeneratorServiceAbstract
		implements ApplicationContextAware {

    private static final String APP_CONF_PREFIX = "lateral_movement_notification";
    private static final String MIN_DATE_TIME_FIELD = "min_ts";

    @Autowired
    private DataQueryHelper dataQueryHelper;
    @Autowired
    private MySqlQueryRunner queryRunner;
    @Autowired
    private DataQueryRunnerFactory dataQueryRunnerFactory;
	@Autowired
	private DataEntitiesConfig dataEntitiesConfig;
	@Autowired
	private ApplicationConfigurationHelper applicationConfigurationHelper;

	private ApplicationContext applicationContext;
	private Set<String> hostnameDomainMarkers;
	private String fieldManipulatorBeanName;
    private String hostnameField;
    private String hostnameDomainMarkersString;
    private String tableName;
    private String dataEntity;
    private String hostnameCondition;
	private int numberOfConcurrentSessions;

    protected List<JSONObject> generateNotificationInternal() throws Exception {
        List<Map<String, Object>> credsShareEvents = new ArrayList<>();
        while(latestTimestamp <= currentTimestamp) {
            long upperLimit = latestTimestamp + DAY_IN_SECONDS; //one day a time
            credsShareEvents.addAll(getCredsShareEventsFromHDFS(upperLimit));
            latestTimestamp = upperLimit;
        }
        //save current timestamp in mongo application_configuration
        Map<String, String> updateLastTimestamp = new HashMap<>();
        updateLastTimestamp.put(APP_CONF_PREFIX + "." + LASTEST_TS, String.valueOf(latestTimestamp));
        applicationConfigurationService.updateConfigItems(updateLastTimestamp);
        List<JSONObject> credsShareNotifications = createCredsShareNotificationsFromImpalaRawEvents(credsShareEvents);
        credsShareNotifications = addRawEventsToCredsShare(credsShareNotifications);
        return credsShareNotifications;
    }

    /**
     * resolve and init some attributes from other attributes
     */
    @PostConstruct
    public void init() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        initConfigurationFromApplicationConfiguration();
        this.hostnameDomainMarkers = new HashSet<>(Arrays.asList(this.hostnameDomainMarkersString.split(",")));
        this.tableName = dataEntitiesConfig.getEntityTable(dataEntity);
        //Init from bean name after fetch from configuration
        FieldManipulator fielManipulator = applicationContext.getBean(fieldManipulatorBeanName, FieldManipulator.class);
        this.hostnameCondition = fielManipulator.getManipulatedFieldCondition(hostnameField,hostnameDomainMarkers);
    }

    private void initConfigurationFromApplicationConfiguration() throws IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
        applicationConfigurationHelper.syncWithConfiguration(APP_CONF_PREFIX, this, Arrays.asList(
                new ImmutablePair(LASTEST_TS,"latestTimestamp"),
                new ImmutablePair("hostnameDomainMarkersString", "hostnameDomainMarkersString"),
                new ImmutablePair("numberOfConcurrentSessions", "numberOfConcurrentSessions"),
                new ImmutablePair("notificationScoreField", "notificationScoreField"),
                new ImmutablePair("notificationTypeField", "notificationTypeField"),
                new ImmutablePair("notificationValueField", "notificationValueField"),
                new ImmutablePair("notificationStartTimestampField", "notificationStartTimestampField"),
                new ImmutablePair("normalizedUsernameField", "normalizedUsernameField"),
                new ImmutablePair("notificationSupportingInformationField", "notificationSupportingInformationField"),
                new ImmutablePair("notificationDataSourceField", "notificationDataSourceField"),
                new ImmutablePair("fieldManipulatorBeanName", "fieldManipulatorBeanName"),
                new ImmutablePair("notificationFixedScore", "notificationFixedScore")
        ));
    }

    private List<JSONObject> addRawEventsToCredsShare(List<JSONObject> credsShareNotifications) {
        for (JSONObject credsShare: credsShareNotifications) {
            addRawEvents(credsShare);
        }
        return credsShareNotifications;
    }

    private void addRawEvents(JSONObject credsShare) {
        //select * from vpnsessiondatares where username='#{username}' and date_time_unix>=#{start_time} and date_time_unix<=#{end_time}
        List<Term> conditions = new ArrayList<>();
        conditions.add(dataQueryHelper.createUserTerm(dataEntity,credsShare.getAsString("normalized_username")));
        conditions.add(dataQueryHelper.createDateRangeTermByOtherTimeField(dataEntity, "start_time_utc",
				(Long)credsShare.get(notificationStartTimestampField),
				(Long)credsShare.get(notificationEndTimestampField)));
        DataQueryDTO dataQueryDTO = dataQueryHelper.createDataQuery(dataEntity, "*", conditions, new ArrayList<>(), -1,
				DataQueryDTOImpl.class);
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
        credsShare.put("supportingInformation", rawEvents);
        credsShare.put("notification_num_of_events",rawEvents.size());
    }

    private List<Map<String, Object>> getCredsShareEventsFromHDFS(long upperLimit) {

//		select distinct seconds_sub(t1.date_time,t1.duration) vpn_session_start,t1.date_time vpn_session_end, t1.normalized_username vpn_uername,t2.normalized_username kerb_usern_name,t1.source_ip vpn_source_ip,t2.client_address kerb_source_ip,t1.hostname vpn_host, t2.machine_name kerb_host , t2.failure_code
//		from vpnsessiondatares t1 inner join authenticationscores t2 on t1.yearmonthday=20160510 and t2.yearmonthday=20160510 and t1.local_ip = t2.client_address and t2.date_time_unix between t1.date_time_unix-t1.duration and t1.date_time_unix and t1.normalized_username != t2.normalized_username;
//
//		select distinct seconds_sub(t1.date_time,t1.duration) vpn_session_start,t1.date_time vpn_session_end, t1.normalized_username vpn_uername,t2.normalized_username ssh_usern_name,t1.source_ip vpn_source_ip,t2.source_ip ssh_source_ip,t1.hostname
//		from vpnsessiondatares t1 inner join sshscores t2 on t1.yearmonthday=20160510 and t2.yearmonthday=20160510 and t1.local_ip = t2.source_ip and t2.date_time_unix between t1.date_time_unix-t1.duration and t1.date_time_unix and t1.normalized_username != t2.normalized_username;

        //create ConditionTerm for the hostname condition
		//TODO  - NEED TO DEVELOP THE UNSUPPORTED SQL FUNCTION AND TO REPLACE THIS CODE TO SUPPORT DATA QUERY
        //create dataQuery for the overlapping sessions - use impalaJDBC and not dataQuery mechanism since
        // some features of the query aren't supported in dataQuery: e.g. CASE WHEN , or SQL functions: lpad, instr
        String query = "select" +
			" username ,normalized_username,id, " + hostnameField + ", count(*) as sessions_count ,min(start_session_time) as start_time ,max(end_session_time) as end_time" +
			" from" +
			" (select t1.username,t1.normalized_username,t1.hostname, u.id, unix_timestamp(seconds_sub(t1.date_time, t1.duration)) as start_session_time ,unix_timestamp(t1.date_time)" +
			" as end_session_time  " +
			"from " + tableName + " t1 inner join " + tableName + " t2 " +
			"on t1.username = t2.username and t1.source_ip!=t2.source_ip  and  seconds_sub(t2.date_time,t2.duration) between seconds_sub(t1.date_time,t1.duration) and t1.date_time  " +
			"inner join users u on t1.normalized_username = u.username where t1.source_ip !='' and t2.source_ip !='' and t1.country = 'Reserved Range' and t2.country='Reserved Range'" +
			" and " + hostnameCondition + " and t1.date_time_unix >= " + latestTimestamp + " and t1.date_time_unix < " + upperLimit + " " +
			"group by t1.username,t1.normalized_username,t1." + hostnameField + ",t1.source_ip ,seconds_sub(t1.date_time, t1.duration) ,t1.date_time,u.id " +
			"having count(t2.source_ip) >= " + numberOfConcurrentSessions + "  )" +
			" t group by username,normalized_username," + hostnameField + ",id";
        //run the query
        return queryRunner.executeQuery(query);

    }

    private long extractEarliestEventFromDataQueryResult(List<Map<String, Object>> queryList) {
        for (Map<String, Object>  resultPair: queryList) {
            if (resultPair.get(MIN_DATE_TIME_FIELD) != null) {
                Timestamp timeToUnix =  (Timestamp)resultPair.get(MIN_DATE_TIME_FIELD);
                return timeToUnix.getTime();
            }
        }
        return 0;
    }

    private List<JSONObject> createCredsShareNotificationsFromImpalaRawEvents(List<Map<String, Object>>
			credsShareEvents) {
        List<JSONObject> evidences = new ArrayList<>();
		// each map is a single event, each pair is column and value
        for (Map<String, Object> credsShareEvent : credsShareEvents) {
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

    /**
     * creates supporting information single event for creds share - a vpnSessionOverlap object.
     * @param impalaEvent
     * @return
     */
    private VpnSessionOverlap createVpnSessionOverlapFromImpalaRow(Map<String, Object> impalaEvent) {
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

    private String getStringValueFromEvent(Map<String, Object> impalaEvent,String field) {
        if (impalaEvent.containsKey(field)) {
            return impalaEvent.get(field).toString();
        }
        return "";
    }

    private int getIntegerValueFromEvent(Map<String, Object> impalaEvent,String field) {
        if (impalaEvent.containsKey(field)) {
            return Integer.parseInt(impalaEvent.get(field).toString());
        }
        return 0;
    }

    private long getLongValueFromEvent(Map<String, Object> impalaEvent,String field){
        if (impalaEvent.containsKey(field)) {
            return Long.parseLong(impalaEvent.get(field).toString());
        }
        return 0L;
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

    public String getNotificationScoreField() {
        return notificationScoreField;
    }

    public void setNotificationScoreField(String notificationScoreField) {
        this.notificationScoreField = notificationScoreField;
    }

    public String getNotificationValueField() {
        return notificationValueField;
    }

    public void setNotificationValueField(String notificationValueField) {
        this.notificationValueField = notificationValueField;
    }

    public String getNormalizedUsernameField() {
        return normalizedUsernameField;
    }

    public void setNormalizedUsernameField(String normalizedUsernameField) {
        this.normalizedUsernameField = normalizedUsernameField;
    }

    public String getNotificationDataSourceField() {
        return notificationDataSourceField;
    }

    public void setNotificationDataSourceField(String notificationDataSourceField) {
        this.notificationDataSourceField = notificationDataSourceField;
    }

    public String getNotificationStartTimestampField() {
        return notificationStartTimestampField;
    }

    public void setNotificationStartTimestampField(String notificationStartTimestampField) {
        this.notificationStartTimestampField = notificationStartTimestampField;
    }

    public String getNotificationEndTimestampField() {
        return notificationEndTimestampField;
    }

    public void setNotificationEndTimestampField(String notificationEndTimestampField) {
        this.notificationEndTimestampField = notificationEndTimestampField;
    }

    public String getNotificationTypeField() {
        return notificationTypeField;
    }

    public void setNotificationTypeField(String notificationTypeField) {
        this.notificationTypeField = notificationTypeField;
    }

    public String getNotificationSupportingInformationField() {
        return notificationSupportingInformationField;
    }

    public void setNotificationSupportingInformationField(String notificationSupportingInformationField) {
        this.notificationSupportingInformationField = notificationSupportingInformationField;
    }

    public String getFieldManipulatorBeanName() {
        return fieldManipulatorBeanName;
    }

    public void setFieldManipulatorBeanName(String fieldManipulatorBeanName) {
        this.fieldManipulatorBeanName = fieldManipulatorBeanName;
    }

    /**
	 * This method responsible on the fetching of the earliest event that this notification based on i.e - for fred sharing the base data source is vpnsession , in case of the first run we want to start executing the heuristic from the first event time
	 * @return
	 * @throws InvalidQueryException
	 */
    protected long fetchEarliestEvent() throws  InvalidQueryException{
        DataQueryDTO dataQueryDTO = dataQueryHelper.createDataQuery(dataEntity, null, new ArrayList<>(),
				new ArrayList<>(), -1, DataQueryDTOImpl.class);
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

    public void setNotificationFixedScore(double notificationFixedScore) {
        this.notificationFixedScore = notificationFixedScore;
    }

    public double getNotificationFixedScore() {
        return notificationFixedScore;
    }

}