package fortscale.collection.jobs.notifications;

import fortscale.common.dataentity.DataEntity;
import fortscale.common.dataqueries.querydto.DataQueryDTO;
import fortscale.common.dataqueries.querydto.DataQueryDTOImpl;
import fortscale.common.dataqueries.querydto.Term;
import fortscale.common.dataqueries.querygenerators.DataQueryRunner;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.common.event.NotificationAnomalyType;
import fortscale.domain.core.User;
import fortscale.domain.core.VpnLateralMovementSupportingInformation;
import fortscale.services.UserService;
import fortscale.utils.CustomedFilter;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.time.Instant;
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
	private static final String SERVICE_NAME = "VpnLateralMovementNotification";
	private static final String APP_CONF_PREFIX = "lateral_movement_notification";
	private static final String SOURCE_IP_FIELD = "source_ip";
	private static final String VPN_START_TIME = "vpn_session_start";
	private static final String VPN_END_TIME = "vpn_session_end";
	private static final String VPN_USERNAME = "vpn_username";
	private static final String TABLE_NAME = "table_name";
	private static final String DATASOURCE_USERNAME = "datasource_username";
	private static final String DATASOURCE_IP = "datasource_ip";
	private static final String EVENT_TIME_UTC = "event_time_utc";
	private static final String DISPLAY_NAME = "display_name";
	private static final String START_TIME_UTC = "start_time_utc";
	private static final String DATE_TIME_UNIX = "date_time_unix";
	private static final String DATA_SOURCE = "data_source";
	private static final List<String> dataSources = Arrays.asList(
			"kerberos_logins", "kerberos_tgt", "ssh", "crmsf", "prnlog", "oracle");
	public static final String ENTITY_ID = "entity_id";
	public static final String SOURCE_IP_COLUMN = "source_ip";

	@Autowired
	private UserService userService;

	@Value("${impala.table.fields.source_ip}")
	private String sourceIpFieldName;
	@Value("${impala.score.ldapauth.table.fields.client_address}")
	public String authSourceIpFieldName;

	private Map<String, Pair<String, String>> tableToEntityIdAndIPField;

	protected List<JSONObject> generateNotificationInternal() throws Exception {
		Map<VPNSessionEvent, List<Map<String, Object>>> lateralMovementEvents = new HashMap<>();
		logger.info("Generating notification of {}. latest time: {} ({}) current time: {} ({})", SERVICE_NAME,
				Instant.ofEpochSecond(latestTimestamp), latestTimestamp,
				Instant.ofEpochSecond(currentTimestamp), currentTimestamp);

		// Process events occurred until now. Do not process periods shorter than
		// MINIMAL_PROCESSING_PERIOD_IN_SEC. Protects from periodic execution jitter
		while (latestTimestamp <= currentTimestamp - MINIMAL_PROCESSING_PERIOD_IN_SEC) {
			// Calc the processing end time: process up to one day. Never process
			// post the current time because those event simply does not exist yet
			long upperLimitExcluding = Math.min(latestTimestamp + DAY_IN_SECONDS, currentTimestamp);
			getLateralMovementEventsFromHDFS(lateralMovementEvents, upperLimitExcluding - 1);
			latestTimestamp = upperLimitExcluding;
		}

		List<JSONObject> lateralMovementNotifications =
				createLateralMovementsNotificationsFromImpalaRawEvents(lateralMovementEvents);
		lateralMovementNotifications =
				addRawEventsToLateralMovement(lateralMovementNotifications, lateralMovementEvents);

		// Save latest processed timestamp in mongo application_configuration.
		// Do that at the end in case there is an error before
		Map<String, String> updateLastTimestamp = new HashMap<>();
		updateLastTimestamp.put(APP_CONF_PREFIX + "." + LATEST_TS, String.valueOf(latestTimestamp));
		applicationConfigurationService.updateConfigItems(updateLastTimestamp);

		logger.info("Processing of {} done. {} events, {} notifications, latest process time {} ({})", SERVICE_NAME,
				lateralMovementEvents.size(), lateralMovementNotifications.size(),
				Instant.ofEpochSecond(latestTimestamp), latestTimestamp);
		return lateralMovementNotifications;
	}

	/**
	 * resolve and init some attributes from other attributes
	 */
	@PostConstruct
	public void init() throws Exception {
		tableToEntityIdAndIPField = new HashMap<>();
		Map<String, DataEntity> entities;
		initConfigurationFromApplicationConfiguration(APP_CONF_PREFIX,
				Collections.singletonList(new ImmutablePair<>(LATEST_TS, TS_PARAM)));
		try {
			entities = dataEntitiesConfig.getAllLeafeEntities();
		} catch (Exception ex) {
			logger.error("failed to get entities {}", ex);
			throw new Exception("failed to get entities");
		}
		for (Map.Entry<String, DataEntity> entry : entities.entrySet()) {
			String tableName = dataEntitiesConfig.getEntityTable(entry.getKey());
			if (tableName == null || !dataSources.contains(entry.getKey())) {
				continue;
			}
			String sourceIpField;
			try {
				sourceIpField = dataEntitiesConfig.getFieldColumn(entry.getKey(), SOURCE_IP_FIELD);
			} catch (InvalidQueryException ex) {
				continue;
			}
			tableToEntityIdAndIPField.put(tableName, new ImmutablePair<>(entry.getKey(), sourceIpField));
		}
	}

	private List<JSONObject> addRawEventsToLateralMovement(
			List<JSONObject> lateralMovementNotifications,
			Map<VPNSessionEvent, List<Map<String, Object>>> lateralMovementEvents) {

		lateralMovementNotifications.forEach(this::addVPNSessionEvents);
		for (JSONObject lateralMovementNotification : lateralMovementNotifications) {
			VPNSessionEvent vpnSessionEvent = new VPNSessionEvent(
					lateralMovementNotification.getAsString(normalizedUsernameField),
					lateralMovementNotification.getAsString(notificationStartTimestampField),
					lateralMovementNotification.getAsString(notificationEndTimestampField));
			List<Map<String, Object>> lateralMovementEventList = lateralMovementEvents.get(vpnSessionEvent);
			if (!CollectionUtils.isEmpty(lateralMovementEventList)) {
				for (Map<String, Object> lateralMovementEvent : lateralMovementEvents.get(vpnSessionEvent)) {
					addUserActivity(lateralMovementNotification,
							lateralMovementEvent.get(TABLE_NAME).toString(),
							lateralMovementEvent.get(DATASOURCE_USERNAME).toString(),
							lateralMovementEvent.get(DATASOURCE_IP).toString());
				}
			}
		}
		return lateralMovementNotifications;
	}

	private void addUserActivity(JSONObject lateralMovement, String tableName, String username, String ip) {
		//select * from tableName where username='#{username}' and ipField = ip and date_time_unix>=#{start_time}
		// and date_time_unix<=#{end_time}
		try {
			String entityId = tableToEntityIdAndIPField.get(tableName).getLeft();
			List<Term> conditions = new ArrayList<>();
			conditions.add(dataQueryHelper.createUserTerm(entityId, username));
			CustomedFilter filter = new CustomedFilter(SOURCE_IP_COLUMN, "equals", ip);
			conditions.add(dataQueryHelper.createCustomTerm(entityId, filter));
			conditions.add(dataQueryHelper.createDateRangeTermByOtherTimeField(entityId, EVENT_TIME_UTC,
					(Long)lateralMovement.get(notificationStartTimestampField),
					(Long)lateralMovement.get(notificationEndTimestampField)));
			DataQueryDTO dataQueryDTO = dataQueryHelper.createDataQuery(
					entityId, "*", conditions, new ArrayList<>(), -1, DataQueryDTOImpl.class);
			List<Map<String, Object>> results = runQuery(dataQueryDTO);
			if (results != null && results.size() > 0) {
				User user = userService.findByUsername(username);
				for (Map<String, Object> result : results) {
					if (user != null) {
						result.put(DISPLAY_NAME, user.getDisplayName());
						result.put(ENTITY_ID, user.getId());
					}
					result.put(DATA_SOURCE, entityId);
				}
				addSupportingInformation(
						lateralMovement, results, VpnLateralMovementSupportingInformation.USER_ACTIVITY_EVENTS);
			} else {
				if (results == null) {
					logger.error("Cannot build query for table " + tableName);
				} else {
					// Results empty
					logger.error("Query must return at least one value from " + tableName);
				}
			}
		} catch (Exception e) {
			logger.error("Can't handle user activity for " + tableName + "," + username + ", " + ip);
		}
	}

	private void addSupportingInformation(JSONObject lateralMovement, List<Map<String, Object>> results, String name) {
		if (CollectionUtils.isEmpty(results)) {
			return;
		}
		JSONObject supportingInformation;
		if (lateralMovement.containsKey(notificationSupportingInformationField)) {
			supportingInformation = (JSONObject)lateralMovement.get(notificationSupportingInformationField);
		} else {
			supportingInformation = new JSONObject();
		}
		supportingInformation.put(name, results);
		lateralMovement.put(notificationSupportingInformationField, supportingInformation);
		int numberOfEvents = 0;
		if (lateralMovement.containsKey(notificationNumOfEventsField)) {
			numberOfEvents = lateralMovement.getAsNumber(notificationNumOfEventsField).intValue();
		}
		numberOfEvents += results.size();
		lateralMovement.put(notificationNumOfEventsField, numberOfEvents);
	}

	private void addVPNSessionEvents(JSONObject lateralMovement) {
		//select * from vpnsessiondatares where username='#{username}' and date_time_unix>=#{start_time} and
		// date_time_unix<=#{end_time}
		List<Term> conditions = new ArrayList<>();
		conditions.add(dataQueryHelper.createUserTerm(
				dataEntity, lateralMovement.getAsString(normalizedUsernameField)));
		conditions.add(dataQueryHelper.createDateRangeTermByOtherTimeField(dataEntity, START_TIME_UTC,
				(Long)lateralMovement.get(notificationStartTimestampField),
				(Long)lateralMovement.get(notificationEndTimestampField)));
		DataQueryDTO dataQueryDTO = dataQueryHelper.createDataQuery(
				dataEntity, "*", conditions, new ArrayList<>(), -1, DataQueryDTOImpl.class);
		List<Map<String, Object>> results = runQuery(dataQueryDTO);
		if (results == null) {
			logger.debug("runQuery returned null. Changing results to empty list.");
			results = Collections.emptyList();
		}
		User user = userService.findByUsername(lateralMovement.getAsString(normalizedUsernameField));
		for (Map<String, Object> result : results) {
			if (user != null) {
				result.put(DISPLAY_NAME, user.getDisplayName());
				result.put(ENTITY_ID, user.getId());
			}
			result.put(DATE_TIME_UNIX, result.get(START_TIME_UTC));
		}
		addSupportingInformation(lateralMovement, results, VpnLateralMovementSupportingInformation.VPN_SESSION_EVENTS);
	}

	private List<Map<String, Object>> runQuery(DataQueryDTO dataQueryDTO) {
		DataQueryRunner dataQueryRunner;
		String rawEventsQuery;
		try {
			dataQueryRunner = dataQueryRunnerFactory.getDataQueryRunner(dataQueryDTO);
			rawEventsQuery = dataQueryRunner.generateQuery(dataQueryDTO);
			logger.info("Running the query: {}", rawEventsQuery);
		} catch (InvalidQueryException ex) {
			logger.debug("bad supporting information query: ", ex.getMessage());
			return null;
		}
		return dataQueryRunner.executeQuery(rawEventsQuery);
	}

	private void getLateralMovementEventsFromHDFS(
			Map<VPNSessionEvent, List<Map<String, Object>>> lateralMovementEvents, long upperLimitIncluding) {

		Instant lowerLimitInstInc = Instant.ofEpochSecond(latestTimestamp);
		Instant upperLimitInstInc = Instant.ofEpochSecond(upperLimitIncluding);
		logger.info("Processing {} from {} ({}) to {} ({})",
				SERVICE_NAME, lowerLimitInstInc, latestTimestamp, upperLimitInstInc, upperLimitIncluding);

		for (Map.Entry<String, Pair<String, String>> entry : tableToEntityIdAndIPField.entrySet()) {
			String tableName = entry.getKey();
			String ipField = entry.getValue().getRight();

			String t1Query = String.format(
					"select date_time, date_time_unix, normalized_username, hostname, source_ip, local_ip, duration " +
					"from vpnsessiondatares where %s and local_ip != ''",
					getEpochtimeBetweenCondition("vpnsessiondatares", lowerLimitInstInc, upperLimitInstInc));

			String t2Query = String.format(
					"select date_time_unix, normalized_username, %s from %s where %s",
					ipField, tableName, getEpochtimeBetweenCondition(tableName, lowerLimitInstInc, upperLimitInstInc));

			String query = String.format(
					"select distinct " +
					// Columns start
					"unix_timestamp(seconds_sub(t1.date_time, t1.duration)) %s, " +
					"t1.date_time_unix %s, " +
					"t1.normalized_username %s, " +
					"t2.normalized_username %s, " +
					"t1.source_ip vpn_source_ip, " +
					"t2.%s %s, " +
					"t1.hostname as hostname, " +
					"'%s' as %s " +
					// Columns end
					"from (%s) t1 inner join (%s) t2 " +
					"on t1.local_ip = t2.%s " +
					"and t2.date_time_unix between t1.date_time_unix - t1.duration and t1.date_time_unix " +
					"and t1.normalized_username != t2.normalized_username",
					VPN_START_TIME, VPN_END_TIME, VPN_USERNAME, DATASOURCE_USERNAME,
					ipField, DATASOURCE_IP, tableName, TABLE_NAME,
					t1Query, t2Query, ipField);

			List<Map<String, Object>> rows = queryRunner.executeQuery(query);
			if (!CollectionUtils.isEmpty(rows)) {
				for (Map<String, Object> row : rows) {
					VPNSessionEvent vpnSessionEvent = new VPNSessionEvent(
							row.get(VPN_USERNAME).toString(),
							row.get(VPN_START_TIME).toString(),
							row.get(VPN_END_TIME).toString());
					List<Map<String, Object>> lateralMovement = lateralMovementEvents.get(vpnSessionEvent);
					if (lateralMovement == null) {
						lateralMovement = new ArrayList<>();
					}
					lateralMovement.add(row);
					lateralMovementEvents.put(vpnSessionEvent, lateralMovement);
				}
			}
		}
	}

	private List<JSONObject> createLateralMovementsNotificationsFromImpalaRawEvents(
			Map<VPNSessionEvent, List<Map<String, Object>>> lateralMovementEvents) {

		// each map is a single event, each pair is column and value
		return lateralMovementEvents.keySet().stream().map(
				this::createLateralMovementNotificationFromLateralMovementQueryEvent).collect(Collectors.toList());
	}

	/**
	 * creates a lateral movement notification object from raw event returned from impala lateral movement query.
	 * lateral movement notification object - a json object to send to evidence creation task as notification.
	 */
	private JSONObject createLateralMovementNotificationFromLateralMovementQueryEvent(
			VPNSessionEvent lateralMovementEvent) {

		long startTime = Long.parseLong(lateralMovementEvent.startTime);
		long endTime = Long.parseLong(lateralMovementEvent.endTime);
		String normalizedUsername = lateralMovementEvent.normalizedUsername;
		return createNotification(
				startTime, endTime, normalizedUsername,
				NotificationAnomalyType.VPN_LATERAL_MOVEMENT.getType(), normalizedUsername);
	}

	private class VPNSessionEvent {
		public String normalizedUsername;
		public String startTime;
		public String endTime;

		public VPNSessionEvent(String normalizedUsername, String startTime, String endTime) {
			this.normalizedUsername = normalizedUsername;
			this.startTime = startTime;
			this.endTime = endTime;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (!VPNSessionEvent.class.isAssignableFrom(obj.getClass())) {
				return false;
			}
			final VPNSessionEvent other = (VPNSessionEvent)obj;
			return
					this.normalizedUsername.equals(other.normalizedUsername) &&
					this.startTime.equals(other.startTime) &&
					this.endTime.equals(other.endTime);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.normalizedUsername, this.startTime, this.endTime);
		}
	}
}
