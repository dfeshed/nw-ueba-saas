package fortscale.ml.model;

import fortscale.ml.model.builder.ContinuousHistogramModelBuilder;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.ContextHistogramRetriever;
import fortscale.ml.model.selector.ContextSelector;
import fortscale.ml.model.selector.FeatureBucketContextSelector;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

@Configurable(preConstruction = true)
public class ModelBuilderManager {
    private static final Logger logger = Logger.getLogger(ModelBuilderManager.class);

    @Autowired
    private ModelStore modelStore;

    private ModelConf modelConf;
    private ContextSelector contextSelector;
    private AbstractDataRetriever dataRetriever;
    private IModelBuilder modelBuilder;

    public ModelBuilderManager(ModelConf modelConf) {
        Assert.notNull(modelConf);
        this.modelConf = modelConf;

        if (modelConf.getContextSelectorConf() != null) {
            contextSelector = new FeatureBucketContextSelector(modelConf.getContextSelectorConf());
        }
        dataRetriever = new ContextHistogramRetriever(modelConf.getDataRetrieverConf());
        modelBuilder = new ContinuousHistogramModelBuilder();
    }

    public void process(IModelBuildingListener listener, String sessionId, DateTime previousEndTime, DateTime currentEndTime) {
        Assert.notNull(currentEndTime);

        if (contextSelector != null) {
            if (previousEndTime == null) {
                long timeRangeInSeconds = modelConf.getDataRetrieverConf().getTimeRangeInSeconds();
                previousEndTime = currentEndTime.minus(new Duration(TimestampUtils.convertToMilliSeconds(timeRangeInSeconds)));
            }

            for (String contextId : contextSelector.getContexts(previousEndTime, currentEndTime)) {
                build(listener, sessionId, contextId, currentEndTime);
            }
        } else {
            build(listener, sessionId, null, currentEndTime);
        }
    }

    public void build(IModelBuildingListener listener, String sessionId, String contextId, DateTime endTime) {
        Object modelBuilderData = dataRetriever.retrieve(contextId, endTime);
        Model model = modelBuilder.build(modelBuilderData);

        boolean success = true;
        try {
            modelStore.save(modelConf, sessionId, contextId, model, endTime);
        } catch (Exception e) {
            logger.error(String.format("Failed to save model %s, with end time %s, for context ID %s.",
                    modelConf.getName(), endTime.toString(), contextId), e);
            success = false;
        }

        if (listener != null) {
            listener.modelBuildingStatus(modelConf.getName(), contextId, endTime, success);
        }
    }

    public void setContextSelector(ContextSelector contextSelector) {
        this.contextSelector = contextSelector;
    }
}
