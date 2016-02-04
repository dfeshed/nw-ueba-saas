package fortscale.ml.scorer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.configuration.AslConfigurationService;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class ScorerConfService extends AslConfigurationService {
    private static final Logger logger = Logger.getLogger(ScorerConfService.class);
    private static final String DATA_SOURCE_SCORERS_NODE_NAME = "data-source-scorers";

    private String scorerConfigurationsLocationPath;
    private String scorerConfigurationsOverridingPath;
    private String scorerConfigurationsAdditionalPath;
    private Map<String, DataSourceScorerConfs> dataSourceToDataSourceScorerConfs = new HashMap<>();

    public abstract String loadScorerConfigurationsLocationPath();
    public abstract String loadScorerConfigurationsOverridingPath();
    public abstract String loadScorerConfigurationsAdditionalPath();

    @Override
    public void afterPropertiesSet() throws Exception {
        scorerConfigurationsLocationPath = loadScorerConfigurationsLocationPath();
        scorerConfigurationsOverridingPath = loadScorerConfigurationsOverridingPath();
        scorerConfigurationsAdditionalPath = loadScorerConfigurationsAdditionalPath();
        super.afterPropertiesSet();
    }

    @Override
    protected String getBaseConfJsonFilesPath() {
        return scorerConfigurationsLocationPath;
    }

    @Override
    protected String getBaseOverridingConfJsonFolderPath() {
        return scorerConfigurationsOverridingPath;
    }

    @Override
    protected String getAdditionalConfJsonFolderPath() {
        return scorerConfigurationsAdditionalPath;
    }

    @Override
    protected String getConfNodeName() {
        return DATA_SOURCE_SCORERS_NODE_NAME;
    }

    @Override
    protected void loadConfJson(JSONObject confJsonObject) {
        loadDataSourceScorerConfs(confJsonObject);
    }

    public DataSourceScorerConfs getDataSourceScorerConfs(String dataSource) {
        return dataSourceToDataSourceScorerConfs.get(dataSource);
    }

    public Map<String, DataSourceScorerConfs> getAllDataSourceScorerConfs() {
        return dataSourceToDataSourceScorerConfs;
    }

    private void loadDataSourceScorerConfs(JSONObject dataSourceScorerConfsJSON) {
        String errorMsg;
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = dataSourceScorerConfsJSON.toJSONString();

        try {
            DataSourceScorerConfs dataSourceScorerConfs = objectMapper.readValue(jsonString, DataSourceScorerConfs.class);
            String dataSource = dataSourceScorerConfs.getDataSource();

            if (dataSourceToDataSourceScorerConfs.containsKey(dataSource)) {
                errorMsg = String.format(
                        "All scorers configuration for specific data-source must be in the same file. %s scorer configurations appear multiple times.",
                        dataSource);
                logger.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }

            dataSourceToDataSourceScorerConfs.put(dataSource, dataSourceScorerConfs);
        } catch (Exception e) {
            errorMsg = String.format("Failed to deserialize data-source scorer confs JSON %s.", jsonString);
            logger.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }
    }
}
