package fortscale.ml.model.retriever;

import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.*;
import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordData;
import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordDataContainer;
import fortscale.ml.model.selector.AccumulatedSmartContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.ml.scorer.algorithms.SmartWeightsAlgorithm;
import fortscale.smart.record.conf.ClusterConf;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.time.TimeRange;
import org.springframework.util.Assert;
import presidio.ade.domain.record.accumulator.AccumulatedSmartRecord;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataReader;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

import static fortscale.ml.model.ModelBuilderData.NoDataReason.NO_DATA_IN_DATABASE;

/**
 * Created by barak_schuster on 24/08/2017.
 */
public class AccumulatedSmartValueRetriever extends AbstractDataRetriever {

    private final SmartRecordConf smartRecordConf;
    private final String smartRecordConfName;
    private final SmartAccumulationDataReader accumulationDataReader;
    private final SmartRecordConfService smartRecordConfService;
    private final FactoryService<IContextSelector> contextSelectorFactoryService;
    private final ModelConf weightsModelConf;
    private ModelConfService modelConfService;
    private final ModelStore modelStore;
    private final SmartWeightsAlgorithm smartWeightsAlgorithm;
    private final Duration oldestAllowedModelDurationDiff;

    public AccumulatedSmartValueRetriever(AccumulatedSmartValueRetrieverConf dataRetrieverConf, SmartAccumulationDataReader accumulationDataReader, SmartRecordConfService smartRecordConfService, FactoryService<IContextSelector> contextSelectorFactoryService, ModelStore modelStore, Duration oldestAllowedModelDurationDiff) {
        super(dataRetrieverConf);
        this.smartRecordConfName = dataRetrieverConf.getSmartRecordConfName();
        this.modelStore = modelStore;
        this.oldestAllowedModelDurationDiff = oldestAllowedModelDurationDiff;
        Assert.hasText(this.smartRecordConfName,"smart record conf name must be defined for retriever");
        this.accumulationDataReader = accumulationDataReader;
        this.smartRecordConfService = smartRecordConfService;
        this.contextSelectorFactoryService = contextSelectorFactoryService;
        this.smartRecordConf = this.smartRecordConfService.getSmartRecordConf(this.smartRecordConfName);
        String weightsModelName = dataRetrieverConf.getWeightsModelName();
        Assert.hasText(weightsModelName ,String.format("weightsModelName must be defined for retriever name=%s",this.smartRecordConfName));
        this.weightsModelConf = this.modelConfService.getModelConf(weightsModelName);
        Assert.notNull(this.weightsModelConf ,String.format("modelConf must be defined for retriever name=%s",this.smartRecordConfName));
        this.smartWeightsAlgorithm = new SmartWeightsAlgorithm();
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime) {
        if(modelConfService == null) {
            modelConfService = DynamicModelConfServiceContainer.getModelConfService();
        }
        Instant startTime = getStartTime(endTime).toInstant();
        Instant endTimeInstant = endTime.toInstant();
        TimeRange timeRange = new TimeRange(startTime,endTimeInstant);

        // If the retrieve is called for building a global model
        if (contextId == null) {
            return retrieveGlobalModelBuilderData(timeRange);
        }
        Stream<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainerStream = readSmartAggregatedRecordData(
                contextId, startTime, endTimeInstant);
        GenericHistogram reductionHistogram = new GenericHistogram();
        final boolean[] noDataInDatabase = {true};

        smartAggregatedRecordDataContainerStream.forEach(recordsDataContainer -> {
            noDataInDatabase[0] = false;
            double entityEventValue = calculateSmartValue(endTimeInstant, recordsDataContainer);
            // TODO: Retriever functions should be iterated and executed here.
            reductionHistogram.add(entityEventValue, 1d);
        });

        if (reductionHistogram.getN() == 0) {
            if (noDataInDatabase[0]) {
                return new ModelBuilderData(NO_DATA_IN_DATABASE);
            } else {
                return new ModelBuilderData(ModelBuilderData.NoDataReason.ALL_DATA_FILTERED);
            }
        } else {
            return new ModelBuilderData(reductionHistogram);
        }
    }

