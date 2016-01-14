package fortscale.streaming.service.model;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.cache.ModelCacheManager;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.model.retriever.ContextHistogramRetrieverConf;
import fortscale.streaming.ExtendedSamzaTaskContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.HashMap;
import java.util.Map;

@Configurable(preConstruction = true)
public class ModelsCacheServiceSamza implements ModelsCacheService {
	@Autowired
	private ModelConfService modelConfService;

	// A mapping from a model conf name to its model cache manager
	private Map<String, ModelCacheManager> modelCacheManagers;

	public ModelsCacheServiceSamza(ExtendedSamzaTaskContext context) {
		modelCacheManagers = new HashMap<>();

		for (ModelConf modelConf : modelConfService.getModelConfs()) {
			ModelCacheManager modelCacheManager = isDiscreteModelConf(modelConf) ?
					new DiscreteModelCacheManagerSamza(context, modelConf) :
					new ModelCacheManagerSamza(context, modelConf);
			modelCacheManagers.put(modelConf.getName(), modelCacheManager);
		}
	}

	@Override
	public Model getModel(Feature feature, String modelConfName, Map<String, Feature> context, long eventEpochtime) {
		if (modelCacheManagers.containsKey(modelConfName)) {
			return modelCacheManagers.get(modelConfName).getModel(feature, context, eventEpochtime);
		} else {
			return null;
		}
	}

	@Override
	public void window() {
		// TODO: Clean models cache info from each manager
	}

	@Override
	public void close() {}

	private static boolean isDiscreteModelConf(ModelConf modelConf) {
		String factoryName = modelConf.getDataRetrieverConf().getFactoryName();
		return factoryName.equals(ContextHistogramRetrieverConf.CONTEXT_HISTOGRAM_RETRIEVER);
	}
}
