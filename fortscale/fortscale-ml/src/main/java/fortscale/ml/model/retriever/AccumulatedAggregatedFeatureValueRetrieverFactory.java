package fortscale.ml.model.retriever;

import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class AccumulatedAggregatedFeatureValueRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
	@Override
	public String getFactoryName() {
		return AccumulatedAggregatedFeatureValueRetrieverConf.ACCUMULATED_AGGREGATED_FEATURE_VALUE_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		AccumulatedAggregatedFeatureValueRetrieverConf config = (AccumulatedAggregatedFeatureValueRetrieverConf)factoryConfig;
		return new AccumulatedAggregatedFeatureValueRetriever(config);
	}
}
