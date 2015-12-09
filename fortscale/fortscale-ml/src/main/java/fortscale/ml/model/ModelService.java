package fortscale.ml.model;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.builder.IModelBuilderConf;
import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.selector.IContextSelectorConf;
import fortscale.utils.factory.FactoryService;
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
	@Autowired
	private FactoryService<IContextSelector> contextSelectorFactoryService;
	@Autowired
	private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;
	@Autowired
	private FactoryService<IModelBuilder> modelBuilderFactoryService;

	private Map<String, ModelBuilderManager> modelConfNameToManager;

	public IContextSelector getContextSelector(IContextSelectorConf conf) {
		return contextSelectorFactoryService.getProduct(conf);
	}

	public AbstractDataRetriever getDataRetriever(AbstractDataRetrieverConf conf) {
		return dataRetrieverFactoryService.getProduct(conf);
	}

	public IModelBuilder getModelBuilder(IModelBuilderConf conf) {
		return modelBuilderFactoryService.getProduct(conf);
	}

	public void process(
			IModelBuildingListener listener, String sessionId, String modelConfName,
			Date previousEndTime, Date currentEndTime) {

		ensureModelBuilderManagersCreation();
		ModelBuilderManager modelBuilderManager = modelConfNameToManager.get(modelConfName);

		if (modelBuilderManager != null) {
			modelBuilderManager.process(listener, sessionId, previousEndTime, currentEndTime);
		} else {
			logger.error("Ignoring invalid model conf name {}.", modelConfName);
		}
	}

	private void ensureModelBuilderManagersCreation() {
		if (modelConfNameToManager == null) {
			modelConfNameToManager = new HashMap<>();

			for (ModelConf modelConf : modelConfService.getModelConfs()) {
				ModelBuilderManager modelBuilderManager = new ModelBuilderManager(modelConf, this);
				modelConfNameToManager.put(modelConf.getName(), modelBuilderManager);
			}
		}
	}
}
