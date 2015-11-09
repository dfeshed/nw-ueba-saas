package fortscale.streaming.task;

import fortscale.ml.model.ModelService;
import fortscale.ml.model.listener.KafkaModelBuildingListener;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.springframework.util.Assert;

public class ModelBuildingStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private KafkaModelBuildingListener modelBuildingListener;
	private ModelService modelService;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		String modelBuildingStatusOutputTopic = config.get("fortscale.model.building.status.output.topic", null);
		Assert.hasText(modelBuildingStatusOutputTopic);

		modelBuildingListener = new KafkaModelBuildingListener(modelBuildingStatusOutputTopic);
		modelService = new ModelService(modelBuildingListener);
	}

	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		String message = (String)envelope.getMessage();
		JSONObject event = (JSONObject)JSONValue.parseWithException(message);

		if (modelBuildingListener != null) {
			modelBuildingListener.setMessageCollector(collector);
		}

		if (modelService != null) {
			modelService.process(event);
		}
	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		if (modelBuildingListener != null) {
			modelBuildingListener.setMessageCollector(collector);
		}

		if (modelService != null) {
			long currentTimeSeconds = TimestampUtils.convertToSeconds(System.currentTimeMillis());
			modelService.window(currentTimeSeconds);
		}
	}

	@Override
	protected void wrappedClose() throws Exception {
		modelBuildingListener = null;
		modelService = null;
	}
}
