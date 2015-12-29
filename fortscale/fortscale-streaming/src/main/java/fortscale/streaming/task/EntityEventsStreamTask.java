package fortscale.streaming.task;

import fortscale.entity.event.EntityEventDataStore;
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.entity.event.EntityEventDataStoreSamza;
import fortscale.streaming.service.entity.event.EntityEventService;
import fortscale.streaming.service.entity.event.KafkaEntityEventSender;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.springframework.util.Assert;

public class EntityEventsStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private static final String SKIP_SENDING_ENTITY_EVENTS_PROPERTY = "fortscale.skip.sending.entity.events";
	private static final String OUTPUT_TOPIC_PROPERTY = "fortscale.output.topic";

	private EntityEventService entityEventService;
	private KafkaEntityEventSender sender;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		EntityEventDataStore entityEventDataStore = new EntityEventDataStoreSamza(new ExtendedSamzaTaskContext(context, config));
		entityEventService = new EntityEventService(entityEventDataStore);

		boolean skipSendingEntityEvents = config.getBoolean(SKIP_SENDING_ENTITY_EVENTS_PROPERTY, false);
		String outputTopic = config.get(OUTPUT_TOPIC_PROPERTY, null);
		Assert.hasText(outputTopic);
		sender = new KafkaEntityEventSender(skipSendingEntityEvents ? null : outputTopic);
	}

	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		if (entityEventService != null) {
			String messageText = (String)envelope.getMessage();
			JSONObject event = (JSONObject)JSONValue.parseWithException(messageText);
			entityEventService.process(event);
		}
	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		if (entityEventService != null) {
			sender.setCollector(collector);
			entityEventService.sendNewEntityEventsAndUpdateStore(System.currentTimeMillis(), sender);
		}
	}

	@Override
	protected void wrappedClose() throws Exception {
		entityEventService = null;
	}
}
