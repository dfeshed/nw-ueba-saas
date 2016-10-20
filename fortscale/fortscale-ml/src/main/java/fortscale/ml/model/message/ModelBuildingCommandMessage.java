package fortscale.ml.model.message;

/**
 * command message that orders ModelBuildingStreamTask to build models (by conf name) via input control topic
 * i.e.
 * {
 * "sessionId": "modelBuildNumber_1",
 * "modelConfName": "all_models",
 * "endTimeInSeconds": 1476943652
 * }
 * Created by barak_schuster on 10/19/16.
 */
public class ModelBuildingCommandMessage {

    private String sessionId;
    private String modelConfName;
    private Long endTimeInSeconds;

    /**
     * Default c'tor
     */
    public ModelBuildingCommandMessage() {
    }

    /**
     *
     * @param sessionId model building session
     * @param modelConfName can be specific model conf or "all_models"
     * @param endTimeInSeconds models will be built on data till that epoch second
     */
    public ModelBuildingCommandMessage(String sessionId, String modelConfName, long endTimeInSeconds) {
        this.sessionId = sessionId;
        this.modelConfName = modelConfName;
        this.endTimeInSeconds = endTimeInSeconds;
    }

    // --- Getters/setters ---

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getModelConfName() {
        return modelConfName;
    }

    public void setModelConfName(String modelConfName) {
        this.modelConfName = modelConfName;
    }

    public Long getEndTimeInSeconds() {
        return endTimeInSeconds;
    }

    public void setEndTimeInSeconds(Long endTimeInSeconds) {
        this.endTimeInSeconds = endTimeInSeconds;
    }
}
