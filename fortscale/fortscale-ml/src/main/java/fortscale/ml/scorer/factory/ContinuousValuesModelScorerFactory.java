package fortscale.ml.scorer.factory;

import fortscale.ml.model.ModelConf;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.scorer.ContinuousValuesModelScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ContinuousValuesModelScorerConf;
import fortscale.ml.scorer.config.ModelInfo;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Component
public class ContinuousValuesModelScorerFactory extends AbstractModelScorerFactory {
	@Override
	public String getFactoryName() {
		return ContinuousValuesModelScorerConf.SCORER_TYPE;
	}

	@Override
	public Scorer getProduct(FactoryConfig factoryConfig) {
		ContinuousValuesModelScorerConf conf = (ContinuousValuesModelScorerConf)factoryConfig;
		super.validateModelScorerConf(conf);

		String modelName = conf.getModelInfo().getModelName();
		List<String> additionalModelNames = conf.getAdditionalModelInfos().stream()
				.map(ModelInfo::getModelName)
				.collect(Collectors.toList());
		AbstractDataRetriever dataRetriever = getDataRetriever(modelName);
		List<List<String>> additionalContextFieldNames = additionalModelNames.stream()
				.map(additionalModelName -> modelConfService
						.getModelConf(additionalModelName)
						.getContextSelectorConf() != null ?
						getDataRetriever(additionalModelName).getContextFieldNames() : new ArrayList<String>())
				.collect(Collectors.toList());
		String featureName = dataRetriever.getEventFeatureNames().iterator().next();

		return new ContinuousValuesModelScorer(
				conf.getName(), modelName, additionalModelNames, dataRetriever.getContextFieldNames(),
				additionalContextFieldNames, featureName, conf.getMinNumOfSamplesToInfluence(),
				conf.getEnoughNumOfSamplesToInfluence(), conf.isUseCertaintyToCalculateScore(),
				conf.getQuadPolyCalibrationConf());
	}

	private AbstractDataRetriever getDataRetriever(String modelName) {
		ModelConf modelConf = modelConfService.getModelConf(modelName);
		AbstractDataRetrieverConf dataRetrieverConf = modelConf.getDataRetrieverConf();
		return dataRetrieverFactoryService.getProduct(dataRetrieverConf);
	}
}
