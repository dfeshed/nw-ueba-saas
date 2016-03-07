package fortscale.ml.scorer.config.production;

import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.scorer.Scorer;
import fortscale.utils.factory.FactoryService;
import org.mockito.Mock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Collections;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@ComponentScan(basePackages = "fortscale.ml.scorer.factory")
public class ProductionScorerConfFilesTestContext {
	// Following mock is required and injected when creating abstract model scorers via their factories.
	@Mock private EventModelsCacheService eventModelsCacheService;
	@Bean public EventModelsCacheService eventModelsCacheService() {return eventModelsCacheService;}

	@Bean
	public ModelConfService modelConfService() {
		return new ModelConfService();
	}

	@Bean
	public FactoryService<Scorer> scorerFactoryService() {
		return new FactoryService<>();
	}

	@Bean
	public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		Properties properties = new Properties();
		properties.put("fortscale.model.configurations.location.path", "classpath:config/asl/models");

		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		configurer.setProperties(properties);
		return configurer;
	}

	@SuppressWarnings("unchecked")
	@Bean
	public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
		AbstractDataRetriever dataRetriever = mock(AbstractDataRetriever.class);
		when(dataRetriever.getEventFeatureNames()).thenReturn(Collections.singleton("myEventFeature"));
		when(dataRetriever.getContextFieldNames()).thenReturn(Collections.singletonList("myContextField"));

		FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = mock(FactoryService.class);
		when(dataRetrieverFactoryService.getProduct(any(AbstractDataRetrieverConf.class))).thenReturn(dataRetriever);
		return dataRetrieverFactoryService;
	}
}
