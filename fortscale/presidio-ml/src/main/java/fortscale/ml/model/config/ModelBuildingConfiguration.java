package fortscale.ml.model.config;

import fortscale.ml.model.ModelConfServiceConfig;
import fortscale.ml.model.ModelServiceConfig;
import fortscale.ml.model.store.ModelStoreConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ModelConfServiceConfig.class,
		ModelServiceConfig.class,
		ContextSelectorFactoryConfig.class,
		ModelBuilderFactoryServiceConfig.class,
		ModelStoreConfig.class,
		DataRetrieverFactoryConfig.class
})
public class ModelBuildingConfiguration {}
