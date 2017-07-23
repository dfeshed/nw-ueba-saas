package presidio.ade.processes.shell.modeling;

import fortscale.aggregation.configuration.AslConfigurationPaths;
import fortscale.aggregation.configuration.AslResourceFactory;
import fortscale.aggregation.feature.bucket.FeatureBucketReaderConfig;
import fortscale.aggregation.feature.bucket.config.BucketConfigurationServiceConfig;
import fortscale.ml.model.ModelingEngineFactory;
import fortscale.ml.model.ModelingService;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.config.ModelingEngineConfiguration;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collections;

@Configuration
@Import({MongoConfig.class, BucketConfigurationServiceConfig.class, FeatureBucketReaderConfig.class, ModelingEngineConfiguration.class})
public class ModelingServiceConfiguration {
	@Autowired
	@Qualifier("enrichedRecordModelConfigurationPaths")
	private AslConfigurationPaths enrichedRecordModelConfigurationPaths;

	@Autowired
	private FactoryService<IContextSelector> contextSelectorFactoryService;
	@Autowired
	private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;
	@Autowired
	private FactoryService<IModelBuilder> modelBuilderFactoryService;
	@Autowired
	private ModelStore modelStore;
	@Autowired
	private ModelingEngineFactory modelingEngineFactory;
	@Autowired
	private AslResourceFactory aslResourceFactory;

	@Bean("enrichedRecordModelConfigurationPaths")
	public AslConfigurationPaths enrichedRecordModelConfigurationPaths() {
		return new AslConfigurationPaths(
				"enriched-record-models",
				"classpath:config/asl/models/raw_events_model_confs_dlpfile.json",
				null,
				null);
	}

	@Bean
	public ModelingEngineFactory modelingEngineFactory() {
		return new ModelingEngineFactory(contextSelectorFactoryService, dataRetrieverFactoryService, modelBuilderFactoryService, modelStore);
	}

	@Bean
	public AslResourceFactory aslResourceFactory() {
		return new AslResourceFactory();
	}

	@Bean
	public ModelingService modelingService() {
		return new ModelingService(Collections.singleton(enrichedRecordModelConfigurationPaths), modelingEngineFactory, aslResourceFactory);
	}
}
