package fortscale.streaming.task;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.aggregation.AggregatorManager;
import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;

public class AggregationEventsStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private String controlTopic;
	private AggregatorManager aggregatorManager;
	private Counter processedMessageCount;
	private Counter lastTimestampCount;
	private String dateFieldName;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		controlTopic = resolveStringValue(config, "fortscale.aggregation.control.topic", res);
		Boolean skipSendEvents = resolveBooleanValue(config, "fortscale.aggregation.skip.send.events", res);
		aggregatorManager = new AggregatorManager(config, new ExtendedSamzaTaskContext(context, config), skipSendEvents);
		processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), "aggregation-message-count");
		String counterName = String.format("%s-last-message-epochtime", config.get("job.name"));
		lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(), counterName);
		dateFieldName = resolveStringValue(config, "fortscale.timestamp.field", res);
	}

	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		String messageText = (String)envelope.getMessage();
		JSONObject event = (JSONObject)JSONValue.parseWithException(messageText);
		String topic = envelope.getSystemStreamPartition().getSystemStream().getStream();
		if (controlTopic.equals(topic)) {
			wrappedWindow(collector, coordinator);
		} else {
			processedMessageCount.inc();
			aggregatorManager.processEvent(event, collector);
			Long endTimestampSeconds = ConversionUtils.convertToLong(event.get(dateFieldName));
			lastTimestampCount.set(endTimestampSeconds);
		}
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
