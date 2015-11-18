package fortscale.ml.model;


import org.joda.time.DateTime;
import org.springframework.util.Assert;

import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.selector.ContextSelector;
import fortscale.ml.model.selector.FeatureBucketContextSelector;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;

public class ModelBuilderManager implements IModelBuildingRegistrar {
    private static final Logger logger = Logger.getLogger(ModelBuilderManager.class);

    private ModelConf modelConf;
    private ContextSelector contextsSelector;
    private IModelBuildingScheduler scheduler;

    public ModelBuilderManager(ModelConf modelConf, IModelBuildingScheduler scheduler) {
        Assert.notNull(modelConf);
        Assert.notNull(scheduler);
        this.modelConf = modelConf;
        this.scheduler = scheduler;
        if(modelConf.getContextSelectorConf() != null){
        	contextsSelector = new FeatureBucketContextSelector(modelConf.getContextSelectorConf());
        }

        scheduler.register(this, calcNextRunTimeInSeconds());
    }

    public void process(IModelBuildingListener listener, DateTime sessionStartTime, DateTime sessionEndTime) {
        if (contextsSelector != null) {
	        for (String contextId : contextsSelector.getContexts(0L, 0L)) {
	        	 build(listener, contextId, sessionStartTime, sessionEndTime);
	        }
	    } else{
	    	build(listener, null, sessionStartTime, sessionEndTime);
	    }

        scheduler.register(this, calcNextRunTimeInSeconds());
    }
    
    public void build(IModelBuildingListener listener, String contextId, DateTime sessionStartTime, DateTime sessionEndTime){
    	Object modelBuilderData = modelConf.getDataRetriever().retrieve(contextId);
        Model model = modelConf.getModelBuilder().build(modelBuilderData);
        boolean success = true;
        try {
            modelConf.getModelStore().save(modelConf, contextId, model, sessionStartTime, sessionEndTime);
        } catch (Exception e) {
            logger.error(String.format("failed to save model for %s for context %s", modelConf.getName(), contextId), e);
            success = false;
        }

        if (listener != null) {
            listener.modelBuildingStatus(modelConf.getName(), contextId, success);
        }
    }

    private long calcNextRunTimeInSeconds() {
        long currentTimeSeconds = TimestampUtils.convertToSeconds(System.currentTimeMillis());
        return currentTimeSeconds + modelConf.getBuildIntervalInSeconds();
    }

	public void setContextsSelector(ContextSelector contextsSelector) {
		this.contextsSelector = contextsSelector;
	}
    
    
}
