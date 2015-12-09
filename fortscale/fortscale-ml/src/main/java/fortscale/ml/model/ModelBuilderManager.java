package fortscale.ml.model;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.listener.ModelBuildingStatus;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.selector.ContextSelector;
import fortscale.ml.model.selector.ContextSelectorConf;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    public ModelBuilderManager(ModelConf modelConf, ModelService modelService) {
        Assert.notNull(modelConf);
        Assert.notNull(modelService);
        this.modelConf = modelConf;

        ContextSelectorConf contextSelectorConf = modelConf.getContextSelectorConf();
        contextSelector = contextSelectorConf == null ? null : modelService.getContextSelector(contextSelectorConf);
        dataRetriever = modelService.getDataRetriever(modelConf.getDataRetrieverConf());
        modelBuilder = modelService.getModelBuilder(modelConf.getModelBuilderConf());
    }

    public void process(IModelBuildingListener listener, String sessionId, Date previousEndTime, Date currentEndTime) {
        Assert.notNull(currentEndTime);
        List<String> contextIds;

        if (contextSelector != null) {
            if (previousEndTime == null) {
                long timeRangeInSeconds = modelConf.getDataRetrieverConf().getTimeRangeInSeconds();
                long timeRangeInMillis = TimeUnit.SECONDS.toMillis(timeRangeInSeconds);
                previousEndTime = new Date(currentEndTime.getTime() - timeRangeInMillis);
            }

            contextIds = contextSelector.getContexts(previousEndTime, currentEndTime);
        } else {
            contextIds = new ArrayList<>();
            contextIds.add(null);
        }

        long numOfSuccesses = 0;
        long numOfFailures = 0;

        for (String contextId : contextIds) {
            if (build(listener, sessionId, contextId, currentEndTime)) {
                numOfSuccesses++;
            } else {
                numOfFailures++;
            }
        }

        logger.info("modelConfName: {}, sessionId: {}, currentEndTime: {}, numOfSuccesses: {}, numOfFailures: {}.",
                modelConf.getName(), sessionId, currentEndTime.toString(), numOfSuccesses, numOfFailures);
    }

    private boolean build(IModelBuildingListener listener, String sessionId, String contextId, Date endTime) {
        ModelBuildingStatus status = ModelBuildingStatus.SUCCESS;

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
                }
            }
        }

        if (listener != null) {
            listener.modelBuildingStatus(modelConf.getName(), sessionId, contextId, endTime, status);
        }

        return status.equals(ModelBuildingStatus.SUCCESS);
    }
}
