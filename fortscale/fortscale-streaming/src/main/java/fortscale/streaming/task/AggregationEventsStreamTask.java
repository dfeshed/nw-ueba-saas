package fortscale.streaming.task;

import com.google.common.collect.Iterables;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.FortscaleValueResolver;
import fortscale.streaming.service.SpringService;
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
	private String dataSourceFieldName;
	private String dateFieldName;
	private Boolean skipSendingAggregationEvents;

	private Counter processedMessageCount;
	private Counter lastTimestampCount;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		
		dataSourceFieldName = resolveStringValue(config, "fortscale.data.source.field", res);

		skipSendingAggregationEvents = resolveBooleanValue(config, "fortscale.aggregation.sendevents", res);

		aggregatorManager = new AggregatorManager(config, new ExtendedSamzaTaskContext(context, config),skipSendingAggregationEvents);
		
		processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), "aggregation-message-count");

		lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(),
				String.format("%s-last-message-epochtime", config.get("job.name")));

		dateFieldName = resolveStringValue(config, "fortscale.timestamp.field", res);

	}


	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		processedMessageCount.inc();

		String messageText = (String)envelope.getMessage();
		JSONObject event = (JSONObject)JSONValue.parseWithException(messageText);

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