    private double calculateSmartValue(Instant endTimeInstant, SmartAggregatedRecordDataContainer recordsDataContainer) {
        SmartWeightsModel smartWeightsModel = getModel(endTimeInstant);
        List<ClusterConf> clusterConfs = smartWeightsModel.getClusterConfs();
        List<SmartAggregatedRecordData> aggregatedRecordsData = recordsDataContainer.getSmartAggregatedRecordsData();
        return smartWeightsAlgorithm.calculateScore(aggregatedRecordsData,clusterConfs);
    }

    private SmartWeightsModel getModel(Instant endTimeInstant) {
        Instant oldestAllowedModelTime = endTimeInstant.minus(oldestAllowedModelDurationDiff);
        ModelDAO latestBeforeEventTimeAfterOldestAllowedModelDao = modelStore.getLatestBeforeEventTimeAfterOldestAllowedModelDao(weightsModelConf, null, endTimeInstant, oldestAllowedModelTime);
        return (SmartWeightsModel) latestBeforeEventTimeAfterOldestAllowedModelDao.getModel();
    }

    private Stream<SmartAggregatedRecordDataContainer> readSmartAggregatedRecordData(String contextId, Instant startTime, Instant endTime) {
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainerList = new ArrayList<>();

        List<AccumulatedSmartRecord> accumulatedSmartRecords = accumulationDataReader.findAccumulatedEventsByContextIdAndStartTimeRange(smartRecordConfName, contextId, startTime, endTime);

        for (AccumulatedSmartRecord accumulatedSmartRecord: accumulatedSmartRecords)
        {
            for(Integer activityTime: accumulatedSmartRecord.getActivityTime())
            {
                Map<String,Double> featureNameToScore = new HashMap();
                for (Map.Entry<String, Map<Integer, Double>> aggrFeature : accumulatedSmartRecord.getAggregatedFeatureEventsValuesMap().entrySet()) {
                    Double activityTimeScore = aggrFeature.getValue().get(activityTime);
                    String featureName = aggrFeature.getKey();
                    if (activityTimeScore == null) {
                        logger.debug("score does not exists for aggrFeature={} at activityTime={} setting to 0", featureName,activityTime);
                    }
                    else {
                        logger.debug("score={} for aggrFeature={} at activityTime={}", activityTimeScore, featureName, activityTime);
                        featureNameToScore.put(featureName, activityTimeScore);
                    }
                }
                smartAggregatedRecordDataContainerList.add(new SmartAggregatedRecordDataContainer(startTime,featureNameToScore));
            }
        }
        return smartAggregatedRecordDataContainerList.stream();
    }

    private ModelBuilderData retrieveGlobalModelBuilderData(TimeRange timeRange) {
        AccumulatedSmartContextSelectorConf conf = new AccumulatedSmartContextSelectorConf(smartRecordConfName);
        IContextSelector contextSelector = contextSelectorFactoryService.getProduct(conf);
        Set<String> contextIds = contextSelector.getContexts(timeRange);
        logger.info("Number of contextIds: " + contextIds.size());
        GenericHistogram reductionHistogram = new GenericHistogram();

        if (contextIds.isEmpty()) {
            return new ModelBuilderData(NO_DATA_IN_DATABASE);
        }

        for (String contextId : contextIds) {
            readSmartAggregatedRecordData(
                    contextId, timeRange.getStart(), timeRange.getEnd()).
                    mapToDouble(jokerEntityEventData -> calculateSmartValue(timeRange.getEnd(), jokerEntityEventData))
                    .max()
                    .ifPresent(maxEntityEventValue -> {
                        // TODO: Retriever functions should be iterated and executed here.
                        reductionHistogram.add(maxEntityEventValue, 1d);
                    });
        }

        if (reductionHistogram.getN() == 0) {
            return new ModelBuilderData(ModelBuilderData.NoDataReason.ALL_DATA_FILTERED);
        } else {
            return new ModelBuilderData(reductionHistogram);
        }
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime, Feature feature) {
        throw new UnsupportedOperationException(String.format(
                "%s does not support retrieval of a single feature",
                getClass().getSimpleName()));
    }

    @Override
    public Set<String> getEventFeatureNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getContextFieldNames() {
        return smartRecordConf.getContexts();
    }

    @Override
    public String getContextId(Map<String, String> context) {
        return AccumulatedSmartRecord.getAggregatedFeatureContextId(context);
    }
}
