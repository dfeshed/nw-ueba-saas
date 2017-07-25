package fortscale.ml.model.retriever.factories;

import fortscale.accumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AccumulatedAggregatedFeatureValueRetriever;
import fortscale.ml.model.retriever.AccumulatedAggregatedFeatureValueRetrieverConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class AccumulatedAggregatedFeatureValueRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {

	@Autowired
	private AccumulatedAggregatedFeatureEventStore store;
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

	@Override
	public String getFactoryName() {
		return AccumulatedAggregatedFeatureValueRetrieverConf.ACCUMULATED_AGGREGATED_FEATURE_VALUE_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		AccumulatedAggregatedFeatureValueRetrieverConf config = (AccumulatedAggregatedFeatureValueRetrieverConf)factoryConfig;
		return new AccumulatedAggregatedFeatureValueRetriever(config, store, aggregatedFeatureEventsConfService);
	}
}
