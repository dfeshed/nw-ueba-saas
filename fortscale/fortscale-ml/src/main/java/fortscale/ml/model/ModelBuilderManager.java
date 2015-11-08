package fortscale.ml.model;

import org.springframework.util.Assert;

public class ModelBuilderManager {
    private ModelConf modelConf;
    private long nextRunTimeInSeconds;

    public ModelBuilderManager(ModelConf modelConf) {
        Assert.notNull(modelConf);
        this.modelConf = modelConf;
        this.nextRunTimeInSeconds = -1;
    }

    public void calcNextRunTime(long currentTimeInSeconds) {
        nextRunTimeInSeconds = currentTimeInSeconds + modelConf.getBuildIntervalInSeconds();
    }

    public long getNextRunTimeInSeconds() {
        if (nextRunTimeInSeconds < 0) {
            throw new IllegalStateException("next run time hasn't been calculated yet");
        }
        return nextRunTimeInSeconds;
    }

    public void run() {
        for (String entityID : modelConf.getEntitiesSelector().getEntities()) {
            EntityData entityData = modelConf.getDataRetriever().retrieve(entityID);
            Model model = modelConf.getModelBuilder().build(entityData);
            modelConf.getModelStore().save(entityID, model);
        }
    }
}
