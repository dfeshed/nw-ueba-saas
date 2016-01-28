package fortscale.ml.scorer.factory;

import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.scorer.Scorer;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractModelScorerFactory extends AbstractServiceAutowiringFactory<Scorer> {
    @Autowired
    protected FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;

    @Autowired
    protected ModelConfService modelConfService;
}
