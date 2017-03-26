package fortscale.ml.model.selector.factories;

import fortscale.ml.model.selector.AggregatedEventContextSelector;
import fortscale.ml.model.selector.AggregatedEventContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class AggregatedEventContextSelectorFactory extends AbstractServiceAutowiringFactory<IContextSelector> {
	@Override
	public String getFactoryName() {
		return AggregatedEventContextSelectorConf.AGGREGATED_EVENT_CONTEXT_SELECTOR;
	}

	@Override
	public IContextSelector getProduct(FactoryConfig factoryConfig) {
		AggregatedEventContextSelectorConf config = (AggregatedEventContextSelectorConf)factoryConfig;
		return new AggregatedEventContextSelector(config);
	}
}
