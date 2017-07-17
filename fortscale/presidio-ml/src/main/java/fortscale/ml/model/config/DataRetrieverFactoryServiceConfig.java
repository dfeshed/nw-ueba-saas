package fortscale.ml.model.config;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.aggregation.feature.bucket.FeatureBucketReaderConfig;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.processes.shell.model.aggregation.ModelAggregationBucketConfigurationServiceConfig;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collection;

@Configuration
@Import({ModelAggregationBucketConfigurationServiceConfig.class, FeatureBucketReaderConfig.class})
@ComponentScan("fortscale.ml.model.retriever.factories")
public class DataRetrieverFactoryServiceConfig {
	@Autowired
	@Qualifier("modelBucketConfigService")
	public BucketConfigurationService bucketConfigurationService;
	@Autowired
	private FeatureBucketReader featureBucketReader;
	@Autowired
	private Collection<AbstractServiceAutowiringFactory<AbstractDataRetriever>> dataRetrieverFactories;

	@Bean
	public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
		FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = new FactoryService<>();
		dataRetrieverFactories.forEach(factory -> factory.registerFactoryService(dataRetrieverFactoryService));
		return dataRetrieverFactoryService;
	}
}
