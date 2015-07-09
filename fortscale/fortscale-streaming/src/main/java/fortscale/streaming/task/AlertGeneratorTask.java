package fortscale.streaming.task;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.Alert;
import fortscale.services.AlertsService;
import fortscale.streaming.alert.subscribers.AlertSubscriber;
import fortscale.streaming.service.SpringService;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static fortscale.streaming.ConfigUtils.getConfigString;

/**
 * Created by danal on 16/06/2015.
 */
public class AlertGeneratorTask extends AbstractStreamTask{

	private static Logger logger = LoggerFactory.getLogger(AlertGeneratorTask.class);

	List<EPStatement> epsStatements = new ArrayList<>();
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
	 * Alerts service (for Mongo export)
	 */
	protected AlertsService alertsService;
	/**
	 * Threshold for creating alerts
	 */
	protected int scoreThreshold;


	@Override protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector,
			TaskCoordinator coordinator) throws Exception {
		// parse the message into json
		String messageText = (String) envelope.getMessage();
		try {
			JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);

			//send evidence to Esper
			Evidence evidence = mapper.readValue(message.toJSONString(), Evidence.class);
			epService.getEPRuntime().sendEvent(evidence);

		} catch (Exception ex){
			logger.error("error parsing: " + messageText);
		}

	}

	@Override protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
	}

	@Override protected void wrappedInit(Config config, TaskContext context)  {
		alertsService = SpringService.getInstance().resolve(AlertsService.class);

		// creating the esper configuration
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


		// semi ordering preparation step for incoming evidence.
		// the ordering is preform in batches of 10 min and is output to a new stream called SemiOrderEvidenceBach - for the use of any other EPL
		// the assumption that the collector will work in a manner where all event from all data sources for a given entity and time range will arrive to the streaming in a max delay of 10 min.
		epService.getEPAdministrator().createEPL("insert into SemiOrderEvidenceBach select * from Evidence.win:time_batch(10 min) order by " + Evidence.startDateField);

		//subscribe instances of Esper EPL statements
		Config fieldsSubset = config.subset("fortscale.esper.rule.subscriber.");
		for (String dataSource : fieldsSubset.keySet()) {
			String className = getConfigString(config, String.format("fortscale.esper.rule.subscriber.%s", dataSource));
			String ruleName = getConfigString(config, String.format("fortscale.esper.rule.name.%s", dataSource));
			String statement = getConfigString(config, String.format("fortscale.esper.rule.statement.%s", dataSource));
			//Create the Esper alert statement object
			EPStatement esperEventStatement = epService.getEPAdministrator().createEPL(statement);
			try {
				//Generate Subscriber class by reflection
				Class<?> clazz = Class.forName(className);
				AlertSubscriber alertSubscriber = (AlertSubscriber) clazz.newInstance();
				//inits the subscriber with the alert Mongo service and the rule name
				alertSubscriber.init(alertsService, ruleName);
				//subscribe Alert creation class to Esper EPL statement
				esperEventStatement.setSubscriber(alertSubscriber);

				epsStatements.add(esperEventStatement);
			} catch (ClassNotFoundException ex){
				logger.error("Cannot find class " + className, ex);
			} catch (InstantiationException ex){
				logger.error("Cannot instantiate class " + className, ex);
			} catch (IllegalAccessException ex){
				logger.error("Cannot access constructor for class "+ className, ex);
			}
		}

	}

	@Override protected void wrappedClose() throws Exception {
		for (EPStatement esperEventStatement : epsStatements) {
			esperEventStatement.destroy();
		}
	}

}
