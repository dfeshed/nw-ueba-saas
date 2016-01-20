package fortscale.streaming.service.scorer;


import fortscale.aggregation.configuration.AslConfigurationService;
import fortscale.utils.logging.Logger;
import groovy.json.JsonException;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class FeatureScoreJsonEventConfService extends AslConfigurationService {
    private static final Logger logger = Logger.getLogger(FeatureScoreJsonEventConfService.class);
    private static final String SCORE_MAPPING_JSON_FIELD_NAME = "score_mapping";

    @Value("${fortscale.streaming.scores.to.event.mapping.conf.json.file.path}")
    private String scoresToEventMappingConfJsonFilePath;
    @Value("${fortscale.streaming.scores.to.event.mapping.conf.json.overriding.file.path}")
    private String scoresToEventMappingConfConfJsonOverridingFilesPath;

    private Map<String, String> scoresToEventMap = new HashMap<>();

    @Override
    protected String getBaseConfJsonFilePath() {
        return scoresToEventMappingConfJsonFilePath;
    }

    @Override
    protected String getBaseOverridingConfJsonFolderPath() { return null;    }


    //Notice: I use the additional conf also for overiding. this means that only the values that are mentioned in the file are being overriden and not all the configuration.
    @Override
    protected String getAdditionalConfJsonFolderPath() {
        return scoresToEventMappingConfConfJsonOverridingFilesPath;
    }

    @Override
    protected String getConfNodeName() {
        return SCORE_MAPPING_JSON_FIELD_NAME;
    }

    @Override
    protected void loadConfJson(JSONObject jsonObj) {
        if (jsonObj == null) {
            String errorMsg = String.format("JSON file does not contain node name %s", SCORE_MAPPING_JSON_FIELD_NAME);
            logger.error(errorMsg);
            throw new JsonException(errorMsg);
        }

        for (Map.Entry<String, Object> entry : jsonObj.entrySet()) {
            scoresToEventMap.put(entry.getKey(), (String)entry.getValue());
        }
    }

    public Map<String, String> getScoresToEventMapping() {
        return scoresToEventMap;
    }
}
