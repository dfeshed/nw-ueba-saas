package fortscale.ml.model;

import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ModelService implements InitializingBean {
	private static final Logger logger = Logger.getLogger(ModelService.class);

	@Autowired
	private ModelConfService modelConfService;

	private Map<String, ModelBuilderManager> modelConfNameToManager;

	@Override
	public void afterPropertiesSet() throws Exception {
		modelConfNameToManager = new HashMap<>();

		for (ModelConf modelConf : modelConfService.getModelConfs()) {
			ModelBuilderManager modelBuilderManager = new ModelBuilderManager(modelConf);
			modelConfNameToManager.put(modelConf.getName(), modelBuilderManager);
		}
	}

	public void process(
			IModelBuildingListener listener, String sessionId, String modelConfName,
			Date previousEndTime, Date currentEndTime) {

		ModelBuilderManager modelBuilderManager = modelConfNameToManager.get(modelConfName);

		if (modelBuilderManager != null) {
			modelBuilderManager.process(listener, sessionId, previousEndTime, currentEndTime);
		} else {
			logger.error("Ignoring invalid model conf name {}.", modelConfName);
		}
	}
}
