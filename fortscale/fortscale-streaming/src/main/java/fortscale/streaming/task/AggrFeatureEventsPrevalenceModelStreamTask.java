package fortscale.streaming.task;

import com.google.common.collect.Iterables;
import fortscale.streaming.service.AggregatedFeatureAndEntityEventsMetricsService;
import fortscale.streaming.service.EventsPrevalenceModelStreamTaskManager;
import fortscale.streaming.service.FortscaleValueResolver;
import fortscale.streaming.service.SpringService;
import fortscale.utils.StringPredicates;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigStringList;
import static fortscale.utils.ConversionUtils.convertToString;

public class AggrFeatureEventsPrevalenceModelStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private static final Logger logger = Logger.getLogger(AggrFeatureEventsPrevalenceModelStreamTask.class);
	private static final String EVENT_TYPE_FIELD_NAME_PROPERTY = "${streaming.event.field.type}";

	private static final String TASK_CONTROL_TOPIC = "fortscale-aggregated-feature-event-prevalence-stats-control";

	private FortscaleValueResolver fortscaleValueResolver;
	private Map<String, List<String>> eventTypeToFeatureFullPath = new HashMap<>();
	private Map<String, EventsPrevalenceModelStreamTaskManager> featureToEventsPrevalenceModelStreamTaskManagerMap = new HashMap<>();
	private String eventTypeFieldName;

	private Counter processedMessageCount;
	private Counter skippedMessageCount;
	private AggregatedFeatureAndEntityEventsMetricsService aggregatedFeatureAndEntityEventsMetricsService;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		fortscaleValueResolver = SpringService.getInstance().resolve(FortscaleValueResolver.class);
		eventTypeFieldName = fortscaleValueResolver.resolveStringValue(EVENT_TYPE_FIELD_NAME_PROPERTY);

		// Get configuration properties for each event type
		Config fieldsSubset = config.subset("fortscale.event.type.");
		for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(".feature.full.path"))) {
			String eventType = fieldConfigKey.substring(0, fieldConfigKey.indexOf(".feature.full.path"));
			List<String> featureFullPath = resolveStringValues(fieldsSubset, fieldConfigKey);
			eventTypeToFeatureFullPath.put(eventType, featureFullPath);
		}

		// Get configuration properties for each feature
		fieldsSubset = config.subset("fortscale.");
		for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(".fortscale.scorers"))) {
			String featureName = fieldConfigKey.substring(0, fieldConfigKey.indexOf(".fortscale.scorers"));
			Config featureEventConfig = fieldsSubset.subset(String.format("%s.", featureName));
			EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager = new EventsPrevalenceModelStreamTaskManager(featureEventConfig, context);
			featureToEventsPrevalenceModelStreamTaskManagerMap.put(featureName, eventsPrevalenceModelStreamTaskManager);
		}

		// create counter metric for processed messages
		processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), "aggr-prevalence-processed-count");
		skippedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), "aggr-prevalence-skip-count");
		aggregatedFeatureAndEntityEventsMetricsService = new AggregatedFeatureAndEntityEventsMetricsService(context);
	}

	private List<String> resolveStringValues(Config config, String string) {
		return fortscaleValueResolver.resolveStringValues(getConfigStringList(config, string));
	}

	/** Process incoming events and update the user models stats */
	@Override
	public void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// Get the input topic
		String topic = envelope.getSystemStreamPartition().getSystemStream().getStream();
		if(TASK_CONTROL_TOPIC.equals(topic)){
			logger.info("Going to export models..");
			wrappedWindow(collector,coordinator);
			logger.info("Finished exporting models");
			return;
		}

		String messageText = (String)envelope.getMessage();
		JSONObject message = (JSONObject)JSONValue.parseWithException(messageText);

		String eventTypeFieldValue = convertToString(message.get(eventTypeFieldName));
		if (StringUtils.isNotBlank(eventTypeFieldValue)) {
			StringBuilder fullPathFeatureNameBuilder = new StringBuilder();
			fullPathFeatureNameBuilder.append(eventTypeFieldValue);
			for (String fieldName : eventTypeToFeatureFullPath.get(eventTypeFieldValue)) {
				fullPathFeatureNameBuilder.append(".").append(message.get(fieldName));
			}

			String fullPathFeatureName = fullPathFeatureNameBuilder.toString();
			aggregatedFeatureAndEntityEventsMetricsService.updateMetrics(fullPathFeatureName);
			EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager =
					featureToEventsPrevalenceModelStreamTaskManagerMap.get(fullPathFeatureName);

			if (eventsPrevalenceModelStreamTaskManager != null) {
				eventsPrevalenceModelStreamTaskManager.process(envelope, collector, coordinator, false);
				processedMessageCount.inc();
			} else {
				skippedMessageCount.inc();
			}
		} else {
			logger.error("got an event with no {} field. event: {}", eventTypeFieldName, messageText);
			skippedMessageCount.inc();
		}
	}


	/** periodically save the state to mongodb as a secondary backing store */
	@Override
	public void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) {
		for (EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager : featureToEventsPrevalenceModelStreamTaskManagerMap.values()) {
			eventsPrevalenceModelStreamTaskManager.window(collector, coordinator);
		}
	}

	/** save the state to mongodb when the job shutsdown */
	@Override
	protected void wrappedClose() throws Exception {
		for (EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager : featureToEventsPrevalenceModelStreamTaskManagerMap.values()) {
			eventsPrevalenceModelStreamTaskManager.close();
		}
		featureToEventsPrevalenceModelStreamTaskManagerMap.clear();
	}
}
