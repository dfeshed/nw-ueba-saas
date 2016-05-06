package fortscale.ml.model.retriever;

import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class EntityEventUnreducedScoreRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
	@Override
	public String getFactoryName() {
		return EntityEventUnreducedScoreRetrieverConf.ENTITY_EVENT_UNREDUCED_SCORE_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		return new EntityEventUnreducedScoreRetriever((EntityEventUnreducedScoreRetrieverConf) factoryConfig);
	}
}
