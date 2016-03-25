package fortscale.ml.model.config;

import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.factory.FactoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
		basePackages = "fortscale.ml.model",
		excludeFilters = {
				@ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.ml.model.prevalance.*"),
				@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class)
		}
)
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
	public FactoryService<IContextSelector> contextSelectorFactoryService() {
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
