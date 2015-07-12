package fortscale.streaming.task;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.Evidence;
import fortscale.services.AlertsService;
import fortscale.streaming.alert.subscribers.BasicAlertSubscriber;
import fortscale.streaming.service.SpringService;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

/**
 * Created by danal on 16/06/2015.
 */
public class AlertGeneratorTask extends AbstractStreamTask{

	private static Logger logger = LoggerFactory.getLogger(AlertGeneratorTask.class);
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
		net.minidev.json.JSONObject message = (net.minidev.json.JSONObject) JSONValue.parseWithException(messageText);

		//send evidence to Esper
		Evidence evidence = mapper.readValue(message.toJSONString(), Evidence.class);
		epService.getEPRuntime().sendEvent(evidence);

	}

	@Override protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
	}

	@Override protected void wrappedInit(Config config, TaskContext context) throws Exception {
		alertsService = SpringService.getInstance().resolve(AlertsService.class);

		// creating the esper configuration
		Configuration esperConfig = new Configuration();

		// define package for Esper event type, each new event type should be part of this package
		esperConfig.addEventTypeAutoName("fortscale.domain.core");

		// define a Esper custom view - use for filtering out of order events from calender pre defined  windows
		esperConfig.addPlugInView("fortscale","ext_timed_batch","fortscale.streaming.alert.plugins.ExternallyTimedBatchViewFortscaleFactory");

		// creating the Esper service
		epService = EPServiceProviderManager.getDefaultProvider(esperConfig);


		// semi ordering preparation step for incoming evidence.
		// the ordering is preform in batches of 10 min and is output to a new stream called SemiOrderEvidenceBach - for the use of any other EPL
		// the assumption that the collector will work in a manner where all event from all data sources for a given entity and time range will arrive to the streaming in a max delay of 10 min.
		epService.getEPAdministrator().createEPL("insert into SemiOrderEvidenceBach select * from Evidence.win:time_batch(10 min) order by " + Evidence.startDateField);

		// basic Suspicious hourly activity Alert EPL (evidence count > 3)
		// group evidence according to entity type and name, for each specific entity create hourly windows.
		// the view assume event are order (but filter out event arriving in delay and are older than the current window time).
		// the window is closed once an event newer than the window arrive.
		//this statement output the result for each window in a batch, each row contain a different evidence id, but all the other parameters (score, time, entity name and type) are identical for all the records.
		EPStatement criticalEventStatement = epService.getEPAdministrator().createEPL("select distinct id,'Suspicious hourly activity' as title,"+Evidence.entityTypeField+","+Evidence.entityNameField+",min("+Evidence.startDateField+") as startDate,max("+Evidence.endDateField+") as endDate,avg("+Evidence.scoreField+") as score from SemiOrderEvidenceBach.std:groupwin("+Evidence.entityTypeField+","+Evidence.entityNameField+").fortscale:ext_timed_batch(startDate, 1 hour, 0L) group by "+Evidence.entityTypeField+","+Evidence.entityNameField+" having count(*) > 3");

		//subscribe Basic Alert creation class to Esper EPL statement
		criticalEventStatement.setSubscriber(new BasicAlertSubscriber(alertsService));

	}

	@Override protected void wrappedClose() throws Exception {
	}

}
