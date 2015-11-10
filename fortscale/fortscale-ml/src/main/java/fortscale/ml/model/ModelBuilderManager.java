package fortscale.ml.model;

import org.springframework.util.Assert;

import fortscale.ml.model.selector.ContextSelector;
import fortscale.ml.model.selector.FeatureBucketContextSelector;

public class ModelBuilderManager {
    private ModelConf modelConf;
    ContextSelector contextsSelector;
    private long nextRunTimeInSeconds;

    public ModelBuilderManager(ModelConf modelConf) {
        Assert.notNull(modelConf);
        this.modelConf = modelConf;
        contextsSelector = new FeatureBucketContextSelector(modelConf.getContextSelectorConf());
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
        if (contextsSelector != null) {
	        for (String contextId : contextsSelector.getContexts(nextRunTimeInSeconds - modelConf.getBuildIntervalInSeconds(), nextRunTimeInSeconds)) {
	        	Object modelBuilderData = modelConf.getModelBuilderDataRetriever().retrieve(contextId);
	            Model model = modelConf.getModelBuilder().build(modelBuilderData);
	            modelConf.getModelStore().save(modelConf, contextId, model);
	        }
	    }
    }
}
