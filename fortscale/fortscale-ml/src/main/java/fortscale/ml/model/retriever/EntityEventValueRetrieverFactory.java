package fortscale.ml.model.retriever;

import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class EntityEventValueRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
	@Override
	public String getFactoryName() {
		return EntityEventValueRetrieverConf.ENTITY_EVENT_VALUE_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		EntityEventValueRetrieverConf config = (EntityEventValueRetrieverConf)factoryConfig;
		return new EntityEventValueRetriever(config);
	}
}
