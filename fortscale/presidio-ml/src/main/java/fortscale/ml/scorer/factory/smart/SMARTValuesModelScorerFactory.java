package fortscale.ml.scorer.factory.smart;

import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.scorer.SMARTValuesModelScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ModelInfo;
import fortscale.ml.scorer.config.SMARTValuesModelScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class SMARTValuesModelScorerFactory extends AbstractServiceAutowiringFactory<Scorer> {

    @Autowired
    protected ModelConfService modelConfService;

    @Autowired
    protected EventModelsCacheService eventModelsCacheService;

    @Override
    public String getFactoryName() {
        return SMARTValuesModelScorerConf.SCORER_TYPE;
    }



    @Override
    public Scorer getProduct(FactoryConfig factoryConfig) {
        SMARTValuesModelScorerConf scorerConf = (SMARTValuesModelScorerConf) factoryConfig;
        validateModelConf(scorerConf.getModelInfo());
        validateModelConf(scorerConf.getGlobalModelInfo());
        String modelName = scorerConf.getModelInfo().getModelName();
        return new SMARTValuesModelScorer(
                scorerConf.getName(),
                scorerConf.getModelInfo().getModelName(),
                scorerConf.getGlobalModelInfo().getModelName(),
                scorerConf.getMinNumOfPartitionsToInfluence(),
                scorerConf.getEnoughNumOfPartitionsToInfluence(),
                scorerConf.isUseCertaintyToCalculateScore(),
                scorerConf.getBaseScorerConf(),
                scorerConf.getGlobalInfluence(),
                factoryService,
                eventModelsCacheService);
    }

    protected void validateModelConf(ModelInfo modelInfo) {
        if (modelConfService.getModelConf(modelInfo.getModelName()) == null) {
            throw new IllegalArgumentException(String.format(
                    "Model conf service does not contain a model conf named %s.",
                    modelInfo.getModelName()));
        }
    }
}
