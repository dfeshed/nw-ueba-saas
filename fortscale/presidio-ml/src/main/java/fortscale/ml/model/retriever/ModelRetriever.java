package fortscale.ml.model.retriever;

import fortscale.common.feature.Feature;
import fortscale.ml.model.*;
import fortscale.ml.model.ModelBuilderData.NoDataReason;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

public class ModelRetriever extends AbstractDataRetriever {
	private final ModelRetrieverConf config;
	private ModelConfService modelConfService;
	private ModelStore modelStore;

	private ModelConf modelConf;

	public ModelRetriever(ModelRetrieverConf config, ModelStore modelStore) {
		super(config);
		this.config = config;
		this.modelStore = modelStore;

	}

	@Override
	public ModelBuilderData retrieve(String contextId, Date endTime) {
		Assert.isNull(contextId, String.format("%s can't be used with a context", getClass().getSimpleName()));
		fillModelConfService();

		List<Model> models = modelStore.getAllContextsModelDaosWithLatestEndTimeLte(modelConf, endTime.toInstant()).stream()
				.map(ModelDAO::getModel)
				.collect(Collectors.toList());

		if (models.isEmpty()) {
			return new ModelBuilderData(NoDataReason.NO_DATA_IN_DATABASE);
		} else {
			return new ModelBuilderData(models);
		}
	}

	private void fillModelConfService() {
		if(modelConfService == null)
		{
			modelConfService = DynamicModelConfServiceContainer.getModelConfService();
			String modelConfName = config.getModelConfName();
			modelConf = this.modelConfService.getModelConf(modelConfName);
			Assert.notNull(modelConf,String.format("failed to find modelConf for modelConfName=%s",modelConfName));
		}
	}

	@Override
	public ModelBuilderData retrieve(String contextId, Date endTime, Feature feature) {
		throw new UnsupportedOperationException(String.format(
				"%s does not support retrieval of a single feature",
				getClass().getSimpleName()));
	}

	@Override
	public Set<String> getEventFeatureNames() {
		throw new UnsupportedOperationException(String.format("%s should be used to create \"additional-models\" only",
				getClass().getSimpleName()));
	}

	@Override
	public List<String> getContextFieldNames() {
		return Collections.emptyList();
	}

	@Override
	public String getContextId(Map<String, String> context) {
		return null;
	}
}
