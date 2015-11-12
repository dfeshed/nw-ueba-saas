package fortscale.ml.model;


import org.springframework.util.Assert;

import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.selector.ContextSelector;
import fortscale.ml.model.selector.FeatureBucketContextSelector;
import fortscale.utils.time.TimestampUtils;

public class ModelBuilderManager implements IModelBuildingRegistrar {
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

    public void process(IModelBuildingListener listener) {
        if (contextsSelector != null) {
	        for (String contextId : contextsSelector.getContexts(0L, 0L)) {
	        	 build(listener, contextId);
	        }
	    } else{
	    	build(listener, null);
	    }
        scheduler.register(this, calcNextRunTimeInSeconds());
    }
    
    public void build(IModelBuildingListener listener, String contextId){
    	Object modelBuilderData = modelConf.getDataRetriever().retrieve(contextId);
        Model model = modelConf.getModelBuilder().build(modelBuilderData);
        boolean success = modelConf.getModelStore().save(modelConf, contextId, model);

        if (listener != null) {
            // TODO: Change to contextId
            listener.modelBuildingStatus(modelConf.getName(), null, success);
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
