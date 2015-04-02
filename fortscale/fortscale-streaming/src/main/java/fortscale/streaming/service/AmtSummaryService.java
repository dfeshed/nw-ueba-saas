package fortscale.streaming.service;

import fortscale.services.notifications.AmtActionToSensitiveAccountNotificationGenerator;
import fortscale.streaming.model.AmtSession;
import fortscale.utils.TimestampUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.apache.commons.lang3.StringUtils;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.core.env.Environment;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static fortscale.utils.ConversionUtils.*;

public class AmtSummaryService {
	private KeyValueStore<String, AmtSession> store;
	private String sessionNormalizedUsernameField;
	private String sessionUsernameField;
	private String sessionYidFieldName;
	private String sessionDateTimeUnixField;
	private String sessionYidVipFieldName;
	private String sessionHostnameField;
	private String sessionActionCodeField;
	private String sessionActionStringField;
	private String sessionIpField;
	private long numOfMillisecondsForClosingSession;
	private long maxSessionDuration;

	// Exclude sessions that don't contain YIDs
	private boolean excludeEmptySessions;

	// Exclude sessions with events matching the following regexp
	private Pattern excludeSessionsRegexp;
	private Set<String> sensitiveActionCodes;
	private Set<String> sensitiveActionCodesForNotification;
	private Set<String> sensitiveActionCodesRegExpForNotification;
	private Map<String, List<String>> failedActionCodes;

	// Session output field names
	private String outputStartTime;
	private String outputEndTime;
	private String outputTime;
	private String outputDuration;
	private String outputYidCount;
	private String outputUsername;
	private String outputNormalizedUsername;
	private String outputAvgYidCount;
	private String outputAvgTimeInYid;
	private String outputYidRate;
	private String outputYidVip;
	private String outputHostname;
	private String outputIpAddress;
	private String outputSensitiveActionsCount;
	private String outputFailedActionsCount;

	private AmtActionToSensitiveAccountNotificationGenerator amtActionToSensitiveAccountNotificationGenerator;

	// Constructor only for testing
	protected AmtSummaryService(Set<String> sensitiveActionCodes, Map<String, List<String>> failedActionCodes) {
		this.sensitiveActionCodes = sensitiveActionCodes;
		this.failedActionCodes = failedActionCodes;
	}

	public AmtSummaryService(KeyValueStore<String, AmtSession> store, long numOfMillisecondsForClosingSession,
			long maxSessionDuration, long staleYidSessionTimeoutMillis, boolean excludeEmptySessions,
			String excludeSessionsRegexp, Set<String> sensitiveActionCodes,
			Set<String> sensitiveActionCodesForNotification, Set<String> sensitiveActionCodesRegExpForNotification,
			AmtActionToSensitiveAccountNotificationGenerator amtActionToSensitiveAccountNotificationGenerator,
			Map<String, List<String>> failedActionCodes) {

		AmtSession.staleYidSessionTimeoutMillis = staleYidSessionTimeoutMillis;

		this.store = store;
		this.numOfMillisecondsForClosingSession = numOfMillisecondsForClosingSession;
		this.maxSessionDuration = maxSessionDuration * 60 * 60 * 1000;
		this.excludeEmptySessions = excludeEmptySessions;
		this.excludeSessionsRegexp = excludeSessionsRegexp != null ? Pattern.compile(excludeSessionsRegexp) : null;
		this.sensitiveActionCodes = sensitiveActionCodes;
		this.sensitiveActionCodesForNotification = sensitiveActionCodesForNotification;
		this.sensitiveActionCodesRegExpForNotification = sensitiveActionCodesRegExpForNotification;
		this.failedActionCodes = failedActionCodes;
		this.amtActionToSensitiveAccountNotificationGenerator = amtActionToSensitiveAccountNotificationGenerator;

		// Get incoming event field names from configuration
		Environment env = SpringService.getInstance().resolve(Environment.class);
		sessionDateTimeUnixField = env.getProperty("impala.data.amt.table.field.epochtime");
		sessionUsernameField = env.getProperty("impala.data.amt.table.field.username");
		sessionYidFieldName = env.getProperty("impala.data.amt.table.field.yid");
		sessionYidVipFieldName = env.getProperty("impala.data.amt.table.field.is_sensitive_machine");
		sessionHostnameField = env.getProperty("impala.data.amt.table.field.hostname");
		sessionActionCodeField = env.getProperty("impala.data.amt.table.field.action_code");
		sessionActionStringField = env.getProperty("impala.data.amt.table.field.action_string");
		sessionNormalizedUsernameField = env.getProperty("impala.data.amt.table.field.normalized_username");
		sessionIpField = env.getProperty("impala.data.amt.table.field.source_ip");

		// Get session output field names from configuration
		outputStartTime = env.getProperty("impala.sessiondata.amt.table.field.start_time");
		outputEndTime = env.getProperty("impala.sessiondata.amt.table.field.end_time");
		outputTime = env.getProperty("impala.sessiondata.amt.table.field.date_time_unix");
		outputDuration = env.getProperty("impala.sessiondata.amt.table.field.duration");
		outputYidCount = env.getProperty("impala.sessiondata.amt.table.field.distinct_yid_count");
		outputUsername = env.getProperty("impala.sessiondata.amt.table.field.username");
		outputNormalizedUsername = env.getProperty("impala.sessiondata.amt.table.field.normalized_username");
		outputAvgYidCount = env.getProperty("impala.sessiondata.amt.table.field.avg_yid_counts");
		outputAvgTimeInYid = env.getProperty("impala.sessiondata.amt.table.field.avg_time_in_yid");
		outputYidRate = env.getProperty("impala.sessiondata.amt.table.field.yid_rate");
		outputYidVip = env.getProperty("impala.sessiondata.amt.table.field.vip_yid");
		outputHostname = env.getProperty("impala.sessiondata.amt.table.field.amt_host");
		outputIpAddress = env.getProperty("impala.sessiondata.amt.table.field.source_ip");
		outputSensitiveActionsCount = env.getProperty("impala.sessiondata.amt.table.field.sensitive_action_count");
		outputFailedActionsCount = env.getProperty("impala.sessiondata.amt.table.field.failed_action_count");
	}

