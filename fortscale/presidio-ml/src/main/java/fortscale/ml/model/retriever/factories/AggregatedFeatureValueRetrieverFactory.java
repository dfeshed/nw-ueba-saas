package fortscale.ml.model.retriever.factories;

import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AggregatedFeatureValueRetriever;
import fortscale.ml.model.retriever.AggregatedFeatureValueRetrieverConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class AggregatedFeatureValueRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
	@Autowired
	private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	@Override
	public String getFactoryName() {
		return AggregatedFeatureValueRetrieverConf.AGGREGATED_FEATURE_VALUE_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		AggregatedFeatureValueRetrieverConf config = (AggregatedFeatureValueRetrieverConf)factoryConfig;
		return new AggregatedFeatureValueRetriever(config, aggregatedFeatureEventsReaderService, aggregatedFeatureEventsConfService);
	}
}
