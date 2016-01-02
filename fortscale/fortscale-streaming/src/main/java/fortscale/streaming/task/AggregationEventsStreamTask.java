package fortscale.streaming.task;

import com.google.common.collect.Iterables;
import fortscale.services.impl.SpringService;
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.FortscaleValueResolver;
import fortscale.streaming.service.aggregation.AggregatorManager;
import fortscale.utils.StringPredicates;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.HashMap;
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToLong;

@Configurable(preConstruction = true)
public class AggregationEventsStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private AggregatorManager aggregatorManager;
	private Map<String, String> topicToDataSourceMap = new HashMap<String, String>();
	private String dataSourceFieldName;
	private String dateFieldName;
	private Boolean skipSendingAggregationEvents;

	private Counter processedMessageCount;
	private Counter lastTimestampCount;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		FortscaleValueResolver res = SpringService.getInstance().resolve(FortscaleValueResolver.class);


		Config fieldsSubset = config.subset("fortscale.");
		for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(".input.topic"))) {
			String eventType = fieldConfigKey.substring(0, fieldConfigKey.indexOf(".input.topic"));
			String inputTopic = getConfigString(config, String.format("fortscale.%s.input.topic", eventType));
			topicToDataSourceMap.put(inputTopic, eventType);
		}
		
		dataSourceFieldName = resolveStringValue(config, "fortscale.data.source.field", res);

		skipSendingAggregationEvents = resolveBooleanValue(config, "fortscale.aggregation.sendevents", res);

		aggregatorManager = new AggregatorManager(config, new ExtendedSamzaTaskContext(context, config),skipSendingAggregationEvents);
		
		processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), "aggregation-message-count");

		lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(),
				String.format("%s-last-message-epochtime", config.get("job.name")));

		dateFieldName = resolveStringValue(config, "fortscale.timestamp.field", res);

	}

	private String resolveStringValue(Config config, String string, FortscaleValueResolver resolver) {
		return resolver.resolveStringValue(getConfigString(config, string));
	}

	private Boolean resolveBooleanValue(Config config, String string, FortscaleValueResolver resolver) {
		return resolver.resolveBooleanValue(getConfigString(config, string));
	}

	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		processedMessageCount.inc();
		// Get the input topic
		String topic = envelope.getSystemStreamPartition().getSystemStream().getStream();
		// Get Event
		String messageText = (String)envelope.getMessage();
		JSONObject event = (JSONObject)JSONValue.parseWithException(messageText);

		//Add data source to the event. In the future it should already be part of the event.
		if(!event.containsKey(dataSourceFieldName)){
			event.put(dataSourceFieldName, topicToDataSourceMap.get(topic));
		}

		aggregatorManager.processEvent(event, collector);

		Long endTimestampSeconds = convertToLong(event.get(dateFieldName));
		lastTimestampCount.set(endTimestampSeconds);

	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		if (aggregatorManager != null) {
			aggregatorManager.window(collector, coordinator);
		}
	}

	@Override
	protected void wrappedClose() throws Exception {
		if (aggregatorManager != null) {
			aggregatorManager.close();
			aggregatorManager = null;
		}
	}
}
