package fortscale.ml.model.message;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Collections;
import java.util.Set;

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

    private boolean selectHighScoreContexts;
    private String sessionId;
    private String modelConfName;
    private Long endTimeInSeconds;
    private Set<String> specifiedContextIds;

    /**
     * Default c'tor
     */
    public ModelBuildingCommandMessage() {
        specifiedContextIds = Collections.emptySet();
    }

    /**
     *  @param sessionId model building session
     * @param modelConfName can be specific model conf or "all_models"
     * @param endTimeInSeconds models will be built on data till that epoch second
     * @param selectHighScoreContexts
     */
    public ModelBuildingCommandMessage(String sessionId, String modelConfName, long endTimeInSeconds, boolean selectHighScoreContexts) {
        this();
        this.sessionId = sessionId;
        this.modelConfName = modelConfName;
        this.endTimeInSeconds = endTimeInSeconds;
        this.selectHighScoreContexts = selectHighScoreContexts;
    }

    public ModelBuildingCommandMessage(String sessionId, String modelConfName, Long endTimeInSeconds, boolean selectHighScoreContexts, Set<String> specifiedContextIds) {
        this(sessionId,modelConfName,endTimeInSeconds,selectHighScoreContexts);
        this.specifiedContextIds = specifiedContextIds;
    }

    /**
     *
     * @return ToString you know...
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
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

    public boolean isSelectHighScoreContexts() {
        return selectHighScoreContexts;
    }

    public void setSelectHighScoreContexts(boolean selectHighScoreContexts) {
        this.selectHighScoreContexts = selectHighScoreContexts;
    }

    public Set<String> getSpecifiedContextIds() {
        return specifiedContextIds;
    }

    public void setSpecifiedContextIds(Set<String> specifiedContextIds) {
        this.specifiedContextIds = specifiedContextIds;
    }
}
