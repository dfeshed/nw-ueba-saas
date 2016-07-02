package fortscale.collection.jobs.notifications;

import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.common.dataentity.DataEntity;
import fortscale.common.dataqueries.querydto.*;
import fortscale.common.dataqueries.querygenerators.DataQueryRunner;
import fortscale.common.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.common.dataqueries.querygenerators.mysqlgenerator.MySqlQueryRunner;
import fortscale.domain.core.VpnSessionOverlap;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
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
public class VpnLateralMovementNotificationService extends NotificationGeneratorServiceAbstract {

    private static final String APP_CONF_PREFIX = "lateral_movement_notification";
    private static final String MIN_DATE_TIME_FIELD = "min_ts";
	private static final String SOURCE_IP_FIELD = "source_ip";

	private final DateFormat df = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    private DataQueryHelper dataQueryHelper;
    @Autowired
    private MySqlQueryRunner queryRunner;
    @Autowired
    private DataQueryRunnerFactory dataQueryRunnerFactory;
	@Autowired
	private DataEntitiesConfig dataEntitiesConfig;

    @Value("${impala.table.fields.source_ip}")
    private String sourceIpFieldName;
	@Value("${impala.score.ldapauth.table.fields.client_address}")
	public String authSourceIpFieldName;

	private Map<String, String> tableToSourceIpField;
	private String dataEntity;

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
	 * This method responsible on the fetching of the earliest event that this notification based on
	 * @return
	 * @throws InvalidQueryException
	 */
	protected long fetchEarliestEvent() throws InvalidQueryException {
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

	/**
     * resolve and init some attributes from other attributes
     */
    @PostConstruct
    public void init() throws Exception {
		tableToSourceIpField = new HashMap<>();
		Map<String, DataEntity> entities;
		initConfigurationFromApplicationConfiguration(APP_CONF_PREFIX, new ArrayList<>());
		try {
			entities = dataEntitiesConfig.getAllLeafeEntities();
		} catch (Exception ex) {
			logger.error("failed to get entities {}", ex);
			throw new Exception("failed to get entities");
		}
		for (Map.Entry<String, DataEntity> entry: entities.entrySet()) {
			String tableName = dataEntitiesConfig.getEntityTable(entry.getKey());
            if (tableName.toLowerCase().contains("vpn")) {
                continue;
            }
            String sourceIpField;
            try {
                sourceIpField = dataEntitiesConfig.getFieldColumn(entry.getKey(), SOURCE_IP_FIELD);
            } catch (InvalidQueryException ex) {
                continue;
            }
			tableToSourceIpField.put(tableName, sourceIpField);
		}
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
        Date date = new Date(upperLimit);
        String dateStr = df.format(date);
		List<Map<String, Object>> result = new ArrayList<>();
		for (Map.Entry<String, String> entry: tableToSourceIpField.entrySet()) {
			String query = "select distinct seconds_sub(t1.date_time,t1.duration) vpn_session_start, " +
					"t1.date_time vpn_session_end, t1.normalized_username vpn_username, " +
					"t2.normalized_username datasource_username, t1.source_ip vpn_source_ip, t2." + entry.getValue() +
					" datasource_source_ip, t1.hostname from vpnsessiondatares t1 inner join " + entry.getKey() +
					" t2 on t1.yearmonthday=" + dateStr + " and t2.yearmonthday=" + dateStr +
					" and t1.local_ip = t2.source_ip " + "and t2.date_time_unix between t1.date_time_unix-t1.duration" +
					" and t1.date_time_unix and t1.normalized_username != t2.normalized_username";
			result.addAll(queryRunner.executeQuery(query));
		}
		return result;
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

    public void setDataEntity(String dataEntity) {
        this.dataEntity = dataEntity;
    }
}