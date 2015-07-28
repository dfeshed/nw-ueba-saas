package fortscale.streaming.task;

import static fortscale.utils.ConversionUtils.convertToString;

import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.ClosableTask;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

import com.google.common.collect.Iterables;

import fortscale.aggregation.feature.event.AggrFeatureEventBuilder;
import fortscale.streaming.service.EventsPrevalenceModelStreamTaskManager;
import fortscale.utils.StringPredicates;

public class AggrFeatureEventsPrevalenceModelStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {

//	private static final Logger logger = LoggerFactory.getLogger(EventsPrevalenceModelStreamTask.class);
	
	private Map<String, EventsPrevalenceModelStreamTaskManager> featureToEventsPrevalenceModelStreamTaskManagerMap = new HashMap<String, EventsPrevalenceModelStreamTaskManager>();
	
	
	
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {		
		// Get configuration properties
		Config fieldsSubset = config.subset("fortscale.");
		for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(".fortscale.scorers"))) {
			String featureName = fieldConfigKey.substring(0, fieldConfigKey.indexOf(".fortscale.scorers"));
			Config featureEventConfig = fieldsSubset.subset(String.format("%s.", featureName));
			EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager = new EventsPrevalenceModelStreamTaskManager(featureEventConfig, context);
			featureToEventsPrevalenceModelStreamTaskManagerMap.put(featureName, eventsPrevalenceModelStreamTaskManager);
		}

	}
		
	/** Process incoming events and update the user models stats */
	@Override public void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		String messageText = (String)envelope.getMessage();
		JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);
		
		// get the timestamp from the message
		String bucketConfName = convertToString(message.get(AggrFeatureEventBuilder.EVENT_FIELD_BUCKET_CONF_NAME));
		//TODO: get the field name for featureName also from AggrFeatureEventBuilder
		String featureName = convertToString(message.get("name"));
		String fullPathFeatureName = String.format("%s.%s", bucketConfName, featureName);
		EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager = featureToEventsPrevalenceModelStreamTaskManagerMap.get(fullPathFeatureName);
		if(eventsPrevalenceModelStreamTaskManager != null){
			eventsPrevalenceModelStreamTaskManager.process(envelope, collector, coordinator);
		}
	}

	
	
	/** periodically save the state to mongodb as a secondary backing store */
	@Override public void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) {
		for(EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager: featureToEventsPrevalenceModelStreamTaskManagerMap.values()){
			eventsPrevalenceModelStreamTaskManager.window(collector, coordinator);
		}
	}

	/** save the state to mongodb when the job shutsdown */
	@Override protected void wrappedClose() throws Exception {
		for(EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager: featureToEventsPrevalenceModelStreamTaskManagerMap.values()){
			eventsPrevalenceModelStreamTaskManager.close();
		}
		featureToEventsPrevalenceModelStreamTaskManagerMap.clear();
	}
}
