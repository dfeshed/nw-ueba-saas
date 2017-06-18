package fortscale.ml.scorer.factory;

import fortscale.ml.model.ModelConf;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.scorer.PersonalThresholdModelScorer;
import fortscale.ml.scorer.config.PersonalThresholdModelScorerConf;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@SuppressWarnings("unused")
@Component
public class PersonalThresholdModelScorerFactory extends AbstractModelScorerFactory {
	private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
			"factoryConfig must be an instance of %s.", PersonalThresholdModelScorerConf.class.getSimpleName());

	@Override
	public String getFactoryName() {
		return PersonalThresholdModelScorerConf.SCORER_TYPE;
	}

	@Override
	public PersonalThresholdModelScorer getProduct(FactoryConfig factoryConfig) {
		Assert.isInstanceOf(PersonalThresholdModelScorerConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);
		PersonalThresholdModelScorerConf scorerConf = (PersonalThresholdModelScorerConf) factoryConfig;
		String modelName = scorerConf.getModelInfo().getModelName();
		ModelConf modelConf = modelConfService.getModelConf(modelName);
		AbstractDataRetrieverConf dataRetrieverConf = modelConf.getDataRetrieverConf();
		AbstractDataRetriever abstractDataRetriever = dataRetrieverFactoryService.getProduct(dataRetrieverConf);
		List<String> contextFieldNames = abstractDataRetriever.getContextFieldNames();

		return new PersonalThresholdModelScorer(
				scorerConf.getName(),
				modelName,
				contextFieldNames,
				scorerConf.getBaseScorerConf(),
				scorerConf.getMaxRatioFromUniformThreshold()
		);
	}
}
