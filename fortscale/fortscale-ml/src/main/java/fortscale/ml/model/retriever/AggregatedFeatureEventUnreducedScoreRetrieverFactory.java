package fortscale.ml.model.retriever;

import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class AggregatedFeatureEventUnreducedScoreRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
	@Override
	public String getFactoryName() {
		return AggregatedFeatureEventUnreducedScoreRetrieverConf.AGGREGATED_FEATURE_EVENT_UNREDUCED_SCORE_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		return new AggregatedFeatureEventUnreducedScoreRetriever((AggregatedFeatureEventUnreducedScoreRetrieverConf) factoryConfig);
	}
}
