package presidio.ade.modeling.config;

import fortscale.aggregation.configuration.AslConfigurationPaths;
import fortscale.aggregation.configuration.AslResourceFactory;
import fortscale.ml.model.ModelingEngineFactory;
import fortscale.ml.model.ModelingService;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.config.ContextSelectorFactoryServiceConfig;
import fortscale.ml.model.config.DataRetrieverFactoryServiceConfig;
import fortscale.ml.model.config.ModelBuilderFactoryServiceConfig;
import fortscale.ml.model.metrics.CategoryRarityModeBuilderMetricsContainerConfig;
import fortscale.ml.model.metrics.CategoryRarityModeRetrieverMetricsContainerConfig;
import fortscale.ml.model.metrics.ModelingServiceMetricsContainer;
import fortscale.ml.model.metrics.ModelingServiceMetricsContainerConfig;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.store.ModelStore;
import fortscale.ml.model.store.ModelStoreConfig;
import fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithmConfig;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.StoreManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.modeling.ModelingServiceCommands;
import presidio.monitoring.flush.MetricContainerFlusher;
import presidio.monitoring.flush.MetricContainerFlusherConfig;

import java.util.Arrays;
import java.util.Collection;

@Configuration
@Import({
		SmartWeightsScorerAlgorithmConfig.class,
		ContextSelectorFactoryServiceConfig.class,
		DataRetrieverFactoryServiceConfig.class,
		ModelBuilderFactoryServiceConfig.class,
		ModelStoreConfig.class,
		ModelingServiceDependencies.class,
		ModelingServiceCommands.class,
		StoreManagerConfig.class,
		ModelingServiceMetricsContainerConfig.class,
		MetricContainerFlusherConfig.class,
})
public class ModelingServiceConfiguration {
	@Value("${presidio.ade.modeling.enriched.records.group.name}")
	private String enrichedRecordsGroupName;
	@Value("${presidio.ade.modeling.enriched.records.base.configuration.path}")
	private String enrichedRecordsBaseConfigurationPath;
	@Value("${presidio.ade.modeling.feature.aggregation.records.group.name}")
	private String featureAggrRecordsGroupName;
	@Value("${presidio.ade.modeling.feature.aggregation.records.base.configuration.path}")
	private String featureAggrRecordsBaseConfigurationPath;
	@Value("${presidio.ade.modeling.smart.records.group.name}")
	private String smartRecordsGroupName;
	@Value("${presidio.ade.modeling.smart.records.base.configuration.path}")
	private String smartRecordsBaseConfigurationPath;

	@Autowired
	private FactoryService<IContextSelector> contextSelectorFactoryService;
	@Autowired
	private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;
	@Autowired
	private FactoryService<IModelBuilder> modelBuilderFactoryService;
	@Autowired
	private ModelStore modelStore;
	@Autowired
	ModelingServiceMetricsContainer modelingServiceMetricsContainer;
	@Autowired
	private ModelingEngineFactory modelingEngineFactory;
	@Autowired
	private AslResourceFactory aslResourceFactory;
	@Autowired
	private StoreManager storeManager;
	@Autowired
	private MetricContainerFlusher metricContainerFlusher;

	@Bean
	public ModelingEngineFactory modelingEngineFactory() {
		return new ModelingEngineFactory(
				contextSelectorFactoryService,
				dataRetrieverFactoryService,
				modelBuilderFactoryService,
				modelStore,
				modelingServiceMetricsContainer);
	}

	@Bean
	public AslResourceFactory aslResourceFactory() {
		return new AslResourceFactory();
	}

	@Bean
	public ModelingService modelingService() {
		Collection<AslConfigurationPaths> modelConfigurationPathsCollection = Arrays.asList(
				new AslConfigurationPaths(enrichedRecordsGroupName, enrichedRecordsBaseConfigurationPath),
				new AslConfigurationPaths(featureAggrRecordsGroupName, featureAggrRecordsBaseConfigurationPath),
				new AslConfigurationPaths(smartRecordsGroupName, smartRecordsBaseConfigurationPath));
		return new ModelingService(modelConfigurationPathsCollection, modelingEngineFactory, aslResourceFactory, storeManager, metricContainerFlusher);
	}
}