	/**
	 * Service method - Responsible for handling a given account with some session logic:
	 * 1. Determine the session's state (start / close / new session / etc.)
	 * 2. Decide if there's a need to send an 'Action To Sensitive Account' notification
	 *
	 * @param msg - The message from the Kafka topic
	 * @return - The closed session event parsed from Json to string, that will move to the score topic
	 * @throws Exception
	 */
	public String handleEvent(JSONObject msg) throws Exception {
		// Get fields from message
		Long timestamp = TimestampUtils.convertToSeconds(convertToLong(msg.get(sessionDateTimeUnixField)));
		String yid = convertToString(msg.get(sessionYidFieldName));
		String username = convertToString(msg.get(sessionUsernameField));
		String normalizedUsername = convertToString(msg.get(sessionNormalizedUsernameField));
		Boolean hasVipYid = convertToBoolean(msg.get(sessionYidVipFieldName));
		String actionCode = convertToString(msg.get(sessionActionCodeField));
		String actionString = convertToString(msg.get(sessionActionStringField));
		String hostname = convertToString(msg.get(sessionHostnameField));
		String ip = convertToString(msg.get(sessionIpField));

		// Returned closed session event to send out
		String closedSessionEvent = null;

		AmtSession session = store.get(username);
		// In case the user doesn't have a session (it's the first
		// session or the first session for the user from deletion)
		if (session == null) {
			// Create a new session
			session = new AmtSession(username, normalizedUsername, timestamp, yid, 0, 0.0);
		} else {
			// Skip events with older timestamp than the
			// current stored session for the user (barrier check)
			if (timestamp < session.getEndTimeUnix())
				return null;

			// In case the existing session is marked as closed,
			// treat the current event as the beginning of a new session
			if (session.isClosed()) {
				session = new AmtSession(username, normalizedUsername, timestamp, yid, session.getSessionsCount(), session.getAverageYids());
			} else {
				// Check the time difference between the current session and the stored session
				if ((timestamp - session.getEndTimeUnix()) * 1000 > numOfMillisecondsForClosingSession ||
					(timestamp - session.getStartTimeUnix()) * 1000 > maxSessionDuration) {
					// Consider this a new session, so report the
					// saved session as closed and create a new session
					session.closeSession();
					closedSessionEvent = getSessionJSON(session);
					session = new AmtSession(username, normalizedUsername, timestamp, yid, session.getSessionsCount(), session.getAverageYids());
				} else {
					// Amend the close time for the saved session
					session.addYid(yid, timestamp);
				}
			}
		}

		// Update session values
		updateSessionValues(session, actionCode, actionString, hostname, ip, timestamp, hasVipYid);
		// Update session in store
		store.put(username, session);
		// Check event Action Vs Sensitivity (in case we need to send a notification)
		checkActionVsAccount(msg, actionCode, hasVipYid, normalizedUsername, yid, timestamp);
		return closedSessionEvent;
	}

