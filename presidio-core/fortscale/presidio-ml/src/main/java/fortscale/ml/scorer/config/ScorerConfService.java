package fortscale.ml.scorer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.configuration.AslConfigurationService;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class ScorerConfService extends AslConfigurationService {
    private static final Logger logger = Logger.getLogger(ScorerConfService.class);
    private static final String ADE_EVENT_TYPE_SCORERS_NODE_NAME = "ade-event-type-scorers";

    private Map<String, AdeEventTypeScorerConfs> adeEventTypeToAdeEventTypeScorerConfsMap = new HashMap<>();

    @Override
    protected String getConfNodeName() {
        return ADE_EVENT_TYPE_SCORERS_NODE_NAME;
    }

    @Override
    protected void loadConfJson(JSONObject confJsonObject) {
        loadAdeEventTypeScorerConfs(confJsonObject);
    }

    public AdeEventTypeScorerConfs getAdeEventTypeScorerConfs(String adeEventType) {
        return adeEventTypeToAdeEventTypeScorerConfsMap.get(adeEventType);
    }

    public Map<String, AdeEventTypeScorerConfs> getAllAdeEventTypeScorerConfs() {
        return adeEventTypeToAdeEventTypeScorerConfsMap;
    }

    private void loadAdeEventTypeScorerConfs(JSONObject adeEventTypeScorerConfsJSON) {
        String errorMsg;
        ObjectMapper objectMapper = ObjectMapperProvider.getInstance().getDefaultObjectMapper();
        String jsonString = adeEventTypeScorerConfsJSON.toJSONString();

        try {
            AdeEventTypeScorerConfs adeEventTypeScorerConfs = objectMapper.readValue(jsonString, AdeEventTypeScorerConfs.class);
            String adeEventType = adeEventTypeScorerConfs.getAdeEventType();

            if (adeEventTypeToAdeEventTypeScorerConfsMap.containsKey(adeEventType)) {
                errorMsg = String.format(
                        "All scorers configuration for specific ade-event-type must be in the same file. %s scorer configurations appear multiple times.",
                        adeEventType);
                logger.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }

            adeEventTypeToAdeEventTypeScorerConfsMap.put(adeEventType, adeEventTypeScorerConfs);
        } catch (Exception e) {
            errorMsg = String.format("Failed to deserialize ade-event-type scorer confs JSON %s.", jsonString);
            logger.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }
    }
}
