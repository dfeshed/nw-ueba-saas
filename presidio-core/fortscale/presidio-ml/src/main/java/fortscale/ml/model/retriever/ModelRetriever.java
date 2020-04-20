package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.bucket.FeatureBucketUtils;
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
		fillModelConfService();
		List<Model> models;
		if(config.getContextFieldName() == null){
			Assert.isNull(contextId, String.format("%s can't be used with a context", getClass().getSimpleName()));
			models = modelStore.getAllContextsModelDaosWithLatestEndTimeLte(modelConf, endTime.toInstant()).stream()
					.map(ModelDAO::getModel)
					.collect(Collectors.toList());
		} else {
			String contextValue = extractContextFromContextId(contextId, config.getContextFieldName());
			models =
					modelStore.getAllContextsModelDaosWithLatestEndTimeLte(modelConf, config.getContextFieldName(),
							contextValue, endTime.toInstant()).stream()
					.map(ModelDAO::getModel)
					.collect(Collectors.toList());
		}



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
			modelStore.ensureContextAndDateTimeIndex(modelConf, Collections.singletonList(config.getContextFieldName()));
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
		return config.getContextFieldName() == null ?
				Collections.emptyList() : Collections.singletonList(config.getContextFieldName());
	}

	@Override
	public String getContextId(Map<String, String> context) {
		return buildContextId(context);
	}

	public static String buildContextId(Map<String, String> context){
		return FeatureBucketUtils.buildContextId(context);
	}

	public static String extractContextFromContextId(String contextId, String contextFieldName){
		return FeatureBucketUtils.extractContextFromContextId(contextId, contextFieldName);
	}
}
