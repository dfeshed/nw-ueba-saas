package fortscale.ml.model;

import fortscale.ml.model.selector.EntitiesSelector;
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

    public void process() {
        EntitiesSelector entitiesSelector = modelConf.getEntitiesSelector();
        String[] entities;
        if (entitiesSelector != null) {
            // we get here for entity model configurations
            entities = entitiesSelector.getEntities();
        }
        else {
            // we get here for global model configurations
            entities = new String[]{null};
        }
        for (String entityID : entities) {
            ModelBuilderData modelBuilderData = modelConf.getModelBuilderDataRetriever().retrieve(entityID);
            Model model = modelConf.getModelBuilder().build(modelBuilderData);
            modelConf.getModelStore().save(modelConf, entityID, model);
        }
    }
}
