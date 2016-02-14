package fortscale.ml.scorer.factory;

import fortscale.ml.model.ModelConf;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.TimeModelScorer;
import fortscale.ml.scorer.config.TimeModelScorerConf;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class TimeModelScorerFactory extends AbstractModelScorerFactory {
	@Override
	public String getFactoryName() {
		return TimeModelScorerConf.SCORER_TYPE;
	}

	@Override
	public Scorer getProduct(FactoryConfig factoryConfig) {
		TimeModelScorerConf conf = (TimeModelScorerConf)factoryConfig;
		super.validateModelScorerConf(conf);

		String modelName = conf.getModelInfo().getModelName();
		ModelConf modelConf = modelConfService.getModelConf(modelName);
		AbstractDataRetrieverConf retrieverConf = modelConf.getDataRetrieverConf();
		AbstractDataRetriever retriever = dataRetrieverFactoryService.getProduct(retrieverConf);
		String featureName = retriever.getEventFeatureNames().iterator().next();

		return new TimeModelScorer(
				conf.getName(), modelName, retriever.getContextFieldNames(), featureName,
				conf.getMinNumOfSamplesToInfluence(), conf.getEnoughNumOfSamplesToInfluence(),
				conf.isUseCertaintyToCalculateScore(),
				conf.getMaxRareTimestampCount(), conf.getMaxNumOfRareTimestamps());
	}
}
