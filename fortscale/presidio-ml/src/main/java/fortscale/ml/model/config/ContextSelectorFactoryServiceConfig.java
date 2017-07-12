package fortscale.ml.model.config;

import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.selector.factories.FeatureBucketContextSelectorFactory;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.util.Collection;

@Configuration
// TODO: Component scan should be un-commented
//@ComponentScan("fortscale.ml.model.selector.factories")
public class ContextSelectorFactoryServiceConfig {
//	@Autowired
//	private Collection<AbstractServiceAutowiringFactory<IContextSelector>> contextSelectorFactories;

	@Bean
	public FeatureBucketContextSelectorFactory featureBucketContextSelectorFactory() {
		return new FeatureBucketContextSelectorFactory();
	}

	@Bean
	public FactoryService<IContextSelector> contextSelectorFactoryService() {
		FactoryService<IContextSelector> contextSelectorFactoryService = new FactoryService<>();
//		contextSelectorFactories.forEach(factory -> factory.registerFactoryService(contextSelectorFactoryService));
		featureBucketContextSelectorFactory().registerFactoryService(contextSelectorFactoryService);
		return contextSelectorFactoryService;
	}
}
