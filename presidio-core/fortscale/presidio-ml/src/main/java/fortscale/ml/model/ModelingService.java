package fortscale.ml.model;

import fortscale.aggregation.configuration.AslConfigurationPaths;
import fortscale.aggregation.configuration.AslResourceFactory;
import fortscale.ml.model.metrics.ModelingServiceMetricsContainer;
import fortscale.utils.logging.Logger;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.record.StoreMetadataProperties;
import org.springframework.core.io.Resource;
import presidio.monitoring.flush.MetricContainerFlusher;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Given a group name, this service creates the corresponding {@link ModelConfService}, that contains all the
 * {@link ModelConf}s in the group. Then it creates a {@link ModelingEngine} for each {@link ModelConf} and runs
 * the engine with the session ID and the end instant as the input.
 * <p>
 * According to the session ID and the end instant, each {@link ModelingEngine} selects the context IDs,
 * retrieves their data, builds and stores their models.
 * <p>
 * This service requires a {@link ModelingEngineFactory} in order to create the {@link ModelingEngine}s and an
 * {@link AslResourceFactory}, that creates the configuration {@link Resource}s for the {@link ModelConfService}.
 *
 * @author Lior Govrin
 */
public class ModelingService {
	private static final Logger logger = Logger.getLogger(ModelingService.class);
	private static final String GROUP_NAME = "group_name";

	private ModelingEngineFactory modelingEngineFactory;
	private StoreManager storeManager;
	private ModelConfServiceBuilder modelConfServiceBuilder;
	private ModelingServiceMetricsContainer modelingServiceMetricsContainer;
	private  MetricContainerFlusher metricContainerFlusher;

	/**
	 * C'tor.
	 *
	 * @param modelConfigurationPathsCollection a collection of all configured groups and their configuration paths
	 * @param modelingEngineFactory             a factory that creates {@link ModelingEngine}s
	 * @param aslResourceFactory                a factory that creates {@link Resource}s
	 */
	public ModelingService(
			Collection<AslConfigurationPaths> modelConfigurationPathsCollection,
			ModelingEngineFactory modelingEngineFactory,
			AslResourceFactory aslResourceFactory, StoreManager storeManager,
			ModelingServiceMetricsContainer modelingServiceMetricsContainer,
			MetricContainerFlusher metricContainerFlusher) {
		this.modelConfServiceBuilder = new ModelConfServiceBuilder(modelConfigurationPathsCollection,aslResourceFactory);
		this.modelingEngineFactory = modelingEngineFactory;
		this.storeManager = storeManager;
		this.metricContainerFlusher = metricContainerFlusher;
		this.modelingServiceMetricsContainer = modelingServiceMetricsContainer;
	}

	/**
	 * @param groupName  the name of the logical group of modelConfs
	 * @param sessionId  the session ID of the models that are built
	 * @param endInstant the end time of the models that are built
	 */
	public void process(String groupName, String sessionId, Instant endInstant) throws Exception {

		try {
			List<ModelConf> modelConfs = getModelConfs(groupName);

			logger.info("Running modeling engines with sessionId {} and endInstant {} as input.", sessionId, endInstant);
			StoreMetadataProperties storeMetadataProperties = createStoreMetadataProperties(groupName);
			for (ModelConf modelConf : modelConfs) {

				modelingServiceMetricsContainer.addTags(groupName, modelConf.getName());

				ModelingEngine modelingEngine = modelingEngineFactory.getModelingEngine(modelConf);
				String modelConfName = modelConf.getName();
				try {
					modelingEngine.process(sessionId, endInstant, storeMetadataProperties);
				}
				catch (Exception e)
				{
					logger.error("encountered error while building model for modelConfName={}",modelConfName,e);
					throw e;
				}

				logger.info("Finished modeling engine process of modelConf {}.", modelConfName);
			}
			storeManager.cleanupCollections(storeMetadataProperties, endInstant);

			metricContainerFlusher.flush();
		}
		catch (Exception e)
		{
			logger.error("encountered error while building models for groupName={}, sessionId={}, endInstant={}",groupName, sessionId, endInstant,e);
			throw e;
		}
	}

	private List<ModelConf> getModelConfs(String groupName){
		ModelConfService modelConfService = modelConfServiceBuilder.buildModelConfService(groupName);
		DynamicModelConfServiceContainer.setModelConfService(modelConfService);
		List<ModelConf> modelConfs = modelConfService.getModelConfs();
		logger.info("Created a modelConfService for group {} with {} modelConfs.", groupName, modelConfs.size());
		return modelConfs;
	}





	public void clean(String groupName, String sessionId) throws Exception {
		// TODO
		logger.info("Clean: groupName {}, sessionId {}.", groupName, sessionId);
	}

	private StoreMetadataProperties createStoreMetadataProperties(String groupName){
		StoreMetadataProperties storeMetadataProperties = new StoreMetadataProperties();
		storeMetadataProperties.setProperty(GROUP_NAME, groupName);
		return storeMetadataProperties;
	}

}
