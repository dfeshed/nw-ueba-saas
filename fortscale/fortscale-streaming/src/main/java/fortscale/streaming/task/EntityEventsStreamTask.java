package fortscale.streaming.task;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.entity.event.EntityEventDataStore;
import fortscale.entity.event.EntityEventService;
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.FortscaleValueResolver;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.entity.event.EntityEventDataStoreSamza;
import fortscale.streaming.service.entity.event.KafkaEntityEventSender;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.springframework.util.Assert;

public class EntityEventsStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private static final String SKIP_SENDING_ENTITY_EVENTS_PROPERTY = "fortscale.skip.sending.entity.events";
	private static final String OUTPUT_TOPIC_NAME_PROPERTY = "kafka.entity.event.topic";

	private EntityEventService entityEventService;
	private String outputTopicName;

	private Counter receivedMessageCount;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		// Create the entity event service
		EntityEventDataStore store = new EntityEventDataStoreSamza(new ExtendedSamzaTaskContext(context, config));
		entityEventService = new EntityEventService(store);

		// Get skip sending entity events flag
		String skipString = config.get(SKIP_SENDING_ENTITY_EVENTS_PROPERTY, Boolean.toString(false));
		FortscaleValueResolver resolver = SpringService.getInstance().resolve(FortscaleValueResolver.class);
		boolean skipBoolean = resolver.resolveBooleanValue(skipString);

		// Get output Kafka topic name
		if (skipBoolean) {
			outputTopicName = null;
		} else {
			outputTopicName = config.get(OUTPUT_TOPIC_NAME_PROPERTY, null);
			Assert.hasText(outputTopicName);
		}
	}

	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		if (entityEventService != null) {
			String messageText = (String)envelope.getMessage();
			JSONObject event = (JSONObject)JSONValue.parseWithException(messageText);
			receivedMessageCount.inc();
			entityEventService.process(event);
		}
	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		if (entityEventService != null) {
			KafkaEntityEventSender sender = new KafkaEntityEventSender(outputTopicName, collector);
			entityEventService.sendNewEntityEventsAndUpdateStore(System.currentTimeMillis(), sender);
		}
	}

	@Override
	protected void wrappedClose() throws Exception {
		entityEventService = null;
	}
}
