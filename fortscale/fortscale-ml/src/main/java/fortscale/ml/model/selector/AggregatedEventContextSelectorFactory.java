package fortscale.ml.model.selector;

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
