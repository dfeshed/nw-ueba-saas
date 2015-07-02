package fortscale.streaming.task;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.Alert;
import fortscale.services.AlertsService;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.service.SpringService;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import static fortscale.streaming.ConfigUtils.getConfigString;

/**
 * Created by danal on 16/06/2015.
 */
public class AlertGeneratorTask extends AbstractStreamTask{

	private static Logger logger = LoggerFactory.getLogger(EvidenceCreationTask.class);
	/**
	 * Esper service provider
	 */
	private EPServiceProvider epService;
	/**
	 * JSON serializer
	 */
	protected ObjectMapper mapper = new ObjectMapper();
	/**
	 * The time field in the input event
	 */
	protected String timestampField;
	/**
	 * The username field in the input event
	 */
	protected String usernameField;
	/**
	 * The score field in the input event
	 */
	protected String scoreField;
	/**
	 * Evidences service (for Mongo export)
	 */
	protected AlertsService alertsService;
	/**
	 * The level DB store name
	 */
	private static final String storeName = "alerts";
	/**
	 * The level DB store: ID to evidence
	 */
	protected KeyValueStore<String, Alert> store;


	@Override protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector,
			TaskCoordinator coordinator) throws Exception {
		// parse the message into json
		String messageText = (String) envelope.getMessage();
		net.minidev.json.JSONObject message = (net.minidev.json.JSONObject) JSONValue.parseWithException(messageText);

		// create alert

		// get the timestamp from the event
//		Long timestampSeconds = convertToLong(validateFieldExistsAndGetValue(message, messageText, timestampField));
//		Long timestamp = TimestampUtils.convertToMilliSeconds(timestampSeconds);

		// get the username from the event
//		String normalizedUsername = convertToString(validateFieldExistsAndGetValue(message, messageText, usernameField));
//		Integer score = convertToInteger(validateFieldExistsAndGetValue(message, messageText, scoreField));

		// Create Alert from event
//		AlertEvent alertEvent = new AlertEvent(alertsService.createTransientAlert(EntityType.User, normalizedUsername,
//				new Date(timestamp), "", null, "", score, AlertStatus.Unread, ""));

		//send the evidence to Esper to process
		Evidence evidence = mapper.readValue(message.toJSONString(), Evidence.class);
		epService.getEPRuntime().sendEvent(evidence);
		// Save evidence to levelDB
//		store.put(alert.getId(), alert);

	}

	Long prevStartTime = 0l;
	private int eventId = 0;
	boolean startWasSend = false ;
	boolean endWasSend = true;
	int iteration = 0;
	int sessionId = 0;

	@Override protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		/*epService.getEPRuntime().sendEvent(new TemperatureEvent(new Random().nextInt(500), new Date().getTime()+new Random().nextInt(100000),++eventId));
		iteration++;
		if(iteration%300 == 0) {
			if (endWasSend) {
				boolean sendStart = new Random().nextBoolean();
				if (sendStart) {
					prevStartTime = new Date().getTime();
					startWasSend = true;
					endWasSend = false;
					MonitorEventSubscriber3 monitorEventSubscriber3 = new MonitorEventSubscriber3(epService,prevStartTime, sessionId);
					System.out.println("started session " + sessionId + " on" + new Date(prevStartTime));
				}
			}
		}
		if(iteration%700 == 0) {
			if (startWasSend) {
				boolean sendEnd = new Random().nextBoolean();
				if (sendEnd) {
					Long endTime = prevStartTime + new Random().nextInt(300000);
					epService.getEPRuntime().sendEvent(new TimestampEnd(endTime, sessionId));
					endWasSend = true;
					startWasSend = false;
					System.out.println("end session " + sessionId + "  on" + new Date(endTime));
					sessionId++;
				}

			}
		}*/
	}

	@Override protected void wrappedInit(Config config, TaskContext context) throws Exception {
		Configuration esperConfig = new Configuration();

		// Get the levelDB store
		store = (KeyValueStore<String, Alert>) context.getStore(storeName);

		alertsService = SpringService.getInstance().resolve(AlertsService.class);

		// get the timestamp field
		timestampField = getConfigString(config, "fortscale.timestamp.field");
		// get the username field
		usernameField = getConfigString(config, "fortscale.events.normalizedusername.field");
		// get the score field
		scoreField = getConfigString(config, "fortscale.events.score.field");

//		esperConfig.addEventTypeAutoName("fortscale.streaming.task.messages");
		esperConfig.addEventTypeAutoName("fortscale.domain.core");
		epService = EPServiceProviderManager.getDefaultProvider(esperConfig);
		epService.getEPAdministrator().createEPL("insert into EvidenceStream select * from Evidence.win:time_batch(30 sec) order by startDate");


//		MonitorEventSubscriber monitorEventSubscriber = new MonitorEventSubscriber(epService);
//		MonitorEventSubscriber2 monitorEventSubscriber2 = new MonitorEventSubscriber2(epService);
		//subscribe Alert creation Esper class
		MonitorAlertSubscriber monitorAlertSubscriber = new MonitorAlertSubscriber(epService, store, alertsService);
		//example of pattern currently not active
		//MonitorEventSubscriber4 monitorEventSubscriber4 = new MonitorEventSubscriber4(epService);

	}

	@Override protected void wrappedClose() throws Exception {

	}

	/**
	 * Validate that the expected field has value in the message JSON and return the value
	 *
	 * @param message        The message JSON
	 * @param messageText    The message JSON as string
	 * @param field    The requested field
	 * @return The value of the field
	 * @throws fortscale.streaming.exceptions.StreamMessageNotContainFieldException in case the field doesn't exist in the JSON
	 */
	private Object validateFieldExistsAndGetValue(JSONObject message, String messageText, String field) throws StreamMessageNotContainFieldException {
		Object value = message.get(field);
		if (value == null) {
			logger.error("message {} does not contains value in field {}", messageText, field);
			throw new StreamMessageNotContainFieldException(messageText, field);
		}
		return value;
	}

}
