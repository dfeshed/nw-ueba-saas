package fortscale.ml.model.selector.factories;

import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.ml.model.selector.AggregatedEventContextSelector;
import fortscale.ml.model.selector.AggregatedEventContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class AggregatedEventContextSelectorFactory extends AbstractServiceAutowiringFactory<IContextSelector> {
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	@Autowired
	private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;

	@Override
	public String getFactoryName() {
		return AggregatedEventContextSelectorConf.AGGREGATED_EVENT_CONTEXT_SELECTOR;
	}

	@Override
	public IContextSelector getProduct(FactoryConfig factoryConfig) {
		AggregatedEventContextSelectorConf conf = (AggregatedEventContextSelectorConf)factoryConfig;
		return new AggregatedEventContextSelector(conf, aggregatedFeatureEventsConfService, aggregatedFeatureEventsReaderService);
	}
}
