package fortscale.ml.model.config;

import fortscale.ml.model.store.ModelStoreConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
		ContextSelectorFactoryServiceConfig.class,
//		DataRetrieverFactoryServiceConfig.class,
		ModelBuilderFactoryServiceConfig.class,
		ModelStoreConfig.class
})
public class ModelingEngineConfiguration {}
