package fortscale.ml.model.retriever;

import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class EntityEventScoreRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
	@Override
	public String getFactoryName() {
		return EntityEventScoreRetrieverConf.ENTITY_EVENT_SCORE_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		return new EntityEventScoreRetriever((EntityEventScoreRetrieverConf) factoryConfig);
	}
}
