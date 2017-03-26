package fortscale.ml.model.selector.factories;

import fortscale.ml.model.selector.EntityEventContextSelector;
import fortscale.ml.model.selector.EntityEventContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class EntityEventContextSelectorFactory extends AbstractServiceAutowiringFactory<IContextSelector> {
	@Override
	public String getFactoryName() {
		return EntityEventContextSelectorConf.ENTITY_EVENT_CONTEXT_SELECTOR;
	}

	@Override
	public IContextSelector getProduct(FactoryConfig factoryConfig) {
		EntityEventContextSelectorConf config = (EntityEventContextSelectorConf)factoryConfig;
		return new EntityEventContextSelector(config);
	}
}
