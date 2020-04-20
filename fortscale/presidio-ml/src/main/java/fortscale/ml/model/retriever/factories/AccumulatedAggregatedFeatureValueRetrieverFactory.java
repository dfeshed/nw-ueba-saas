package fortscale.ml.model.retriever.factories;

import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.model.metrics.MaxContinuousModelRetrieverMetricsContainer;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AccumulatedAggregatedFeatureValueRetriever;
import fortscale.ml.model.retriever.AccumulatedAggregatedFeatureValueRetrieverConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;

@SuppressWarnings("unused")
@Component
public class AccumulatedAggregatedFeatureValueRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {

	@Autowired
	private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	@Autowired
	private MaxContinuousModelRetrieverMetricsContainer maxContinuousModelRetrieverMetricsContainer;

	@Override
	public String getFactoryName() {
		return AccumulatedAggregatedFeatureValueRetrieverConf.ACCUMULATED_AGGREGATED_FEATURE_VALUE_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		AccumulatedAggregatedFeatureValueRetrieverConf config = (AccumulatedAggregatedFeatureValueRetrieverConf)factoryConfig;
		return new AccumulatedAggregatedFeatureValueRetriever(config, aggregationEventsAccumulationDataReader, aggregatedFeatureEventsConfService, maxContinuousModelRetrieverMetricsContainer);
	}
}
