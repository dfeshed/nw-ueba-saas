package fortscale.streaming.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.services.notifications.AmtActionToSensitiveAccountNotificationGenerator;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.model.AmtSession;
import fortscale.streaming.service.AmtSummaryService;
import fortscale.streaming.service.SpringService;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fortscale.streaming.ConfigUtils.getConfigString;

public class AMTSessionizeStreamTask extends AbstractStreamTask {
	private String outputTopic;
	private AmtSummaryService amtSummaryService;

	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// Parse the incoming message and get the required fields from it
		String messageText = (String) envelope.getMessage();
		JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);

		String closedSessionEvent = amtSummaryService.handleEvent(message);
		if (closedSessionEvent != null)
			outputClosedSessionEvent(collector, closedSessionEvent);
	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		List<String> closedSessions = amtSummaryService.findClosedSessions();
		for (String closedSession : closedSessions)
			outputClosedSessionEvent(collector, closedSession);
	}

	private void outputClosedSessionEvent(MessageCollector collector, String closedSessionEvent) throws Exception {
		// Output the closed session event
		try {
			collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), closedSessionEvent));
		} catch (Exception e) {
			throw new KafkaPublisherException(String.format("Failed to send closed AMT session message %s", closedSessionEvent), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		outputTopic = config.get("fortscale.output.topic", "");

		// Create summary service for session data service
		KeyValueStore<String, AmtSession> sessionStore = (KeyValueStore<String, AmtSession>) context.getStore(getConfigString(config, "fortscale.session.store.name"));
		long numOfMillisecondsForClosingSession = config.getLong("fortscale.session.numOfMillisecondsForClosingSession");
		long maxSessionDuration = config.getLong("fortscale.session.maxSessionDuration");
		long staleYidSessionTimeoutMillis = config.getLong("fortscale.session.staleYidSessionTimeoutMillis");
		boolean excludeEmptySessions = config.getBoolean("fortscale.session.excludeEmpty");
		String excludeSessionsRegexp = config.get("fortscale.session.excludeSessionWithOnlyEvent.regexp", null);
		String failedActionCodesJSON = config.get("fortscale.session.failed.action.codes", "");
		Set<String> sensitiveActionCodes = new HashSet<>(config.getList("fortscale.session.sensitive.action.codes"));
		Set<String> sensitiveActionCodesForNotification = new HashSet<>(config.getList("fortscale.session.sensitive.notification.action.codes"));
		Set<String> sensitiveActionCodesRegExpForNotification = new HashSet<>(config.getList("fortscale.session.sensitive.notification.action.code.regexp"));

		AmtActionToSensitiveAccountNotificationGenerator amtActionToSensitiveAccountNotificationGenerator =
			SpringService.getInstance().resolve(AmtActionToSensitiveAccountNotificationGenerator.class);

		ObjectMapper mapper = new ObjectMapper();
		Map<String, List<String>> failedActionCodes = mapper.readValue(failedActionCodesJSON, Map.class);

		amtSummaryService = new AmtSummaryService(sessionStore, numOfMillisecondsForClosingSession, maxSessionDuration,
			staleYidSessionTimeoutMillis, excludeEmptySessions, excludeSessionsRegexp, sensitiveActionCodes,
			sensitiveActionCodesForNotification, sensitiveActionCodesRegExpForNotification,
			amtActionToSensitiveAccountNotificationGenerator, failedActionCodes);
	}

	@Override
	protected void wrappedClose() throws Exception {}
}
