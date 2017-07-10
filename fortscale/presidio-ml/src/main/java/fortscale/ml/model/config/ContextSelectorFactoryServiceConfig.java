package fortscale.ml.model.config;

import fortscale.ml.model.selector.IContextSelector;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Configuration
@ComponentScan("fortscale.ml.model.selector.factories")
public class ContextSelectorFactoryServiceConfig {
	@Autowired
	private Collection<AbstractServiceAutowiringFactory<IContextSelector>> contextSelectorFactories;

	@Bean
	public FactoryService<IContextSelector> contextSelectorFactoryService() {
		FactoryService<IContextSelector> contextSelectorFactoryService = new FactoryService<>();
		contextSelectorFactories.forEach(factory -> factory.registerFactoryService(contextSelectorFactoryService));
		return contextSelectorFactoryService;
	}
}
