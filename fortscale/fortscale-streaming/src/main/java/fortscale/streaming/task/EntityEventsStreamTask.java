package fortscale.streaming.task;

import fortscale.entity.event.EntityEventService;
import fortscale.services.impl.SpringService;
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.FortscaleValueResolver;
import fortscale.streaming.service.entity.event.EntityEventDataStoreSamza;
import fortscale.streaming.service.entity.event.KafkaEntityEventSender;
import fortscale.streaming.task.message.ProcessMessageContext;
import net.minidev.json.JSONObject;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.task.*;
import org.springframework.util.Assert;

public class EntityEventsStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private static final String SKIP_SENDING_ENTITY_EVENTS_PROPERTY = "fortscale.skip.sending.entity.events";
	private static final String OUTPUT_TOPIC_NAME_PROPERTY = "fortscale.output.topic.name";

	private static final String TASK_CONTROL_TOPIC = "fortscale-entity-event-stream-control";

	private EntityEventService entityEventService;
	EntityEventDataStoreSamza store;
	private String outputTopicName;

	private Counter receivedMessageCount;

	@Override
	protected void processInit(Config config, TaskContext context) throws Exception {
		// Create the entity event service
		store = new EntityEventDataStoreSamza(new ExtendedSamzaTaskContext(context, config));
		entityEventService = new EntityEventService(store);
		receivedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(),
		String.format("%s-received-message-count", config.get("job.name")));

		// Get skip sending entity events flag
		String skipString = config.get(SKIP_SENDING_ENTITY_EVENTS_PROPERTY, Boolean.toString(false));
		FortscaleValueResolver resolver = SpringService.getInstance().resolve(FortscaleValueResolver.class);
		boolean skipBoolean = resolver.resolveBooleanValue(skipString);

		// Get output Kafka topic name
		if (skipBoolean) {
			outputTopicName = null;
		} else {
			outputTopicName = resolver.resolveStringValue(config, OUTPUT_TOPIC_NAME_PROPERTY);
			Assert.hasText(outputTopicName);
		}
	}

	@Override
	protected void processMessage(ProcessMessageContext messageContext) throws Exception {
		if (entityEventService != null) {
			// Get the input topic
			String topic = messageContext.getTopicName();
			if(TASK_CONTROL_TOPIC.equals(topic)){
				store.sync();
				return;
			}
			JSONObject event = messageContext.getMessageAsJson();
			receivedMessageCount.inc();
			entityEventService.process(event);
		}
	}

	@Override
	protected void processWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		if (entityEventService != null) {
			KafkaEntityEventSender sender = new KafkaEntityEventSender(outputTopicName, collector);
			entityEventService.sendNewEntityEventsAndUpdateStore(System.currentTimeMillis(), sender);
		}
	}

	@Override
	protected void processClose() throws Exception {
		entityEventService = null;
	}
}
