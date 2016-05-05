package fortscale.ml.scorer.factory;

import fortscale.ml.model.ModelConf;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.scorer.SMARTValuesModelScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ModelInfo;
import fortscale.ml.scorer.config.SMARTValuesModelScorerConf;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Component
public class SMARTValuesModelScorerFactory extends AbstractModelScorerFactory {
    @Override
    public String getFactoryName() {
        return SMARTValuesModelScorerConf.SCORER_TYPE;
    }

    @Override
    public Scorer getProduct(FactoryConfig factoryConfig) {
        //TODO: all this code (which is duplicated in CategoryRarityModelScorerFactory and ContinuousValuesModelScorerFactory should be refactored
        SMARTValuesModelScorerConf scorerConf = (SMARTValuesModelScorerConf) factoryConfig;
        super.validateModelScorerConf(scorerConf);
        String modelName = scorerConf.getModelInfo().getModelName();
        List<String> additionalModelNames = scorerConf.getAdditionalModelInfos().stream()
                .map(ModelInfo::getModelName)
                .collect(Collectors.toList());
        AbstractDataRetriever dataRetriever = getDataRetriever(modelName);
        List<String> contextFieldNames = dataRetriever.getContextFieldNames();
        List<List<String>> additionalContextFieldNames = additionalModelNames.stream()
				.map(additionalModelName -> modelConfService
						.getModelConf(additionalModelName)
						.getContextSelectorConf() != null ?
						getDataRetriever(additionalModelName).getContextFieldNames() : new ArrayList<String>())
				.collect(Collectors.toList());
		Set<String> featureNames = dataRetriever.getEventFeatureNames();

        // Currently in this implementation we use only single feature per model.
        String featureName = featureNames.iterator().next();

        return new SMARTValuesModelScorer(
                scorerConf.getName(),
                modelName,
                additionalModelNames,
                contextFieldNames,
                additionalContextFieldNames,
                featureName,
                scorerConf.getMinNumOfSamplesToInfluence(),
                scorerConf.getEnoughNumOfSamplesToInfluence(),
                scorerConf.isUseCertaintyToCalculateScore(),
                scorerConf.getGlobalInfluence()
        );
    }

    private AbstractDataRetriever getDataRetriever(String modelName) {
        ModelConf modelConf = modelConfService.getModelConf(modelName);
        AbstractDataRetrieverConf dataRetrieverConf = modelConf.getDataRetrieverConf();
        return dataRetrieverFactoryService.getProduct(dataRetrieverConf);
    }
}
