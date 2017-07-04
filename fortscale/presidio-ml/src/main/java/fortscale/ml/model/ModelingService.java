package fortscale.ml.model;

import fortscale.aggregation.configuration.AslConfigurationPaths;
import fortscale.utils.logging.Logger;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Given a group name, this service creates the corresponding {@link ModelConfService}, that contains all the
 * {@link ModelConf}s in the group. Then it creates a {@link ModelingEngine} for each {@link ModelConf} and runs
 * the engine with the run ID and the end instant as the input.
 * <p>
 * According to the run ID and the end instant, each {@link ModelingEngine} selects the context IDs, retrieves their
 * data, builds and stores their models.
 * <p>
 * This service requires a {@link ModelingEngineFactory} in order to create the {@link ModelingEngine}s.
 *
 * @author Lior Govrin
 */
public class ModelingService {
	private static final Logger logger = Logger.getLogger(ModelingService.class);

	private Map<String, AslConfigurationPaths> groupNameToModelConfigurationPathsMap;
	private ModelingEngineFactory modelingEngineFactory;

	/**
	 * C'tor.
	 *
	 * @param modelConfigurationPathsCollection a collection of all configured groups and their configuration paths
	 * @param modelingEngineFactory             a factory that creates {@link ModelingEngine}s
	 */
	public ModelingService(
			Collection<AslConfigurationPaths> modelConfigurationPathsCollection,
			ModelingEngineFactory modelingEngineFactory) {

		groupNameToModelConfigurationPathsMap = modelConfigurationPathsCollection.stream()
				.collect(Collectors.toMap(AslConfigurationPaths::getGroupName, Function.identity()));
		this.modelingEngineFactory = modelingEngineFactory;
	}

	/**
	 * @param groupName  the name of the logical group of modelConfs
	 * @param runId      the run ID of the models that are built
	 * @param endInstant the end time of the models that are built
	 */
	public void process(String groupName, String runId, Instant endInstant) throws Exception {
		if (!groupNameToModelConfigurationPathsMap.containsKey(groupName)) {
			logger.error("Group {} is not configured. Ending modeling service process.", groupName);
			return;
		}

		ModelConfService modelConfService = new ModelConfService(groupNameToModelConfigurationPathsMap.get(groupName));
		modelConfService.afterPropertiesSet(); // Load all the modelConfs in the group
		List<ModelConf> modelConfs = modelConfService.getModelConfs();
		logger.info("Created a modelConfService for group {} with {} modelConfs.", groupName, modelConfs.size());
		logger.info("Going to run modeling engines with runId {} and endInstant {} as input.", runId, endInstant);

		for (ModelConf modelConf : modelConfs) {
			modelingEngineFactory.getModelingEngine(modelConf).process(runId, endInstant);
			logger.info("Finished modeling engine process of modelConf {}.", modelConf.getName());
		}
	}
}
