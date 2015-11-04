package fortscale.ml.model;

import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class ModelService {
	private static final String MODEL_CONF_NAME_JSON_FIELD = "modelConfName";

	@Autowired
	ModelConfService modelConfService;

	Map<String, ModelManager> modelConfNameToManager;
	PriorityQueue<ModelManager> runTimePriorityQueue;

	public ModelService() {
		modelConfNameToManager = new HashMap<>();
		runTimePriorityQueue = new PriorityQueue<ModelManager>(new Comparator<ModelManager>() {
			
		});

		for (ModelConf modelConf : modelConfService.getModelConfs()) {
			ModelManager modelManager = new ModelManager(modelConf);
			modelConfNameToManager.put(modelConf.getName(), modelManager);

			long currentTimeSeconds = TimestampUtils.convertToSeconds(System.currentTimeMillis());
			modelManager.setNextRunTime(currentTimeSeconds);
			runTimePriorityQueue.add(modelManager);
		}
	}

	public void process(JSONObject event) {
		String modelConfName = event.getAsString(MODEL_CONF_NAME_JSON_FIELD);
		ModelManager modelManager = modelConfNameToManager.get(modelConfName);
		if (modelManager != null) {
			modelManager.process();
		}
	}

	public void window(long currentTimeSeconds) {
		while (!runTimePriorityQueue.isEmpty() && runTimePriorityQueue.peek().)
	}

	public void close() {
		
	}
}
