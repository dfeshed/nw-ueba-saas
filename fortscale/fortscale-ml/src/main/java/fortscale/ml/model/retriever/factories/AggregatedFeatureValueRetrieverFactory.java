package fortscale.ml.model.retriever.factories;

import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AggregatedFeatureValueRetriever;
import fortscale.ml.model.retriever.AggregatedFeatureValueRetrieverConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class AggregatedFeatureValueRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
	@Override
	public String getFactoryName() {
		return AggregatedFeatureValueRetrieverConf.AGGREGATED_FEATURE_VALUE_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		AggregatedFeatureValueRetrieverConf config = (AggregatedFeatureValueRetrieverConf)factoryConfig;
		return new AggregatedFeatureValueRetriever(config);
	}
}
