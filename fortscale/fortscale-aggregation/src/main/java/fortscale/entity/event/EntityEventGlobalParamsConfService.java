package fortscale.entity.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.configuration.AslConfigurationService;
import fortscale.aggregation.feature.event.AggrFeatureRetentionStrategy;
import fortscale.utils.logging.Logger;
import groovy.json.JsonException;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by YaronDL on 1/7/2016.
 */
public class EntityEventGlobalParamsConfService extends AslConfigurationService {
    private static final Logger logger = Logger.getLogger(EntityEventGlobalParamsConfService.class);

    private static final String GLOBAL_PARAMS_JSON_FIELD_NAME = "EntityEventsGlobalParams";

    @Value("${fortscale.entity.event.global.params.json.file.path}")
    private String entityEventGlobalParamsConfJsonFilePath;
    @Value("${fortscale.entity.event.global.params.conf.json.overriding.files.path}")
    private String entityEventGlobalParamsConfJsonOverridingFilesPath;

    private Map<String, Object> globalParams = new HashMap<>();

    @Override
    protected String getBaseConfJsonFilePath() {
        return entityEventGlobalParamsConfJsonFilePath;
    }

    @Override
    protected String getBaseOverridingConfJsonFolderPath() {
        return entityEventGlobalParamsConfJsonOverridingFilesPath;
    }

    @Override
    protected String getAdditionalConfJsonFolderPath() {
        return null;
    }

    @Override
    protected String getConfNodeName() {
        return GLOBAL_PARAMS_JSON_FIELD_NAME;
    }

    @Override
    protected void loadConfJson(JSONObject jsonObj){
        if (jsonObj == null) {
            String errorMsg = String.format("JSON file does not contain node name %s", GLOBAL_PARAMS_JSON_FIELD_NAME);
            logger.error(errorMsg);
            throw new JsonException(errorMsg);
        }

        for (Map.Entry<String, Object> entry : jsonObj.entrySet()) {
            globalParams.put(entry.getKey(), entry.getValue());
        }
    }

    public Map<String, Object> getGlobalParams() {
        return globalParams;
    }
}

