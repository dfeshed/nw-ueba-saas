package fortscale.ml.model;

import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.selector.EntitiesSelector;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.springframework.util.Assert;

public class ModelBuilderManager implements IModelBuildingRegistrar {
    private static final Logger logger = Logger.getLogger(ModelBuilderManager.class);

    private ModelConf modelConf;
    private IModelBuildingScheduler scheduler;

    public ModelBuilderManager(ModelConf modelConf, IModelBuildingScheduler scheduler) {
        Assert.notNull(modelConf);
        Assert.notNull(scheduler);
        this.modelConf = modelConf;
        this.scheduler = scheduler;

        scheduler.register(this, calcNextRunTimeInSeconds());
    }

    @Override
    public void process(IModelBuildingListener listener) {
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
            Object modelBuilderData = modelConf.getDataRetriever().retrieve(entityID);
            Model model = modelConf.getModelBuilder().build(modelBuilderData);
            boolean success = true;
            try {
                modelConf.getModelStore().save(modelConf, entityID, model);
            } catch (Exception e) {
                logger.error(String.format("failed to save model for %s for context %s", modelConf.getName(), entityID), e);
                success = false;
            }

            if (listener != null) {
                // TODO: Change to contextId
                listener.modelBuildingStatus(modelConf.getName(), null, success);
            }
        }

        scheduler.register(this, calcNextRunTimeInSeconds());
    }

    private long calcNextRunTimeInSeconds() {
        long currentTimeSeconds = TimestampUtils.convertToSeconds(System.currentTimeMillis());
        return currentTimeSeconds + modelConf.getBuildIntervalInSeconds();
    }
}
