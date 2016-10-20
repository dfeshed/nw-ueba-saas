package fortscale.ml.model.message;

/**
 * summarise model building status for model configuration name in a session
 * i.e
 * {
 * "modelConfName": "distinct_number_of_countries.normalized_username.vpn.daily",
 * "endTimeInSeconds": 1476748800,
 * "numOfFailures": 0,
 * "sessionId": "modelBuildNumber_1",
 * "numOfSuccesses": 106844
 * }
 * Created by barak_schuster on 10/19/16.
 */
public class ModelBuildingSummaryMessage {
    private String sessionId;
    private String modelConfName;
    private long endTimeInSeconds;
    private long numOfSuccesses;
    private long numOfFailures;

    /**
     * Default c'tor
     */
    public ModelBuildingSummaryMessage() {
    }

    /**
     * @param sessionId        model building session id, used to determine build session (debug only)
     * @param modelConfName    {@link  fortscale.ml.model.ModelConf}
     * @param endTimeInSeconds end time of execution for all contexts for that specific modelConfName
     * @param numOfSuccesses   number of successful model building
     * @param numOfFailures    number of model building failures
     */
    public ModelBuildingSummaryMessage(String sessionId, String modelConfName, long endTimeInSeconds, long numOfSuccesses, long numOfFailures) {
        this.sessionId = sessionId;
        this.modelConfName = modelConfName;
        this.endTimeInSeconds = endTimeInSeconds;
        this.numOfSuccesses = numOfSuccesses;
        this.numOfFailures = numOfFailures;
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

    public long getNumOfSuccesses() {
        return numOfSuccesses;
    }

    public void setNumOfSuccesses(long numOfSuccesses) {
        this.numOfSuccesses = numOfSuccesses;
    }

    public long getNumOfFailures() {
        return numOfFailures;
    }

    public void setNumOfFailures(long numOfFailures) {
        this.numOfFailures = numOfFailures;
    }
}
