package fortscale.ml.scorer.factory;

import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.scorer.AbstractModelScorer;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractModelScorerFactory extends AbstractServiceAutowiringScorerFactory<AbstractModelScorer> {
    @Autowired
    protected FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;

    @Autowired
    protected ModelConfService modelConfService;
}
