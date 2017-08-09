package fortscale.ml.model.config;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Configuration
@ComponentScan("fortscale.ml.model.builder.factories")
public class ModelBuilderFactoryServiceConfig {
	@Autowired
	private Collection<AbstractServiceAutowiringFactory<IModelBuilder>> modelBuilderFactories;

	@Bean
	public FactoryService<IModelBuilder> modelBuilderFactoryService() {
		FactoryService<IModelBuilder> modelBuilderFactoryService = new FactoryService<>();
		modelBuilderFactories.forEach(factory -> factory.registerFactoryService(modelBuilderFactoryService));
		return modelBuilderFactoryService;
	}
}
