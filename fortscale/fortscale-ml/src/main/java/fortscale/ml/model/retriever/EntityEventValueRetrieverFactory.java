package fortscale.ml.model.retriever;

import fortscale.entity.event.EntityEventDataCachedReaderService;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@Component
public class EntityEventValueRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {

	private Map<String, EntityEventDataCachedReaderService> entityEventDataCachedReaderServicesMap = new HashMap<>();

	@Override
	public String getFactoryName() {
		return EntityEventValueRetrieverConf.ENTITY_EVENT_VALUE_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		EntityEventValueRetrieverConf config = (EntityEventValueRetrieverConf)factoryConfig;
		String enttityEventConfName = config.getEntityEventConfName();
		EntityEventDataCachedReaderService entityEventDataCachedReaderService = entityEventDataCachedReaderServicesMap.get(enttityEventConfName);
		if(entityEventDataCachedReaderService == null) {
			entityEventDataCachedReaderService = new EntityEventDataCachedReaderService();
			entityEventDataCachedReaderServicesMap.put(enttityEventConfName, entityEventDataCachedReaderService);
		}
		return new EntityEventValueRetriever(config, entityEventDataCachedReaderService);
	}
}
