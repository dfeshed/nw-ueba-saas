package fortscale.ml.model;

import org.springframework.util.Assert;

public class ModelBuilderManager {
    private ModelConf modelConf;
    private long nextRunTimeInSeconds;

    public ModelBuilderManager(ModelConf modelConf) {
        Assert.notNull(modelConf);
        this.modelConf = modelConf;
        nextRunTimeInSeconds = -1;
    }

    public void calcNextRunTime(long currentTimeInSeconds) {
        nextRunTimeInSeconds = currentTimeInSeconds + modelConf.getBuildIntervalInSeconds();
    }

    public long getNextRunTimeInSeconds() {
        if (nextRunTimeInSeconds < 0) {
            throw new IllegalStateException("calcNextRunTime must be called before calling to getNextRunTimeInSeconds");
        }
        return nextRunTimeInSeconds;
    }

    public void run() {
    }
}
