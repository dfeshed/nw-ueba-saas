package fortscale.ml.model.retriever.factories;

import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AccumulatedEntityEventValueRetriever;
import fortscale.ml.model.retriever.AccumulatedEntityEventValueRetrieverConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class AccumulatedEntityEventValueRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
	@Override
	public String getFactoryName() {
		return AccumulatedEntityEventValueRetrieverConf.ACCUMULATED_ENTITY_EVENT_VALUE_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		AccumulatedEntityEventValueRetrieverConf config = (AccumulatedEntityEventValueRetrieverConf)factoryConfig;
		return new AccumulatedEntityEventValueRetriever(config);
	}
}
