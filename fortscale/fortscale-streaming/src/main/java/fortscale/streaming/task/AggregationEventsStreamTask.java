package fortscale.streaming.task;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.aggregation.AggregatorManager;
import fortscale.streaming.task.message.ProcessMessageContext;
import fortscale.streaming.task.message.StreamingProcessMessageContext;
import fortscale.streaming.task.metrics.AggregationEventsStreamTaskMetrics;
import fortscale.utils.ConversionUtils;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.task.*;


public class AggregationEventsStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private static final Logger logger = Logger.getLogger(AggregationEventsStreamTask.class);

	private String controlTopic;
	private AggregatorManager aggregatorManager;
	private AggregationEventsStreamTaskMetrics taskMetrics;
	// TODO: remove all the Counters once BDP is abandoned in favor of DPM
	private Counter processedMessageCount;
	private Counter skippedMessageCount;
	private Counter lastTimestampCount;
	private String dateFieldName;

	@Override
	protected void processInit(Config config, TaskContext context) throws Exception {
		controlTopic = resolveStringValue(config, "fortscale.aggregation.control.topic", res);
		Boolean skipSendEvents = resolveBooleanValue(config, "fortscale.aggregation.skip.send.events", res);
		aggregatorManager = new AggregatorManager(config, new ExtendedSamzaTaskContext(context, config), skipSendEvents);
		processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(),
				resolveStringValue(config, "fortscale.processed.message.count.metric.name", res));
		skippedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(),
				resolveStringValue(config, "fortscale.skipped.message.count.metric.name", res));
		lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(),
				resolveStringValue(config, "fortscale.last.message.epochtime.metric.name", res));
		dateFieldName = resolveStringValue(config, "fortscale.timestamp.field", res);
	}

	@Override
	protected void wrappedCreateTaskMetrics() {
		taskMetrics = new AggregationEventsStreamTaskMetrics(statsService);
	}

	@Override
	protected void processMessage(ProcessMessageContext messageContext) throws Exception {

		JSONObject event = messageContext.getMessageAsJson();
		String topic = messageContext.getTopicName();
		Long epochtime = ConversionUtils.convertToLong(event.get(dateFieldName));
		StreamingProcessMessageContext streamingProcessMessageContext = (StreamingProcessMessageContext) messageContext;
		MessageCollector collector = streamingProcessMessageContext.getCollector();
		TaskCoordinator coordinator = streamingProcessMessageContext.getCoordinator();
		if (epochtime != null) {
			if (controlTopic.equals(topic)) {
				logger.info("received message at controlTopic: {}",event.toJSONString());
				aggregatorManager.advanceTime(epochtime);
				aggregatorManager.window(collector, coordinator, true);
			} else {
				processedMessageCount.inc();
				taskMetrics.processedMessages++;
				aggregatorManager.processEvent(event, collector);
				lastTimestampCount.set(epochtime);
				taskMetrics.lastMessageEpoch = epochtime;
			}
		} else {
			skippedMessageCount.inc();
			taskMetrics.skippedMessages++;
		}
	}

	@Override
	protected void processWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		if (aggregatorManager != null) {
			aggregatorManager.window(collector, coordinator, false);
		}
	}

	@Override
	protected void processClose() throws Exception {
		if (aggregatorManager != null) {
			aggregatorManager.close();
			aggregatorManager = null;
		}
	}
}
