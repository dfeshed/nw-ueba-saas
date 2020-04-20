package fortscale.ml.model.selector.factories;

import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.model.selector.AggregatedEventContextSelector;
import fortscale.ml.model.selector.AggregatedEventContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;

@SuppressWarnings("unused")
@Component
public class AggregatedEventContextSelectorFactory extends AbstractServiceAutowiringFactory<IContextSelector> {
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	@Autowired
	private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;

	@Override
	public String getFactoryName() {
		return AggregatedEventContextSelectorConf.AGGREGATED_EVENT_CONTEXT_SELECTOR;
	}

	@Override
	public IContextSelector getProduct(FactoryConfig factoryConfig) {
		AggregatedEventContextSelectorConf conf = (AggregatedEventContextSelectorConf)factoryConfig;
		return new AggregatedEventContextSelector(conf, aggregatedFeatureEventsConfService, aggregationEventsAccumulationDataReader);
	}
}
