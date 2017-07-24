package fortscale.ml.model.retriever.factories;

import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AggregatedFeatureEventUnreducedScoreRetriever;
import fortscale.ml.model.retriever.AggregatedFeatureEventUnreducedScoreRetrieverConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class AggregatedFeatureEventUnreducedScoreRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {

	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	@Autowired
	private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;

	@Override
	public String getFactoryName() {
		return AggregatedFeatureEventUnreducedScoreRetrieverConf.AGGREGATED_FEATURE_EVENT_UNREDUCED_SCORE_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		return new AggregatedFeatureEventUnreducedScoreRetriever((AggregatedFeatureEventUnreducedScoreRetrieverConf) factoryConfig,
				aggregatedFeatureEventsConfService, aggregatedFeatureEventsReaderService);
	}
}
