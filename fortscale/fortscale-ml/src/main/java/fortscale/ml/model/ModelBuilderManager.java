package fortscale.ml.model;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.listener.ModelBuildingStatus;
import fortscale.ml.model.metrics.ModelBuilderManagerMetrics;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.selector.IContextSelectorConf;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configurable(preConstruction = true)
public class ModelBuilderManager {
    private static final Logger logger = Logger.getLogger(ModelBuilderManager.class);

    @Autowired
    private FactoryService<IContextSelector> contextSelectorFactoryService;
    @Autowired
    private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;
    @Autowired
    private FactoryService<IModelBuilder> modelBuilderFactoryService;
    @Autowired
    private ModelStore modelStore;
    @Autowired
    private StatsService statsService;

    @Value("${fortscale.model.build.selector.delta.in.seconds}")
    private long selectorDeltaInSeconds;

    private ModelConf modelConf;
    private IContextSelector contextSelector;
    private AbstractDataRetriever dataRetriever;
    private IModelBuilder modelBuilder;
    private ModelBuilderManagerMetrics metrics;

    public ModelBuilderManager(ModelConf modelConf) {
        Assert.notNull(modelConf);
        this.modelConf = modelConf;

        IContextSelectorConf contextSelectorConf = modelConf.getContextSelectorConf();
        contextSelector = contextSelectorConf == null ? null : contextSelectorFactoryService.getProduct(contextSelectorConf);
        dataRetriever = dataRetrieverFactoryService.getProduct(modelConf.getDataRetrieverConf());
        modelBuilder = modelBuilderFactoryService.getProduct(modelConf.getModelBuilderConf());
        metrics = new ModelBuilderManagerMetrics(statsService, modelConf.getName());
    }

    public void process(IModelBuildingListener listener, String sessionId, Date previousEndTime, Date currentEndTime) {
        Assert.notNull(currentEndTime);
        List<String> contextIds;

        metrics.process++;
        metrics.currentEndTime = TimeUnit.MILLISECONDS.toSeconds(currentEndTime.getTime());
        logger.info("<<< Starting building models for {}, sessionId {}, previousEndTime {}, currentEndTime {}",
                modelConf.getName(), sessionId, previousEndTime, currentEndTime);

        if (contextSelector != null) {
            if (previousEndTime == null) {
                metrics.processWithNoPreviousEndTime++;
                long timeRangeInSeconds = modelConf.getDataRetrieverConf().getTimeRangeInSeconds();
                long timeRangeInMillis = TimeUnit.SECONDS.toMillis(timeRangeInSeconds);
                previousEndTime = new Date(currentEndTime.getTime() - timeRangeInMillis);
            } else {
                metrics.processWithPreviousEndTime++;
                previousEndTime = new Date(currentEndTime.getTime() - TimeUnit.SECONDS.toMillis(selectorDeltaInSeconds));
            }

            contextIds = contextSelector.getContexts(previousEndTime, currentEndTime);
        } else {
            metrics.processWithNoContextSelector++;
            contextIds = new ArrayList<>();
            contextIds.add(null);
        }

        metrics.contextIds += contextIds.size();
        logger.info("Selected {} context IDs", contextIds.size());

        long numOfSuccesses = 0;
        long numOfFailures = 0;

        for (String contextId : contextIds) {
            // Run retriever, builder and store steps
            ModelBuildingStatus status = process(sessionId, contextId, currentEndTime);

            // If it exists, inform listener about the results
            if (listener != null) {
                listener.modelBuildingStatus(modelConf.getName(), sessionId, contextId, currentEndTime, status);
            }

            // Update metrics
            if (status.equals(ModelBuildingStatus.SUCCESS)) {
                metrics.successes++;
                numOfSuccesses++;
            } else {
                metrics.failures++;
                numOfFailures++;
            }
        }

        if (listener != null) {
            listener.modelBuildingSummary(modelConf.getName(), sessionId, currentEndTime, numOfSuccesses, numOfFailures);
        }

        logger.info(">>> Finished building models for {}, sessionId {}, numOfSuccesses {}, numOfFailures {}",
                modelConf.getName(), sessionId, numOfSuccesses, numOfFailures);
    }

    private ModelBuildingStatus process(String sessionId, String contextId, Date endTime) {
        Object modelBuilderData;
        Model model;

        // Retriever
        try {
            modelBuilderData = dataRetriever.retrieve(contextId, endTime);
        } catch (Exception e) {
            metrics.retrieverFailures++;
            logger.error("Failed to retrieve data: " + e.toString() + ": " + ExceptionUtils.getStackTrace(e));
            modelBuilderData = null;
        }
        if (modelBuilderData == null) {
            return ModelBuildingStatus.RETRIEVER_FAILURE;
        }

        // Builder
        try {
            model = modelBuilder.build(modelBuilderData);
        } catch (Exception e) {
            metrics.builderFailures++;
            logger.error("Failed to build model: " + e.toString() + ": " + ExceptionUtils.getStackTrace(e));
            model = null;
        }
        if (model == null) {
            return ModelBuildingStatus.BUILDER_FAILURE;
        }

        long timeRangeInMillis = TimeUnit.SECONDS.toMillis(modelConf.getDataRetrieverConf().getTimeRangeInSeconds());
        Date startTime = new Date(endTime.getTime() - timeRangeInMillis);

        // Store
        try {
            modelStore.save(modelConf, sessionId, contextId, model, startTime, endTime);
        } catch (Exception e) {
            metrics.storeFailures++;
            logger.error("Failed to store model: " + e.toString() + ": " + ExceptionUtils.getStackTrace(e));
            return ModelBuildingStatus.STORE_FAILURE;
        }

        return ModelBuildingStatus.SUCCESS;
    }
}
