package fortscale.ml.scorer.factory;

import fortscale.ml.model.ModelConf;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.scorer.ModelBasedScoreMapper;
import fortscale.ml.scorer.config.ModelBasedScoreMapperConf;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@Component
public class ModelBasedScoreMapperFactory extends AbstractModelScorerFactory {
	private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
			"factoryConfig must be an instance of %s.", ModelBasedScoreMapperConf.class.getSimpleName());

	@Override
	public String getFactoryName() {
		return ModelBasedScoreMapperConf.SCORER_TYPE;
	}

	@Override
	public ModelBasedScoreMapper getProduct(FactoryConfig factoryConfig) {
		Assert.isInstanceOf(ModelBasedScoreMapperConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);
		ModelBasedScoreMapperConf scorerConf = (ModelBasedScoreMapperConf) factoryConfig;
		String modelName = scorerConf.getModelInfo().getModelName();
		ModelConf modelConf = modelConfService.getModelConf(modelName);
		AbstractDataRetrieverConf dataRetrieverConf = modelConf.getDataRetrieverConf();
		AbstractDataRetriever abstractDataRetriever = dataRetrieverFactoryService.getProduct(dataRetrieverConf);
		Set<String> featureNames = abstractDataRetriever.getEventFeatureNames();
		List<String> contextFieldNames = modelConf.getContextSelectorConf() != null ?
				abstractDataRetriever.getContextFieldNames() : Collections.emptyList();

		// Currently in this implementation we use only single feature per model.
		String featureName = featureNames.iterator().next();

		return new ModelBasedScoreMapper(scorerConf.getName(),
				modelName,
				contextFieldNames,
				featureName,
				scorerConf.getBaseScorerConf()
		);
	}
}
