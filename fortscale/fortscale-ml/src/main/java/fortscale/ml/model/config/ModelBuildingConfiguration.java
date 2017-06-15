package fortscale.ml.model.config;

import fortscale.ml.model.ModelConfServiceConfig;
import fortscale.ml.model.ModelServiceConfig;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.builder.factories.BuilderFactoriesConfig;
import fortscale.ml.model.retriever.factories.RetrieverFactoriesConfig;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.selector.factories.SelectorFactoriesConfig;
import fortscale.ml.model.store.ModelStoreConfig;
import fortscale.utils.factory.FactoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ModelConfServiceConfig.class,
		ModelServiceConfig.class,
		RetrieverFactoriesConfig.class,
		SelectorFactoriesConfig.class,
		BuilderFactoriesConfig.class,
		ModelStoreConfig.class,
		DataRetrieverFactoryServiceConfig.class
})
public class ModelBuildingConfiguration {

	@Bean
	public FactoryService<IContextSelector> contextSelectorFactoryService() {
		return new FactoryService<>();
	}

	@Bean
	public FactoryService<IModelBuilder> modelBuilderFactoryService() {
		return new FactoryService<>();
	}

}
