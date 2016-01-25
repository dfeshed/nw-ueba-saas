package fortscale.ml.scorer.factory;

import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import javax.validation.constraints.NotNull;

public abstract class AbstractModelScorerFactory<T> extends AbstractServiceAutowiringScorerFactory implements ModelScorerFactory {

    @Autowired
    FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;

    @Autowired
    protected ModelConfService modelConfService;
}
