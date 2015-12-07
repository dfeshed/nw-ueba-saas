package fortscale.ml.model.config;

import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.selector.ContextSelector;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.factory.FactoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("fortscale.ml.model")
public class ModelBuildingConfiguration {
	// TODO: Annotate with @Service instead
	@Bean
	public ModelConfService modelConfService() {
		return new ModelConfService();
	}

	// TODO: Annotate with @Repository instead
	@Bean
	public ModelStore modelStore() {
		return new ModelStore();
	}

	@Bean
	public FactoryService<ContextSelector> contextSelectorFactoryService() {
		return new FactoryService<>();
	}

	@Bean
	public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
		return new FactoryService<>();
	}

	@Bean
	public FactoryService<IModelBuilder> modelBuilderFactoryService() {
		return new FactoryService<>();
	}
}
