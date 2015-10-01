package fortscale.collection.jobs.cleanup;

import java.util.List;

/**
 * Created by Amir Keren on 30/09/15.
 */
public class CleanupStep {

    private List<MiniStep> miniSteps;

    public List<MiniStep> getMiniSteps() {
        return miniSteps;
    }

    public void setMiniSteps(List<MiniStep> miniSteps) {
        this.miniSteps = miniSteps;
    }

}