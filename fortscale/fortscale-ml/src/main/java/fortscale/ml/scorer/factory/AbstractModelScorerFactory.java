package fortscale.ml.scorer.factory;

import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.utils.factory.Factory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractModelScorerFactory<T> extends AbstractServiceAutowiringScorerFactory implements Factory {

    @Autowired
    FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;

    @Autowired
    protected ModelConfService modelConfService;
}
