package fortscale.collection.jobs.cleanup;

import java.util.Map;

/**
 * Created by Amir Keren on 30/09/15.
 */
public class CleanupSteps {

    private Map<String, CleanupStep> cleanupSteps;

    public Map<String, CleanupStep> getCleanupSteps() {
        return cleanupSteps;
    }

    public void setCleanupSteps(Map<String, CleanupStep> cleanupSteps) {
        this.cleanupSteps = cleanupSteps;
    }

    public CleanupStep getCleanupStep(String stepId) {
        return cleanupSteps.get(stepId);
    }

}