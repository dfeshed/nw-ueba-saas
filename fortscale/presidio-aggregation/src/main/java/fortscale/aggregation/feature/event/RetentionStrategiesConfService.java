package fortscale.aggregation.feature.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.configuration.AslConfigurationService;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class RetentionStrategiesConfService extends AslConfigurationService {
	private static final Logger logger = Logger.getLogger(RetentionStrategiesConfService.class);
	private static final String ARRAY_OF_RETENTION_STRATEGIES_JSON_FIELD_NAME = "RetentionStrategies";

	@Value("${fortscale.aggregation.retention.strategy.conf.json.file.name}")
	private String retentionStrategyConfJsonFilePath;
	@Value("${fortscale.aggregation.retention.strategy.conf.json.overriding.files.path}")
	private String retentionStrategyConfJsonOverridingFilesPath;
	@Value("${fortscale.aggregation.retention.strategy.conf.json.additional.files.path}")
	private String retentionStrategyConfJsonAdditionalFilesPath;

	public RetentionStrategiesConfService(){}

	public RetentionStrategiesConfService(String retentionStrategyConfJsonFilePath,String retentionStrategyConfJsonOverridingFilesPath,String retentionStrategyConfJsonAdditionalFilesPath){
		this.retentionStrategyConfJsonFilePath = retentionStrategyConfJsonFilePath;
		this.retentionStrategyConfJsonOverridingFilesPath = retentionStrategyConfJsonOverridingFilesPath;
		this.retentionStrategyConfJsonAdditionalFilesPath = retentionStrategyConfJsonAdditionalFilesPath;
	}

	private Map<String, AggrFeatureRetentionStrategy> aggrFeatureRetentionStrategies = new HashMap<>();

	@Override
	protected String getBaseConfJsonFilesPath() {
		return retentionStrategyConfJsonFilePath;
	}

	@Override
	protected String getBaseOverridingConfJsonFolderPath() {
		return retentionStrategyConfJsonOverridingFilesPath;
	}

	@Override
	protected String getAdditionalConfJsonFolderPath() {
		return retentionStrategyConfJsonAdditionalFilesPath;
	}

	@Override
	protected String getConfNodeName() {
		return ARRAY_OF_RETENTION_STRATEGIES_JSON_FIELD_NAME;
	}

	@Override
	protected void loadConfJson(JSONObject jsonObj) {
		String confAsString = jsonObj.toJSONString();
		try {
			AggrFeatureRetentionStrategy aggrFeatureRetentionStrategy = (new ObjectMapper()).readValue(confAsString, AggrFeatureRetentionStrategy.class);
			aggrFeatureRetentionStrategies.put(aggrFeatureRetentionStrategy.getName(), aggrFeatureRetentionStrategy);
		} catch (Exception e) {
			String errorMsg = String.format("Failed to deserialize JSON %s", confAsString);
			logger.error(errorMsg, e);
			throw new RuntimeException(errorMsg, e);
		}
	}

	public AggrFeatureRetentionStrategy getAggrFeatureRetentionStrategy(String retentionStrategyName) {
		return aggrFeatureRetentionStrategies.get(retentionStrategyName);
	}
}
