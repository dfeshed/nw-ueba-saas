package fortscale.streaming.task;

import fortscale.ml.model.listener.KafkaModelBuildingListener;
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.model.ModelBuildingRegistrationService;
import fortscale.streaming.service.model.ModelBuildingSamzaStore;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.springframework.util.Assert;

public class ModelBuildingStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private KafkaModelBuildingListener modelBuildingListener;
	private ModelBuildingRegistrationService modelBuildingRegistrationService;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		String modelBuildingStatusOutputTopic = config.get("fortscale.model.building.status.output.topic", null);
		Assert.hasText(modelBuildingStatusOutputTopic);

		modelBuildingListener = new KafkaModelBuildingListener(modelBuildingStatusOutputTopic);
		ModelBuildingSamzaStore modelBuildingStore = new ModelBuildingSamzaStore(new ExtendedSamzaTaskContext(context, config));
		modelBuildingRegistrationService = new ModelBuildingRegistrationService(modelBuildingListener, modelBuildingStore);
	}

	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		String message = (String)envelope.getMessage();
		JSONObject event = (JSONObject)JSONValue.parseWithException(message);

		if (modelBuildingListener != null) {
			modelBuildingListener.setMessageCollector(collector);
		}

		if (modelBuildingRegistrationService != null) {
			modelBuildingRegistrationService.process(event);
		}
	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		if (modelBuildingListener != null) {
			modelBuildingListener.setMessageCollector(collector);
		}

		if (modelBuildingRegistrationService != null) {
			modelBuildingRegistrationService.window();
		}
	}

	@Override
	protected void wrappedClose() throws Exception {
		modelBuildingListener = null;
		modelBuildingRegistrationService = null;
	}
}
