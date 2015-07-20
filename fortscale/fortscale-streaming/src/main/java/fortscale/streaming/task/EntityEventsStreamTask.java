package fortscale.streaming.task;

import fortscale.streaming.service.aggregation.entity.event.EntityEventService;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.springframework.util.Assert;

public class EntityEventsStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private String outputTopic;
	private EntityEventService entityEventService;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		outputTopic = config.get("fortscale.output.topic", null);
		Assert.notNull(outputTopic);
		entityEventService = new EntityEventService();
	}

	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		String messageText = (String)envelope.getMessage();
		JSONObject event = (JSONObject)JSONValue.parseWithException(messageText);
		entityEventService.process(event);
	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		if (entityEventService != null) {
			entityEventService.window(System.currentTimeMillis(), outputTopic, collector);
		}
	}

	@Override
	protected void wrappedClose() throws Exception {
		entityEventService = null;
	}
}
