package fortscale.ml.model;

import fortscale.ml.model.listener.IModelBuildingListener;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.*;

@Configurable(preConstruction = true)
public class ModelService implements IModelBuildingScheduler {
	private static final int PRIORITY_QUEUE_INITIAL_CAPACITY = 50;
	private static final String MODEL_CONF_NAME_JSON_FIELD = "modelConfName";

	@Autowired
	private ModelConfService modelConfService;

	private IModelBuildingListener modelBuildingListener;
	private Map<String, ModelBuilderManager> modelConfNameToManager;
	private PriorityQueue<Pair<IModelBuildingRegistrar, Long>> registrarRunTimeQueue;
	private long sessionId;

	public ModelService(IModelBuildingListener modelBuildingListener) {
		Assert.notNull(modelBuildingListener);
		this.modelBuildingListener = modelBuildingListener;
		sessionId = new Random().nextLong();

		modelConfNameToManager = new HashMap<>();
		registrarRunTimeQueue = new PriorityQueue<>(PRIORITY_QUEUE_INITIAL_CAPACITY, new Comparator<Pair<IModelBuildingRegistrar, Long>>() {
			@Override
			public int compare(Pair<IModelBuildingRegistrar, Long> pair1, Pair<IModelBuildingRegistrar, Long> pair2) {
				return Long.compare(pair1.getRight(), pair2.getRight());
			}
		});

		for (ModelConf modelConf : modelConfService.getModelConfs()) {
			ModelBuilderManager modelManager = new ModelBuilderManager(modelConf, this);
			modelConfNameToManager.put(modelConf.getName(), modelManager);
		}
	}

	public void process(JSONObject event) {
		String modelConfName = event.getAsString(MODEL_CONF_NAME_JSON_FIELD);
		ModelBuilderManager modelManager = modelConfNameToManager.get(modelConfName);
		if (modelManager != null) {
			modelManager.process(modelBuildingListener, sessionId);
		}
	}

	public void window(long currentTimeSeconds) {
		while (!registrarRunTimeQueue.isEmpty() && registrarRunTimeQueue.peek().getRight() <= currentTimeSeconds) {
			Pair<IModelBuildingRegistrar, Long> registrarRunTimePair = registrarRunTimeQueue.poll();
			registrarRunTimePair.getLeft().process(modelBuildingListener, sessionId);
		}
	}

	@Override
	public void register(IModelBuildingRegistrar registrar, long epochtime) {
		if (registrar != null && epochtime >= 0) {
			Pair<IModelBuildingRegistrar, Long> registrarRunTimePair = new ImmutablePair<>(registrar, epochtime);
			registrarRunTimeQueue.add(registrarRunTimePair);
		}
	}
}
