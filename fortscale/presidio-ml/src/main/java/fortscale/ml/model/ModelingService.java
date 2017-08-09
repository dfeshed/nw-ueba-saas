package fortscale.ml.model;

import fortscale.aggregation.configuration.AslConfigurationPaths;
import fortscale.aggregation.configuration.AslResourceFactory;
import fortscale.utils.logging.Logger;
import org.springframework.core.io.Resource;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

	private Map<String, AslConfigurationPaths> groupNameToModelConfigurationPathsMap;
	private ModelingEngineFactory modelingEngineFactory;
	private AslResourceFactory aslResourceFactory;

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
			AslResourceFactory aslResourceFactory) {

		groupNameToModelConfigurationPathsMap = modelConfigurationPathsCollection.stream()
				.collect(Collectors.toMap(AslConfigurationPaths::getGroupName, Function.identity()));
		this.modelingEngineFactory = modelingEngineFactory;
		this.aslResourceFactory = aslResourceFactory;
	}

	/**
	 * @param groupName  the name of the logical group of modelConfs
	 * @param sessionId  the session ID of the models that are built
	 * @param endInstant the end time of the models that are built
	 */
	public void process(String groupName, String sessionId, Instant endInstant) throws Exception {
		if (!groupNameToModelConfigurationPathsMap.containsKey(groupName)) {
			logger.error("Group {} is not configured. Ending modeling service process.", groupName);
			return;
		}

		AslConfigurationPaths modelConfigurationPaths = groupNameToModelConfigurationPathsMap.get(groupName);
		ModelConfService modelConfService = new ModelConfService(
				aslResourceFactory.getResources(modelConfigurationPaths.getBaseConfigurationPath()),
				aslResourceFactory.getResources(modelConfigurationPaths.getOverridingConfigurationPath()),
				aslResourceFactory.getResources(modelConfigurationPaths.getAdditionalConfigurationPath()));
		modelConfService.loadAslConfigurations();
		List<ModelConf> modelConfs = modelConfService.getModelConfs();
		logger.info("Created a modelConfService for group {} with {} modelConfs.", groupName, modelConfs.size());
		logger.info("Running modeling engines with sessionId {} and endInstant {} as input.", sessionId, endInstant);

		for (ModelConf modelConf : modelConfs) {
			modelingEngineFactory.getModelingEngine(modelConf).process(sessionId, endInstant);
			logger.info("Finished modeling engine process of modelConf {}.", modelConf.getName());
		}
	}

	public void clean(String groupName, String sessionId) throws Exception {
		// TODO
		logger.info("Clean: groupName {}, sessionId {}.", groupName, sessionId);
	}
}
