package fortscale.streaming.task;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.Evidence;
import fortscale.services.AlertsService;
import fortscale.streaming.alert.RuleConfig;
import fortscale.streaming.alert.subscribers.AlertSubscriber;
import fortscale.streaming.service.SpringService;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigString;

/**
 * Created by danal on 16/06/2015.
 */
public class AlertGeneratorTask extends AbstractStreamTask{

	private static Logger logger = LoggerFactory.getLogger(AlertGeneratorTask.class);

	List<EPStatement> epsStatements = new ArrayList<>();

	Map<String,RuleConfig> rulesConfiguration = new HashMap<>();
	/**
	 * Esper service provider
	 */
	private EPServiceProvider epService;
	/**
	 * JSON serializer
	 */
	protected ObjectMapper mapper = new ObjectMapper();
	/**
	 * Alerts service (for Mongo export)
	 */
	protected AlertsService alertsService;


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
			logger.error("error parsing: " + messageText, ex);
		}

	}

	@Override protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
	}

	@Override protected void wrappedInit(Config config, TaskContext context) {
		alertsService = SpringService.getInstance().resolve(AlertsService.class);

		// creating the esper configuration
		Configuration esperConfig = new Configuration();

		// define package for Esper event type, each new event type should be part of this package
		esperConfig.addEventTypeAutoName("fortscale.domain.core");

		// define a Esper custom view - use for filtering out of order events from calender pre defined  windows
		esperConfig.addPlugInView("fortscale", "ext_timed_batch", "fortscale.streaming.alert.plugins.ExternallyTimedBatchViewFortscaleFactory");

		// creating the Esper service
		epService = EPServiceProviderManager.getDefaultProvider(esperConfig);

		//subscribe instances of Esper EPL statements
		Config fieldsSubset = config.subset("fortscale.esper.rule.name.");
		for (String rule : fieldsSubset.keySet()) {
			String ruleName = getConfigString(config, String.format("fortscale.esper.rule.name.%s", rule));
			String statement = getConfigString(config, String.format("fortscale.esper.rule.statement.%s", rule));
			String subscriberBeanName = getConfigString(config, String.format("fortscale.esper.rule.subscriberBean.%s", rule));
			boolean autoCreate = config.getBoolean(String.format("fortscale.esper.rule.auto-create.%s", rule));
			RuleConfig ruleConfig = new RuleConfig(ruleName, statement, autoCreate, subscriberBeanName);
			rulesConfiguration.put(ruleName, ruleConfig);
			if(autoCreate) {
				createStatement(ruleConfig);
			}
		}
	}


	@Override protected void wrappedClose() throws Exception {
		for (EPStatement esperEventStatement : epsStatements) {
			esperEventStatement.destroy();
		}
	}

	/**
	 * create esper statement according to given rule configuration and set a subscriber to that statement
	 */
	private void createStatement(RuleConfig ruleConfig){
		//Create the Esper alert statement object
		EPStatement esperEventStatement = epService.getEPAdministrator().createEPL(ruleConfig.getStatement());
		//Generate Subscriber from spring
		if (!ruleConfig.getSubscriberBeanName().equals("none")) {
			AlertSubscriber alertSubscriber = (AlertSubscriber) SpringService.getInstance().resolve(ruleConfig.getSubscriberBeanName());
			//inits the subscriber with the alert Mongo service and the rule name
			alertSubscriber.init(alertsService);
			//subscribe Alert creation class to Esper EPL statement
			esperEventStatement.setSubscriber(alertSubscriber);
		}
		epsStatements.add(esperEventStatement);
	}

}
