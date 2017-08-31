package fortscale.ml.model;

import fortscale.aggregation.configuration.AslConfigurationPaths;
import fortscale.aggregation.configuration.AslResourceFactory;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

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
		try {
			if (!groupNameToModelConfigurationPathsMap.containsKey(groupName)) {
				logger.error("Group {} is not configured. Ending modeling service process.", groupName);
				return;
			}

			List<ModelConf> modelConfs = getModelConfs(groupName);

			logger.info("Running modeling engines with sessionId {} and endInstant {} as input.", sessionId, endInstant);
			for (ModelConf modelConf : modelConfs) {
				ModelingEngine modelingEngine = modelingEngineFactory.getModelingEngine(modelConf);
				String modelConfName = modelConf.getName();
				try {
					modelingEngine.process(sessionId, endInstant);
				}
				catch (Exception e)
				{
					logger.error("encountered error while building model for modelConfName={}",modelConfName,e);
					throw e;
				}

				logger.info("Finished modeling engine process of modelConf {}.", modelConfName);
			}
		}
		catch (Exception e)
		{
			logger.error("encountered error while building models for groupName={}, sessionId={}, endInstant={}",groupName, sessionId, endInstant,e);
			throw e;
		}
	}

	private List<ModelConf> getModelConfs(String groupName){
		String[] groupNames = groupName.split("\\.");
		Assert.isTrue(groupNames.length <= 2, "a group name is expected to contain at most root group and sub group.");
		String rootGroupName = groupNames[0];
		String subGroupName = groupNames.length < 2 ? "" : groupNames[1];
		AslConfigurationPaths modelConfigurationPaths = groupNameToModelConfigurationPathsMap.get(rootGroupName);
		ModelConfService modelConfService = new ModelConfService(
				getResources(modelConfigurationPaths.getBaseConfigurationPath(), subGroupName),
				getResources(modelConfigurationPaths.getOverridingConfigurationPath(), subGroupName),
				getResources(modelConfigurationPaths.getAdditionalConfigurationPath(), subGroupName));
		modelConfService.loadAslConfigurations();
		DynamicModelConfServiceContainer.setModelConfService(modelConfService);
		List<ModelConf> modelConfs = modelConfService.getModelConfs();
		logger.info("Created a modelConfService for group {} with {} modelConfs.", groupName, modelConfs.size());
		return modelConfs;
	}

	private Resource[] getResources(String rootGroupPath, String subGroupName){
		String path = StringUtils.isNotBlank(subGroupName)? rootGroupPath + subGroupName + ".json" : rootGroupPath + "*.json";
		return aslResourceFactory.getResources(path);
	}

	public void clean(String groupName, String sessionId) throws Exception {
		// TODO
		logger.info("Clean: groupName {}, sessionId {}.", groupName, sessionId);
	}
}
