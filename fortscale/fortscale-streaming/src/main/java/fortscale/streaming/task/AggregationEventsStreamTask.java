package fortscale.streaming.task;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.aggregation.AggregatorManager;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.springframework.beans.factory.annotation.Configurable;

import static fortscale.utils.ConversionUtils.convertToLong;

@Configurable(preConstruction = true)
public class AggregationEventsStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private static final String TASK_CONTROL_TOPIC = "fortscale-aggregation-events-control";

	private AggregatorManager aggregatorManager;
	private String dateFieldName;
	private Boolean skipSendingAggregationEvents;

	private Counter processedMessageCount;
	private Counter lastTimestampCount;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {

		skipSendingAggregationEvents = resolveBooleanValue(config, "fortscale.aggregation.sendevents", res);

		aggregatorManager = new AggregatorManager(config, new ExtendedSamzaTaskContext(context, config),skipSendingAggregationEvents);
		
		processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), "aggregation-message-count");

		lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(),
				String.format("%s-last-message-epochtime", config.get("job.name")));

		dateFieldName = resolveStringValue(config, "fortscale.timestamp.field", res);

	}


	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		String messageText = (String)envelope.getMessage();
		JSONObject event = (JSONObject)JSONValue.parseWithException(messageText);
		String topic = envelope.getSystemStreamPartition().getSystemStream().getStream();
		if (TASK_CONTROL_TOPIC.equals(topic)) {
			wrappedWindow(collector, coordinator);
		} else {
			processedMessageCount.inc();
			aggregatorManager.processEvent(event, collector);
			Long endTimestampSeconds = convertToLong(event.get(dateFieldName));
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
