package fortscale.ml.model;

import fortscale.ml.model.builder.ContinuousHistogramModelBuilder;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.retriever.EntityHistogramRetriever;
import fortscale.ml.model.retriever.IDataRetriever;
import fortscale.ml.model.selector.ContextSelector;
import fortscale.ml.model.selector.FeatureBucketContextSelector;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

@Configurable(preConstruction = true)
public class ModelBuilderManager implements IModelBuildingRegistrar {
    private static final Logger logger = Logger.getLogger(ModelBuilderManager.class);

    @Autowired
    private ModelStore modelStore;

    private ModelConf modelConf;
    private ContextSelector contextSelector;
    private IDataRetriever dataRetriever;
    private IModelBuilder modelBuilder;
    private IModelBuildingScheduler scheduler;

    public ModelBuilderManager(ModelConf modelConf, IModelBuildingScheduler scheduler) {
        Assert.notNull(modelConf);
        Assert.notNull(scheduler);

        this.modelConf = modelConf;
        this.scheduler = scheduler;

        if (modelConf.getContextSelectorConf() != null) {
            contextSelector = new FeatureBucketContextSelector(modelConf.getContextSelectorConf());
        }
        dataRetriever = new EntityHistogramRetriever(modelConf.getDataRetrieverConf());
        modelBuilder = new ContinuousHistogramModelBuilder();

        scheduler.register(this, calcNextRunTimeInSeconds());
    }

    @Override
    public void process(IModelBuildingListener listener, long sessionId) {
        if (contextSelector != null) {
            for (String contextId : contextSelector.getContexts(0L, 0L)) {
                build(listener, contextId, sessionId);
            }
        } else {
            build(listener, null, sessionId);
        }

        scheduler.register(this, calcNextRunTimeInSeconds());
    }

    public void build(IModelBuildingListener listener, String contextId, long sessionId) {
        Object modelBuilderData = dataRetriever.retrieve(contextId);
        Model model = modelBuilder.build(modelBuilderData);

        boolean success = true;
        try {
            modelStore.save(modelConf, contextId, model, sessionId);
        } catch (Exception e) {
            logger.error(String.format("Failed to save model %s for context ID %s", modelConf.getName(), contextId), e);
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

    public void setContextSelector(ContextSelector contextSelector) {
        this.contextSelector = contextSelector;
    }
}
