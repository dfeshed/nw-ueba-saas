package fortscale.ml.model.config;

import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Configuration
@ComponentScan("fortscale.ml.model.retriever.factories")
public class DataRetrieverFactoryServiceConfig {
	@Autowired
	private Collection<AbstractServiceAutowiringFactory<AbstractDataRetriever>> dataRetrieverFactories;

	@Bean
	public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
		FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = new FactoryService<>();
		dataRetrieverFactories.forEach(factory -> factory.registerFactoryService(dataRetrieverFactoryService));
		return dataRetrieverFactoryService;
	}
}
