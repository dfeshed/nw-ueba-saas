package fortscale.streaming.task;

import static fortscale.streaming.ConfigUtils.getConfigStringList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.config.MapConfig;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.ClosableTask;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

import fortscale.streaming.service.EventsPrevalenceModelStreamTaskManager;
import fortscale.streaming.service.FortscaleStringValueResolver;
import fortscale.streaming.service.SpringService;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class MultipleEventsPrevalenceModelStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private static final String FORTSCALE_EVENTS_PREVALENCE_STRAM_MANATGERS_DATA_SOURCES_PROPERTY_NAME = "fortscale.events-prevalence-stream-managers.data-sources";
	private static final String DATA_SOURCE_FIELD_NAME_PROPERTY = "${streaming.event.datasource.field.name}";

	private static final Logger logger = Logger.getLogger(EventsPrevalenceModelStreamTask.class);


	private Map<String, EventsPrevalenceModelStreamTaskManager> dataSourceToEventsPrevalenceModelStreamTaskManagerMap = new HashMap<String, EventsPrevalenceModelStreamTaskManager>();
	private Map<String, String> topicToDataSourceMap = new HashMap<>();
	private String dataSourceFieldName;


	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		FortscaleStringValueResolver fortscaleStringValueResolver = SpringService.getInstance().resolve(FortscaleStringValueResolver.class);

		dataSourceFieldName = fortscaleStringValueResolver.resolveStringValue(DATA_SOURCE_FIELD_NAME_PROPERTY);

		// Get configuration properties
		List<String> dataSources = getConfigStringList(config,FORTSCALE_EVENTS_PREVALENCE_STRAM_MANATGERS_DATA_SOURCES_PROPERTY_NAME);

		Config fieldsSubset = config.subset("fortscale.");
		for (String dataSource : dataSources) {
			Config dataSourceConfig = fieldsSubset.subset(String.format("%s.", dataSource));
			String dataSourceInputTopic = dataSourceConfig.get("input.topic");
			if(StringUtils.isNotBlank(dataSourceInputTopic)){
				topicToDataSourceMap.put(dataSourceInputTopic, dataSource);
			}
			dataSourceConfig = addPrefixToConfigEntries(dataSourceConfig, "fortscale.");
			EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager = new EventsPrevalenceModelStreamTaskManager(dataSourceConfig, context);
			dataSourceToEventsPrevalenceModelStreamTaskManagerMap.put(dataSource, eventsPrevalenceModelStreamTaskManager);
		}

	}
	

	private Config addPrefixToConfigEntries(Config config, String prefix) {
		Map<String, String> newConfigMap = new HashMap<>();
		if(config!=null && StringUtils.isNotBlank(prefix)) {
			for (String oldKey : config.keySet()) {
				String value = config.get(oldKey);
				String newKey = String.format("%s%s", prefix, oldKey);
				newConfigMap.put(newKey, value);
			}
		}
		return new MapConfig(newConfigMap);
	}
	
	/** Process incoming events and update the user models stats */
	@Override public void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		String topicName = envelope.getSystemStreamPartition().getSystemStream().getStream();
		String messageText = (String)envelope.getMessage();
		JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);

		EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager = getEventsPrevalenceModelStreamTaskManager(message, topicName, messageText);
		eventsPrevalenceModelStreamTaskManager.process(envelope, collector, coordinator);
	}
	
	private EventsPrevalenceModelStreamTaskManager getEventsPrevalenceModelStreamTaskManager(JSONObject event, String topicName, String messageText) throws Exception{
		String dataSource = (String) event.get(dataSourceFieldName);
		if(dataSource == null){
			// convert topic to data source name
			dataSource = topicToDataSourceMap.get(topicName);
			if(dataSource == null){
				String errMsg = String.format("received event which doesn't contains the %s field and the topic %s is not mapped to any data source. event: %s", dataSourceFieldName,  topicName, messageText);
				logger.error(errMsg);
				throw new Exception(errMsg);
			}
		}
		EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager = dataSourceToEventsPrevalenceModelStreamTaskManagerMap.get(dataSource);
		if (eventsPrevalenceModelStreamTaskManager==null) {
			String errMsg = String.format("recieved event with data source %s which is not configured in the task. event: %s", dataSource, messageText);
			logger.error(errMsg);
			throw new Exception(errMsg);
		}
		
		return eventsPrevalenceModelStreamTaskManager;
	}

	
	
	/** periodically save the state to mongodb as a secondary backing store */
	@Override public void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) {
		for(EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager: dataSourceToEventsPrevalenceModelStreamTaskManagerMap.values()){
			eventsPrevalenceModelStreamTaskManager.window(collector, coordinator);
		}
	}

	/** save the state to mongodb when the job shutsdown */
	@Override protected void wrappedClose() throws Exception {
		for(EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager: dataSourceToEventsPrevalenceModelStreamTaskManagerMap.values()){
			eventsPrevalenceModelStreamTaskManager.close();
		}
		dataSourceToEventsPrevalenceModelStreamTaskManagerMap.clear();
	}
}
