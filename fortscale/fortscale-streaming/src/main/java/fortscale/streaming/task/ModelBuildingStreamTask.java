package fortscale.streaming.task;

import fortscale.ml.model.listener.KafkaModelBuildingListener;
import fortscale.services.impl.SpringService;
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.FortscaleValueResolver;
import fortscale.streaming.service.model.ModelBuildingRegistrationService;
import fortscale.streaming.service.model.ModelBuildingSamzaStore;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.springframework.util.Assert;

public class ModelBuildingStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {

	public static final String CONTROL_OUTPUT_TOPIC_KEY = "fortscale.model.build.control.output.topic";
	private static final String MODEL_FILTER_REGEX = "fortscale.model.build.all_models.model_name.filter.regex";

	private KafkaModelBuildingListener modelBuildingListener;
	private ModelBuildingRegistrationService modelBuildingRegistrationService;
	private String allModelFilterRegex;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		FortscaleValueResolver resolver = SpringService.getInstance().resolve(FortscaleValueResolver.class);
		String controlOutputTopic = resolver.resolveStringValue(config, CONTROL_OUTPUT_TOPIC_KEY);
		Assert.hasText(controlOutputTopic);

		allModelFilterRegex = resolver.resolveStringValue(config,MODEL_FILTER_REGEX);
		modelBuildingListener = new KafkaModelBuildingListener(controlOutputTopic);
		ModelBuildingSamzaStore modelBuildingStore = new ModelBuildingSamzaStore(new ExtendedSamzaTaskContext(context, config));
		modelBuildingRegistrationService = new ModelBuildingRegistrationService(modelBuildingListener, modelBuildingStore,allModelFilterRegex);
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
