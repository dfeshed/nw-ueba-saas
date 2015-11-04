package fortscale.streaming.task;

import fortscale.ml.model.ModelService;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.springframework.util.Assert;

public class ModelBuildingStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private String outputTopic;
	private ModelService modelService;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		outputTopic = config.get("fortscale.output.topic", null);
		Assert.notNull(outputTopic, "Model building output topic is missing in configuration.");
		modelService = new ModelService();
	}

	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		String message = (String)envelope.getMessage();
		JSONObject event = (JSONObject)JSONValue.parseWithException(message);
		if (modelService != null) {
			modelService.process(event);
		}
	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		if (modelService != null) {
			modelService.window();
		}
	}

	@Override
	protected void wrappedClose() throws Exception {
		if (modelService != null) {
			modelService.close();
			modelService = null;
		}
	}
}
