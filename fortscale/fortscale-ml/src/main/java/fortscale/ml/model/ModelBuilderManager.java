package fortscale.ml.model;

import org.springframework.util.Assert;

import fortscale.ml.model.selector.ContextSelector;

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
        ContextSelector contextsSelector = modelConf.getContextSelectorConf();
        if (contextsSelector != null) {
	        for (String contextId : contextsSelector.getContexts()) {
	            ModelBuilderData modelBuilderData = modelConf.getModelBuilderDataRetriever().retrieve(contextId);
	            Model model = modelConf.getModelBuilder().build(modelBuilderData);
	            modelConf.getModelStore().save(modelConf, contextId, model);
	        }
	    }
    }
}