	private String getSessionJSON(AmtSession session) {
		JSONObject json = new JSONObject();

		// Skip sessions that did not work on any YID
		if (excludeEmptySessions && (session.getYidCount() == 0 || session.getDuration() == 0.0) && !session.isHasRealActions())
			return null;

		json.put(outputStartTime, session.getStartDate());
		json.put(outputEndTime, session.getEndDate());
		json.put(outputTime, session.getEndTimeUnix());
		json.put(outputDuration, session.getDuration());
		json.put(outputYidCount, session.getYidCount());
		json.put(outputUsername, session.getUsername());
		json.put(outputNormalizedUsername, session.getNormalizedUsername());
		json.put(outputAvgYidCount, session.getAverageYids());
		json.put(outputAvgTimeInYid, session.getAverageTimeInYid());
		json.put(outputYidRate, session.getYidRate());
		json.put(outputYidVip, session.isVipYid());
		json.put(outputHostname, session.getDominantHostname());
		json.put(outputSensitiveActionsCount, session.getActionTypeCount(AmtSession.ActionType.Sensitive));
		json.put(outputFailedActionsCount, session.getActionTypeCount(AmtSession.ActionType.Failed));
		json.put(outputIpAddress, session.getDominantIp());

		return json.toJSONString(JSONStyle.NO_COMPRESS);
	}

	protected void updateSessionValues(AmtSession session, String actionCode, String actionString, String hostname, String ip, Long timestamp, Boolean hasVipYid) {
		incSensitiveActionCode(session, actionCode);
		incFailedActionCode(session, actionCode, actionString);
		session.addHostname(hostname);
		session.addIpAddress(ip);
		session.setEndTimeUnix(timestamp);
		markSessionWithRealActions(session, actionCode);

		if (hasVipYid)
			session.markVipYid();
	}

	private void checkActionVsAccount(JSONObject msg, String actionCode, boolean hasVipYid, String normalizedUsername, String yid, long dateTimeUnix) throws Exception {
		// In case we need to add a new notification to action on sensitive account
		if (hasVipYid) {
			for (String actionCodeFromList : sensitiveActionCodesForNotification) {
				if (actionCodeFromList.equals(actionCode)) {
					amtActionToSensitiveAccountNotificationGenerator.createNotifications(msg, normalizedUsername, yid, dateTimeUnix);
					break;
				}
			}

			// In case of regex matching from regexp list
			for (String regexp : sensitiveActionCodesRegExpForNotification) {
				if (actionCode.matches(regexp)) {
					amtActionToSensitiveAccountNotificationGenerator.createNotifications(msg, normalizedUsername, yid, dateTimeUnix);
					break;
				}
			}
		}
	}

	protected void incSensitiveActionCode(AmtSession session, String actionCode) {
		if (StringUtils.isNotEmpty(actionCode)) {
			// Check if action code is in the set of sensitive action codes
			if (sensitiveActionCodes.contains(actionCode.toUpperCase()))
				session.incActionTypeCount(AmtSession.ActionType.Sensitive);
		}
	}

	protected void incFailedActionCode(AmtSession session, String actionCode, String actionString) {
		if (StringUtils.isNotEmpty(actionCode)) {
			// Check if action code is in the map of failed action codes and the action string is in the matching string set
			if (failedActionCodes.containsKey(actionCode.toUpperCase()) && failedActionCodes.get(actionCode.toUpperCase()).contains(actionString))
				session.incActionTypeCount(AmtSession.ActionType.Failed);
		}
	}

	/**
	 * Check if the action is a meaningful action
	 * (not only markup for session-open, etc.)
	 * and if so mark the session
	 *
	 * @param session    The session to mark
	 * @param actionCode The action
	 */
	private void markSessionWithRealActions(AmtSession session, String actionCode) {
		if (StringUtils.isNotEmpty(actionCode) && !session.isHasRealActions()) {
			if (excludeSessionsRegexp == null || !excludeSessionsRegexp.matcher(actionCode).matches()) {
				session.setHasRealActions(true);
			}
		}
	}

	public List<String> findClosedSessions() {
		long currentTime = System.currentTimeMillis();
		List<AmtSession> closedSessions = new LinkedList<>();

		// Go over the sessions stored and see if we have an
		// outstanding session that should be reported as closed
		KeyValueIterator<String, AmtSession> iterator = store.all();
		List<String> closedSessionsEvents = new LinkedList<>();

		try {
			while (iterator.hasNext()) {
				Entry<String, AmtSession> entry = iterator.next();
				AmtSession session = entry.getValue();

				if (currentTime - session.getLastUpdated() > numOfMillisecondsForClosingSession) {
					// Report session has been closed
					closedSessions.add(session);
				}
			}
		} finally {
			if (iterator != null)
				iterator.close();
		}

		// Go over all found sessions, update them in the store and report them back
		for (AmtSession session : closedSessions) {
			session.closeSession();
			store.put(session.getUsername(), session);

			String sessionJson = getSessionJSON(session);
			if (sessionJson != null)
				closedSessionsEvents.add(sessionJson);
		}

		return closedSessionsEvents;
	}
}
