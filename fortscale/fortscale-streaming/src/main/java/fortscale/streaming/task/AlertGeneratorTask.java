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
	/**
	 * Threshold for creating evidences
	 */
	protected int scoreThreshold;
	protected AlertsService alertsService;

	@Override protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector,
			TaskCoordinator coordinator) throws Exception {
		// parse the message into json
		String messageText = (String) envelope.getMessage();
		net.minidev.json.JSONObject message = (net.minidev.json.JSONObject) JSONValue.parseWithException(messageText);

		//send evidence to Esper
		Evidence evidence = mapper.readValue(message.toJSONString(), Evidence.class);
		epService.getEPRuntime().sendEvent(evidence);

	}

	@Override protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
	}

	@Override protected void wrappedInit(Config config, TaskContext context) throws Exception {
		Configuration esperConfig = new Configuration();

		alertsService = SpringService.getInstance().resolve(AlertsService.class);

		// get the timestamp field
		timestampField = getConfigString(config, "fortscale.timestamp.field");
		// get the username field
		usernameField = getConfigString(config, "fortscale.events.normalizedusername.field");
		// get the score field
		scoreField = getConfigString(config, "fortscale.events.score.field");
		// get the threshold for creating evidences
		scoreThreshold = config.getInt("fortscale.score.threshold");

		esperConfig.addEventTypeAutoName("fortscale.domain.core");
		epService = EPServiceProviderManager.getDefaultProvider(esperConfig);
		epService.getEPAdministrator().createEPL("insert into EvidenceStream select * from Evidence.win:time_batch(30 sec) order by startDate");


		//subscribe Alert creation Esper class
		MonitorAlertSubscriber monitorAlertSubscriber = new MonitorAlertSubscriber(epService, alertsService);

	}

	@Override protected void wrappedClose() throws Exception {
	}

}
