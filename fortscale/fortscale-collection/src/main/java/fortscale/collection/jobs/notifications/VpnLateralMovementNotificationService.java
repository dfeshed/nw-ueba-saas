package fortscale.collection.jobs.notifications;

import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.common.dataqueries.querydto.*;
import fortscale.common.dataqueries.querygenerators.DataQueryRunner;
import fortscale.common.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.common.dataqueries.querygenerators.mysqlgenerator.MySqlQueryRunner;
import fortscale.domain.core.VpnSessionOverlap;
import net.minidev.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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

	private final DateFormat df = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    private DataQueryHelper dataQueryHelper;
    @Autowired
    private MySqlQueryRunner queryRunner;
    @Autowired
    private DataQueryRunnerFactory dataQueryRunnerFactory;
	@Autowired
	private DataEntitiesConfig dataEntitiesConfig;

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
        List<Map<String, Object>> lateralMovementEvent = new ArrayList<>();
        while(latestTimestamp <= currentTimestamp) {
            long upperLimit = latestTimestamp + DAY_IN_SECONDS; //one day a time
            lateralMovementEvent.addAll(getLateralMovementEventsFromHDFS(upperLimit));
            latestTimestamp = upperLimit;
        }
        //save current timestamp in mongo application_configuration
        Map<String, String> updateLastTimestamp = new HashMap<>();
        updateLastTimestamp.put(APP_CONF_PREFIX + "." + LASTEST_TS, String.valueOf(latestTimestamp));
        applicationConfigurationService.updateConfigItems(updateLastTimestamp);
        List<JSONObject> lateralMovementNotifications =
				createLateralMovementsNotificationsFromImpalaRawEvents(lateralMovementEvent);
        lateralMovementNotifications = addRawEventsToLateralMovement(lateralMovementNotifications);
        return lateralMovementNotifications;
    }

    /**
     * resolve and init some attributes from other attributes
     */
    @PostConstruct
    public void init() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        initConfigurationFromApplicationConfiguration(APP_CONF_PREFIX);
        this.hostnameDomainMarkers = new HashSet<>(Arrays.asList(this.hostnameDomainMarkersString.split(",")));
        this.tableName = dataEntitiesConfig.getEntityTable(dataEntity);
        //Init from bean name after fetch from configuration
        FieldManipulator fieldManipulator = applicationContext.getBean(fieldManipulatorBeanName,
                FieldManipulator.class);
        this.hostnameCondition = fieldManipulator.getManipulatedFieldCondition(hostnameField,hostnameDomainMarkers);
    }

    private List<JSONObject> addRawEventsToLateralMovement(List<JSONObject> lateralMovementNotifications) {
		lateralMovementNotifications.forEach(this::addRawEvents);
        return lateralMovementNotifications;
    }

    private void addRawEvents(JSONObject lateralMovement) {
        //select * from vpnsessiondatares where username='#{username}' and date_time_unix>=#{start_time} and date_time_unix<=#{end_time}
        List<Term> conditions = new ArrayList<>();
        conditions.add(dataQueryHelper.createUserTerm(dataEntity,lateralMovement.getAsString("normalized_username")));
        conditions.add(dataQueryHelper.createDateRangeTermByOtherTimeField(dataEntity, "start_time_utc",
				(Long)lateralMovement.get(notificationStartTimestampField),
				(Long)lateralMovement.get(notificationEndTimestampField)));
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
        List<VpnSessionOverlap> rawEvents = queryList.stream().map(this::createVpnSessionOverlapFromImpalaRow).
				collect(Collectors.toList());
		// each map is a single event, each pair is column and value
		lateralMovement.put("supportingInformation", rawEvents);
        lateralMovement.put("notification_num_of_events",rawEvents.size());
    }

    private List<Map<String, Object>> getLateralMovementEventsFromHDFS(long upperLimit) {

//		select distinct seconds_sub(t1.date_time,t1.duration) vpn_session_start,t1.date_time vpn_session_end, t1.normalized_username vpn_uername,t2.normalized_username kerb_usern_name,t1.source_ip vpn_source_ip,t2.client_address kerb_source_ip,t1.hostname vpn_host, t2.machine_name kerb_host , t2.failure_code
//		from vpnsessiondatares t1 inner join authenticationscores t2 on t1.yearmonthday=20160510 and t2.yearmonthday=20160510 and t1.local_ip = t2.client_address and t2.date_time_unix between t1.date_time_unix-t1.duration and t1.date_time_unix and t1.normalized_username != t2.normalized_username;
//
//		select distinct seconds_sub(t1.date_time,t1.duration) vpn_session_start,t1.date_time vpn_session_end, t1.normalized_username vpn_uername,t2.normalized_username ssh_usern_name,t1.source_ip vpn_source_ip,t2.source_ip ssh_source_ip,t1.hostname
//		from vpnsessiondatares t1 inner join sshscores t2 on t1.yearmonthday=20160510 and t2.yearmonthday=20160510 and t1.local_ip = t2.source_ip and t2.date_time_unix between t1.date_time_unix-t1.duration and t1.date_time_unix and t1.normalized_username != t2.normalized_username;

	 /*String query = "select" +
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
		" t group by username,normalized_username," + hostnameField + ",id";*/

        Date date = new Date(upperLimit);
        String dateStr = df.format(date);
		String table = "sshscores";
		String ipField = "source_ip";
		String query = "select distinct seconds_sub(t1.date_time,t1.duration) vpn_session_start, " +
				"t1.date_time vpn_session_end, t1.normalized_username vpn_username, " +
				"t2.normalized_username datasource_username, t1.source_ip vpn_source_ip, t2." + ipField +
				" datasource_source_ip, t1.hostname from vpnsessiondatares t1 inner join " + table +
				" t2 on t1.yearmonthday=" + dateStr + " and t2.yearmonthday=" + dateStr +
                " and t1.local_ip = t2.source_ip " + "and t2.date_time_unix between t1.date_time_unix-t1.duration " +
                "and t1.date_time_unix and t1.normalized_username != t2.normalized_username";
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

    private List<JSONObject> createLateralMovementsNotificationsFromImpalaRawEvents(List<Map<String, Object>>
			lateralMovementEvents) {
        List<JSONObject> evidences = new ArrayList<>();
		// each map is a single event, each pair is column and value
        for (Map<String, Object> lateralMovementEvent : lateralMovementEvents) {
            JSONObject evidence = createLateralMovementNotificationFromLateralMovementQueryEvent(lateralMovementEvent);
            evidences.add(evidence);
        }
        return evidences;
    }

    /**
     * creates a lateral movement notification object from raw event returned from impala lateral movement query.
     * lateral movement notification object - a json object to send to evidence creation task as notification.
     * @param lateralMovementEvent
     * @return
     */
    private JSONObject createLateralMovementNotificationFromLateralMovementQueryEvent(Map<String, Object>
			lateralMovementEvent) {
        JSONObject vpnLateralMovement = new JSONObject();
        long startTime = getLongValueFromEvent(lateralMovementEvent, "start_time");
        long endTime = getLongValueFromEvent(lateralMovementEvent, "end_time");
        int sessionsCount = getIntegerValueFromEvent(lateralMovementEvent, "sessions_count");
        String normalizedUsername = getStringValueFromEvent(lateralMovementEvent, "normalized_username");
        vpnLateralMovement.put(notificationScoreField, notificationFixedScore);
        vpnLateralMovement.put(notificationStartTimestampField, startTime);
        vpnLateralMovement.put(notificationEndTimestampField, endTime);
        vpnLateralMovement.put(notificationTypeField, "VPN_user_lateral_movement");
        vpnLateralMovement.put(notificationValueField, sessionsCount);
        vpnLateralMovement.put(normalizedUsernameField, normalizedUsername);
        List<String> entities = new ArrayList<>();
        entities.add(dataEntity);
        vpnLateralMovement.put(notificationDataSourceField, entities);
        return vpnLateralMovement;
    }

    /**
     * creates supporting information single event for lateral movement - a vpnSessionOverlap object.
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

	/**
	 * This method responsible on the fetching of the earliest event that this notification based on i.e -
	 * for fred sharing the base data source is vpnsession , in case of the first run we want to start executing the
	 * heuristic from the first event time
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