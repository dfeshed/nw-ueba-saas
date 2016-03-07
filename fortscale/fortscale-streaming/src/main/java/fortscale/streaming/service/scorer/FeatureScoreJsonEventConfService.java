package fortscale.streaming.service.scorer;

import fortscale.aggregation.configuration.AslConfigurationService;
import fortscale.streaming.service.FortscaleValueResolver;
import fortscale.utils.logging.Logger;
import groovy.json.JsonException;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

public class FeatureScoreJsonEventConfService extends AslConfigurationService {
    private static final Logger logger = Logger.getLogger(FeatureScoreJsonEventConfService.class);
    private static final String SCORE_MAPPING_JSON_FIELD_NAME = "score_mapping";
    private static final String FULL_SCORER_NAME_SEPARATOR = "#";

    @Value("${fortscale.streaming.scores.to.event.mapping.conf.json.file.path}")
    private String scoresToEventMappingConfJsonFilePath;
    @Value("${fortscale.streaming.scores.to.event.mapping.conf.json.overriding.file.path}")
    private String scoresToEventMappingConfConfJsonOverridingFilesPath;

    @Autowired
    private FortscaleValueResolver fortscaleValueResolver;

    private Map<String, Map<String, List<String>>> rootScorersMap = new HashMap<>();

    @Override
    protected String getBaseConfJsonFilesPath() {
        return scoresToEventMappingConfJsonFilePath;
    }

    @Override
    protected String getBaseOverridingConfJsonFolderPath() {
        return null;
    }

    // Notice: I use the additional conf also for overriding.
    // This means that only the values that are mentioned in
    // the file are being overridden and not all the configuration.
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
            List<String> scorePath = Arrays.asList(entry.getKey().split(FULL_SCORER_NAME_SEPARATOR));
            String rootScorer = scorePath.get(0);
            Map<String, List<String>> eventFieldNameToScorerPath = rootScorersMap.get(rootScorer);
            if (eventFieldNameToScorerPath == null) {
                eventFieldNameToScorerPath = new HashMap<>();
                rootScorersMap.put(rootScorer, eventFieldNameToScorerPath);
            }
            String fieldName = fortscaleValueResolver.resolveStringValue((String)entry.getValue());
            eventFieldNameToScorerPath.put(fieldName, scorePath);
        }
    }

    public Map<String, List<String>> getEventFieldNameToScorerPathMap(String rootScorer) {
        Map<String, List<String>> ret = rootScorersMap.get(rootScorer);
        return ret != null ? ret : Collections.emptyMap();
    }

    public Set<List<String>> getAllScorerNamePaths() {
        Set<List<String>> allScorerNamePaths = new HashSet<>();
        for (Map<String, List<String>> map : rootScorersMap.values())
            map.values().forEach(allScorerNamePaths::add);
        return allScorerNamePaths;
    }
}
