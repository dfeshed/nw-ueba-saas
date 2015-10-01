package fortscale.collection.jobs.cleanup;

import java.util.List;

/**
 * Created by Amir Keren on 30/09/15.
 */
public class CleanupStep {

    private List<MiniStep> timeBasedSteps;
    private List<MiniStep> otherSteps;

    public List<MiniStep> getTimeBasedSteps() {
        return timeBasedSteps;
    }

    public void setTimeBasedSteps(List<MiniStep> timeBasedSteps) {
        this.timeBasedSteps = timeBasedSteps;
    }

    public List<MiniStep> getOtherSteps() {
        return otherSteps;
    }

    public void setOtherSteps(List<MiniStep> otherSteps) {
        this.otherSteps = otherSteps;
    }

}