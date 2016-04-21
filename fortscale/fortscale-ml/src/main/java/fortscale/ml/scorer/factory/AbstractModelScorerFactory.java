package fortscale.ml.scorer.factory;

import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ModelInfo;
import fortscale.ml.scorer.config.ModelScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractModelScorerFactory extends AbstractServiceAutowiringFactory<Scorer> {
    @Autowired
    protected FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;

    @Autowired
    protected FactoryService<IContextSelector> contextSelectorFactoryService;

    @Autowired
    protected ModelConfService modelConfService;

    protected void validateModelScorerConf(ModelScorerConf modelScorerConf) {
        ModelInfo modelInfo = modelScorerConf.getModelInfo();

        if (modelConfService.getModelConf(modelInfo.getModelName()) == null) {
            throw new IllegalArgumentException(String.format(
                    "Model conf service does not contain a model conf named %s.",
                    modelInfo.getModelName()));
        }
    }
}
