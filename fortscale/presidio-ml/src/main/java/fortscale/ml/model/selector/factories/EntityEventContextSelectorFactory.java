package fortscale.ml.model.selector.factories;

import fortscale.entity.event.EntityEventConfService;
import fortscale.entity.event.EntityEventDataReaderService;
import fortscale.entity.event.EntityEventDataReaderServiceConfig;
import fortscale.entity.event.config.EntityEventConfServiceConfig;
import fortscale.ml.model.selector.EntityEventContextSelector;
import fortscale.ml.model.selector.EntityEventContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Import({EntityEventConfServiceConfig.class,EntityEventDataReaderServiceConfig.class})
public class EntityEventContextSelectorFactory extends AbstractServiceAutowiringFactory<IContextSelector> {
	@Autowired
	private EntityEventConfService entityEventConfService;
	@Autowired
	private EntityEventDataReaderService entityEventDataReaderService;

	@Override
	public String getFactoryName() {
		return EntityEventContextSelectorConf.ENTITY_EVENT_CONTEXT_SELECTOR;
	}

	@Override
	public IContextSelector getProduct(FactoryConfig factoryConfig) {
		EntityEventContextSelectorConf conf = (EntityEventContextSelectorConf)factoryConfig;
		return new EntityEventContextSelector(conf, entityEventConfService, entityEventDataReaderService);
	}
}
