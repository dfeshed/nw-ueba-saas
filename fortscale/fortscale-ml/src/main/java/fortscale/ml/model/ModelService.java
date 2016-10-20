package fortscale.ml.model;

import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ModelService {
	private static final Logger logger = Logger.getLogger(ModelService.class);

	@Autowired
	private ModelConfService modelConfService;

	private Map<String, ModelBuilderManager> modelConfNameToManager;

	public void init() {
		modelConfNameToManager = new HashMap<>();

		for (ModelConf modelConf : modelConfService.getModelConfs()) {
			ModelBuilderManager modelBuilderManager = new ModelBuilderManager(modelConf);
			modelConfNameToManager.put(modelConf.getName(), modelBuilderManager);
		}
	}

	public void process(
			IModelBuildingListener listener, String sessionId, String modelConfName,
			Date previousEndTime, Date currentEndTime, Map<String, String> managerParams,
			Map<String, String> selectorParams, Map<String, String> retrieverParams,
			Map<String, String> builderParams) {
		ModelBuilderManager modelBuilderManager = modelConfNameToManager.get(modelConfName);
		if (modelBuilderManager != null) {
			modelBuilderManager.process(listener, sessionId, previousEndTime, currentEndTime,
					managerParams, selectorParams, retrieverParams, builderParams);
		} else {
			logger.error("Ignoring invalid model conf name {}.", modelConfName);
		}
	}
}
