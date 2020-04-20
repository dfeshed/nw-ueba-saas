package fortscale.ml.scorer.factory.smart;


import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.SmartWeightsModelScorer;
import fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithm;
import fortscale.ml.scorer.config.ModelInfo;
import fortscale.ml.scorer.config.SmartWeightsModelScorerConf;
import fortscale.ml.scorer.factory.AbstractModelScorerFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class SmartWeightsModelScorerFactory extends AbstractModelScorerFactory {

    @Autowired
    protected ModelConfService modelConfService;

    @Autowired
    protected EventModelsCacheService eventModelsCacheService;

    @Autowired
    private SmartWeightsScorerAlgorithm smartWeightsScorerAlgorithm;

    @Override
    public String getFactoryName() {
        return SmartWeightsModelScorerConf.SCORER_TYPE;
    }

    @Override
    public Scorer getProduct(FactoryConfig factoryConfig) {
        SmartWeightsModelScorerConf scorerConf = (SmartWeightsModelScorerConf) factoryConfig;
        validateModelScorerConf(scorerConf.getModelInfo());
        String modelName = scorerConf.getModelInfo().getModelName();

        return new SmartWeightsModelScorer(scorerConf.getName(),modelName,smartWeightsScorerAlgorithm,eventModelsCacheService);
    }

    protected void validateModelScorerConf(ModelInfo modelInfo) {
        if (modelConfService.getModelConf(modelInfo.getModelName()) == null) {
            throw new IllegalArgumentException(String.format(
                    "Model conf service does not contain a model conf named %s.",
                    modelInfo.getModelName()));
        }
    }
}
