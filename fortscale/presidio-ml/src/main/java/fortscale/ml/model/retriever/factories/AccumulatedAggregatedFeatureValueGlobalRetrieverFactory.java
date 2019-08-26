package fortscale.ml.model.retriever.factories;


import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.model.metrics.AccumulatedAggregatedFeatureValueGlobalRetrieverMetricsContainer;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AccumulatedAggregatedFeatureValueGlobalRetriever;
import fortscale.ml.model.retriever.AccumulatedAggregatedFeatureValueGlobalRetrieverConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;

@SuppressWarnings("unused")
@Component
public class AccumulatedAggregatedFeatureValueGlobalRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {

    @Autowired
    private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    @Autowired
    private AccumulatedAggregatedFeatureValueGlobalRetrieverMetricsContainer metricsContainer;

    @Override
    public String getFactoryName() {
        return AccumulatedAggregatedFeatureValueGlobalRetrieverConf.ACCUMULATED_AGGREGATED_FEATURE_VALUE_GLOBAL_RETRIEVER;
    }

    @Override
    public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
        AccumulatedAggregatedFeatureValueGlobalRetrieverConf config = (AccumulatedAggregatedFeatureValueGlobalRetrieverConf)factoryConfig;
        return new AccumulatedAggregatedFeatureValueGlobalRetriever(config, aggregationEventsAccumulationDataReader, aggregatedFeatureEventsConfService, metricsContainer);
    }
}
