package fortscale.ml.model.message;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * represents model building status for contextId (user) in a model building session
 * i.e.
 * {
 * "modelConfName": "date_time_unix.normalized_username.ntlm",
 * "endTimeInSeconds": 1476748800,
 * "isSuccessful": true,
 * "contextId": "normalized_username###baraks@fortscale.com",
 * "details": "Successful model building",
 * "sessionId": "modelBuildNumber_1"
 * }
 * <p>
 * Created by barak_schuster on 10/19/16.
 */
public class ModelBuildingStatusMessage {
    public static final String CONTEXT_ID_FIELD_NAME = "contextId";

    private String sessionId;
    private String modelConfName;
    private long endTimeInSeconds;
    private String contextId;
    private boolean isSuccessful;
    private String details;

    /**
     * Default c'tor
     */
    public ModelBuildingStatusMessage() {
    }

    /**
     * C'tor
     *
     * @param sessionId        model building session id, used to determine build session (debug only)
     * @param modelConfName    {@link  fortscale.ml.model.ModelConf}
     * @param endTimeInSeconds model building execution end time
     * @param contextId        i.e. username
     * @param isSuccessful     false if model failed to build, true otherwise
     * @param details          {@link fortscale.ml.model.listener.ModelBuildingStatus}
     */
    public ModelBuildingStatusMessage(String sessionId, String modelConfName, long endTimeInSeconds, String contextId, boolean isSuccessful, String details) {
        this.sessionId = sessionId;
        this.modelConfName = modelConfName;
        this.endTimeInSeconds = endTimeInSeconds;
        this.contextId = contextId;
        this.isSuccessful = isSuccessful;
        this.details = details;
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

    public long getEndTimeInSeconds() {
        return endTimeInSeconds;
    }

    public void setEndTimeInSeconds(long endTimeInSeconds) {
        this.endTimeInSeconds = endTimeInSeconds;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    @JsonProperty("isSuccessful")
    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
