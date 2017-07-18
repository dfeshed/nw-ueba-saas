package fortscale.ml.model.retriever.factories;

import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AggregatedFeaturePersonalThresholdModelBuilderDataRetriever;
import fortscale.ml.model.retriever.AggregatedFeaturePersonalThresholdModelBuilderDataRetrieverConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class AggregatedFeaturePersonalThresholdModelBuilderDataRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	@Autowired
	private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;

	@Override
	public String getFactoryName() {
		return AggregatedFeaturePersonalThresholdModelBuilderDataRetrieverConf.AGGREGATED_FEATURE_PERSONAL_THRESHOLD_MODEL_BUILDER_DATA_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		return new AggregatedFeaturePersonalThresholdModelBuilderDataRetriever((AggregatedFeaturePersonalThresholdModelBuilderDataRetrieverConf) factoryConfig,
				aggregatedFeatureEventsConfService, aggregatedFeatureEventsReaderService);
	}
}
