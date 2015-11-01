package fortscale.streaming.task;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToBoolean;
import static fortscale.utils.ConversionUtils.convertToString;

import java.util.HashMap;
import java.util.Map;

import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.ClosableTask;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

import fortscale.streaming.service.EventsPrevalenceModelStreamTaskManager;
import fortscale.streaming.service.FortscaleStringValueResolver;
import fortscale.streaming.service.SpringService;
import org.slf4j.LoggerFactory;

public class MultipleEventsPrevalenceModelStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private static final String FORTSCALE_EVENTS_PREVALENCE_STRAM_MANATGERS_DATA_SOURCES_PROPERTY_NAME = "fortscale.events-prevalence-stream-managers.data-sources";
	private static final String DATA_SOURCE_FIELD_NAME_PROPERTY = "${streaming.event.datasource.field.name}";

	private static final Logger logger = Logger.getLogger(EventsPrevalenceModelStreamTask.class);

	private FortscaleStringValueResolver fortscaleStringValueResolver;

	private Map<String, EventsPrevalenceModelStreamTaskManager> dataSourceToEventsPrevalenceModelStreamTaskManagerMap = new HashMap<String, EventsPrevalenceModelStreamTaskManager>();
	private String dataSourceFieldName;


	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		fortscaleStringValueResolver = SpringService.getInstance().resolve(FortscaleStringValueResolver.class);

		dataSourceFieldName = fortscaleStringValueResolver.resolveStringValue(DATA_SOURCE_FIELD_NAME_PROPERTY);

		// Get configuration properties
		String[] dataSources = config.get(FORTSCALE_EVENTS_PREVALENCE_STRAM_MANATGERS_DATA_SOURCES_PROPERTY_NAME, "").split(",\\s*");
		if(dataSources.length==0) {
			throw new IllegalArgumentException(String.format("%s config property is missing or empty, can't create events streaming managers.", FORTSCALE_EVENTS_PREVALENCE_STRAM_MANATGERS_DATA_SOURCES_PROPERTY_NAME));
		}

		Config fieldsSubset = config.subset("fortscale.");
		for (String dataSource : dataSources) {
			Config dataSourceConfig = fieldsSubset.subset(String.format("%s.", dataSource));
			dataSourceConfig = addPrefixToConfigEntries(dataSourceConfig, "fortscale.");
			EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager = new EventsPrevalenceModelStreamTaskManager(dataSourceConfig, context);
			dataSourceToEventsPrevalenceModelStreamTaskManagerMap.put(dataSource, eventsPrevalenceModelStreamTaskManager);
		}

	}
	
	private String resolveStringValue(Config config, String string) {
		return fortscaleStringValueResolver.resolveStringValue(getConfigString(config, string));
	}

	private Config addPrefixToConfigEntries(Config config, String prefix) {
		if(config!=null && StringUtils.isNotBlank(prefix)) {
			for (String key : config.keySet()) {
				String newValue = String.format("%s%s", prefix, config.get(key));
				config.put(key, newValue);
			}
		}
		return config;
	}
	
	/** Process incoming events and update the user models stats */
	@Override public void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		String messageText = (String)envelope.getMessage();
		JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);

		String dataSource = convertToString(message.get(dataSourceFieldName));
		if(StringUtils.isNotBlank(dataSource)) {
			EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager = dataSourceToEventsPrevalenceModelStreamTaskManagerMap.get(dataSource);
			if(eventsPrevalenceModelStreamTaskManager != null) {
				eventsPrevalenceModelStreamTaskManager.process(envelope, collector, coordinator);
			} else {
				logger.error("got an event with non-supported {}. event: {}", dataSourceFieldName, messageText);
			}
		} else {
			logger.error("got an event with no {} field. event: {}", dataSourceFieldName, messageText);
		}
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
