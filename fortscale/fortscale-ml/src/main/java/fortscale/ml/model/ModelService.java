package fortscale.ml.model;

import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class ModelService {
	private static final int PRIORITY_QUEUE_INITIAL_CAPACITY = 50;
	private static final String MODEL_CONF_NAME_JSON_FIELD = "modelConfName";

	@Autowired
	private ModelConfService modelConfService;

	private IModelBuildingListener modelBuildingListener;
	private Map<String, ModelBuilderManager> modelConfNameToManager;
	private PriorityQueue<ModelBuilderManager> runTimePriorityQueue;

	public ModelService(IModelBuildingListener modelBuildingListener) {
		Assert.notNull(modelBuildingListener);
		this.modelBuildingListener = modelBuildingListener;

		modelConfNameToManager = new HashMap<>();
		runTimePriorityQueue = new PriorityQueue<>(PRIORITY_QUEUE_INITIAL_CAPACITY, new Comparator<ModelBuilderManager>() {
			@Override
			public int compare(ModelBuilderManager modelManager1, ModelBuilderManager modelManager2) {
				return Long.compare(modelManager1.getNextRunTimeInSeconds(), modelManager2.getNextRunTimeInSeconds());
			}
		});

		for (ModelConf modelConf : modelConfService.getModelConfs()) {
			ModelBuilderManager modelManager = new ModelBuilderManager(modelConf);
			modelConfNameToManager.put(modelConf.getName(), modelManager);

			calcNextRunTimeFromNow(modelManager);
			runTimePriorityQueue.add(modelManager);
		}
	}

	public void process(JSONObject event) {
		String modelConfName = event.getAsString(MODEL_CONF_NAME_JSON_FIELD);
		ModelBuilderManager modelManager = modelConfNameToManager.get(modelConfName);
		if (modelManager != null) {
			modelManager.process();
		}
	}

	public void window(long currentTimeSeconds) {
		while (!runTimePriorityQueue.isEmpty() && runTimePriorityQueue.peek().getNextRunTimeInSeconds() <= currentTimeSeconds) {
			ModelBuilderManager modelManager = runTimePriorityQueue.poll();
			modelManager.process();
			calcNextRunTimeFromNow(modelManager);
			runTimePriorityQueue.add(modelManager);
		}
	}

	private static void calcNextRunTimeFromNow(ModelBuilderManager modelManager) {
		long currentTimeSeconds = TimestampUtils.convertToSeconds(System.currentTimeMillis());
		modelManager.calcNextRunTime(currentTimeSeconds);
	}
}
