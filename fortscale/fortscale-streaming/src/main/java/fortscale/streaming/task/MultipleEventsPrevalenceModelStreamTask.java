package fortscale.streaming.task;

import fortscale.streaming.service.EventsPrevalenceModelStreamTaskManager;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.config.MapConfig;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.streaming.ConfigUtils.getConfigStringList;

public class MultipleEventsPrevalenceModelStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private static final String FORTSCALE_EVENTS_PREVALENCE_STRAM_MANATGERS_DATA_SOURCES_PROPERTY_NAME = "fortscale.events-prevalence-stream-managers.data-sources";
	private static final String DATA_SOURCE_FIELD_NAME_PROPERTY = "${streaming.event.datasource.field.name}";

	private static final Logger logger = Logger.getLogger(EventsPrevalenceModelStreamTask.class);

	private Map<StreamingTaskDataSourceConfigKey, EventsPrevalenceModelStreamTaskManager> dataSourceToEventsPrevalenceModelStreamTaskManagerMap = new HashMap<>();

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		List<String> availableDataSources = getConfigStringList(config,FORTSCALE_EVENTS_PREVALENCE_STRAM_MANATGERS_DATA_SOURCES_PROPERTY_NAME);

		for (Map.Entry<String,String> configField : config.subset("fortscale.events.name.").entrySet()) {
			String configKey = configField.getValue();

			String dataSource = getConfigString(config, String.format("fortscale.events.%s.data.source", configKey));

			if (!availableDataSources.contains(dataSource)) {
				logger.warn("Cannot find data source {} in data sources list: {}");

				continue;
			}

			String lastState = getConfigString(config, String.format("fortscale.events.%s.last.state", configKey));

			Config dataSourceConfig = config.subset(String.format("fortscale.events.%s.", configKey));
			dataSourceConfig = addPrefixToConfigEntries(dataSourceConfig, "fortscale.");
			EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager = new EventsPrevalenceModelStreamTaskManager(dataSourceConfig, context);
			dataSourceToEventsPrevalenceModelStreamTaskManagerMap.put(new StreamingTaskDataSourceConfigKey(dataSource, lastState), eventsPrevalenceModelStreamTaskManager);
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
		EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager = getEventsPrevalenceModelStreamTaskManager(envelope);
		eventsPrevalenceModelStreamTaskManager.process(envelope, collector, coordinator);
	}
	
	private EventsPrevalenceModelStreamTaskManager getEventsPrevalenceModelStreamTaskManager(IncomingMessageEnvelope envelope) throws Exception{
		JSONObject message = parseJsonMessage(envelope);

		StreamingTaskDataSourceConfigKey configKey = extractDataSourceConfigKey(message);

		EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager = dataSourceToEventsPrevalenceModelStreamTaskManagerMap.get(configKey);

		if (eventsPrevalenceModelStreamTaskManager == null)
		{
			throw new IllegalStateException("No configuration found for config key " + configKey + ". Message received: " + message.toJSONString());
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
