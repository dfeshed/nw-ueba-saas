package fortscale.ml.model;

import fortscale.ml.model.builder.ContinuousHistogramModelBuilder;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.listener.ModelBuildingStatus;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.ContextHistogramRetriever;
import fortscale.ml.model.selector.ContextSelector;
import fortscale.ml.model.selector.FeatureBucketContextSelector;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Configurable(preConstruction = true)
public class ModelBuilderManager {
    private static final Logger logger = Logger.getLogger(ModelBuilderManager.class);

    @Autowired
    private ModelStore modelStore;

    private ModelConf modelConf;
    private ContextSelector contextSelector;
    private AbstractDataRetriever dataRetriever;
    private IModelBuilder modelBuilder;

    private long numOfSuccesses;
    private long numOfFailures;

    public ModelBuilderManager(ModelConf modelConf) {
        Assert.notNull(modelConf);
        this.modelConf = modelConf;

        if (modelConf.getContextSelectorConf() != null) {
            contextSelector = new FeatureBucketContextSelector(modelConf.getContextSelectorConf());
        }
        dataRetriever = new ContextHistogramRetriever(modelConf.getDataRetrieverConf());
        modelBuilder = new ContinuousHistogramModelBuilder();
    }

    public void process(IModelBuildingListener listener, String sessionId, Date previousEndTime, Date currentEndTime) {
        Assert.notNull(currentEndTime);
        numOfSuccesses = 0;
        numOfFailures = 0;

        if (contextSelector != null) {
            if (previousEndTime == null) {
                long timeRangeInSeconds = modelConf.getDataRetrieverConf().getTimeRangeInSeconds();
                long timeRangeInMillis = TimeUnit.SECONDS.toMillis(timeRangeInSeconds);
                previousEndTime = new Date(currentEndTime.getTime() - timeRangeInMillis);
            }

            for (String contextId : contextSelector.getContexts(previousEndTime, currentEndTime)) {
                build(listener, sessionId, contextId, currentEndTime);
            }
        } else {
            build(listener, sessionId, null, currentEndTime);
        }

        logger.info("modelConfName: {}, sessionId: {}, currentEndTime: {}, numOfSuccesses: {}, numOfFailures: {}.",
                modelConf.getName(), sessionId, currentEndTime.toString(), numOfSuccesses, numOfFailures);
    }

    public void setContextSelector(ContextSelector contextSelector) {
        this.contextSelector = contextSelector;
    }

    private void build(IModelBuildingListener listener, String sessionId, String contextId, Date endTime) {
        ModelBuildingStatus status = ModelBuildingStatus.SUCCESS;
        Exception exception = null;

        Object modelBuilderData = dataRetriever.retrieve(contextId, endTime);
        if (modelBuilderData == null) {
            status = ModelBuildingStatus.RETRIEVER_FAILURE;
        } else {
            Model model = modelBuilder.build(modelBuilderData);
            if (model == null) {
                status = ModelBuildingStatus.BUILDER_FAILURE;
            } else {
                try {
                    modelStore.save(modelConf, sessionId, contextId, model, endTime);
                } catch (Exception e) {
                    status = ModelBuildingStatus.STORE_FAILURE;
                    exception = e;
                }
            }
        }

        // Update metrics
        if (status.equals(ModelBuildingStatus.SUCCESS)) {
            numOfSuccesses++;
        } else {
            numOfFailures++;
            // Log if model building failed
            String message = String.format("%s. modelConfName: %s, sessionId: %s, contextId: %s, endTime: %s.",
                    status.getMessage(), modelConf.getName(), sessionId, contextId, endTime.toString());
            logError(message, exception);
        }

        // Inform listener
        if (listener != null) {
            listener.modelBuildingStatus(modelConf.getName(), sessionId, contextId, endTime, status);
        }
    }

    private static void logError(String errorMsg, Exception exception) {
        if (exception == null) {
            logger.error(errorMsg);
        } else {
            logger.error(errorMsg, exception);
        }
    }
}
