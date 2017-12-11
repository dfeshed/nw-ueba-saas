package fortscale.ml.model.retriever;

import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.*;
import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordData;
import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordDataContainer;
import fortscale.ml.model.retriever.smart_data.SmartValueData;
import fortscale.ml.model.selector.AccumulatedSmartContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithm;
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
import java.util.stream.Collectors;

import static fortscale.ml.model.ModelBuilderData.NoDataReason.NO_DATA_IN_DATABASE;
import static fortscale.ml.model.retriever.smart_data.SmartAccumulationFlattener.flattenSmartRecordToSmartAggrData;

/**
 * @author Barak Schuster
 * @author Lior Govrin
 */
public class AccumulatedSmartValueRetriever extends AbstractDataRetriever {

    private final SmartRecordConf smartRecordConf;
    private final String smartRecordConfName;
    private final SmartAccumulationDataReader accumulationDataReader;
    private final FactoryService<IContextSelector> contextSelectorFactoryService;
    private final long partitionsResolutionInSeconds;
    private ModelConf weightsModelConf;
    private final String weightsModelName;
    private ModelConfService modelConfService;
    private final ModelStore modelStore;
    private final SmartWeightsScorerAlgorithm smartWeightsScorerAlgorithm;
    private final Duration oldestAllowedModelDurationDiff;

    public AccumulatedSmartValueRetriever(AccumulatedSmartValueRetrieverConf dataRetrieverConf, SmartAccumulationDataReader accumulationDataReader, SmartRecordConfService smartRecordConfService, FactoryService<IContextSelector> contextSelectorFactoryService, ModelStore modelStore, Duration oldestAllowedModelDurationDiff, SmartWeightsScorerAlgorithm smartWeightsScorerAlgorithm) {
        super(dataRetrieverConf);
        this.smartRecordConfName = dataRetrieverConf.getSmartRecordConfName();
        this.modelStore = modelStore;
        this.oldestAllowedModelDurationDiff = oldestAllowedModelDurationDiff;
        Assert.hasText(this.smartRecordConfName,"smart record conf name must be defined for retriever");
        this.accumulationDataReader = accumulationDataReader;
        this.contextSelectorFactoryService = contextSelectorFactoryService;
        this.smartRecordConf = smartRecordConfService.getSmartRecordConf(this.smartRecordConfName);
        this.partitionsResolutionInSeconds = dataRetrieverConf.getPartitionsResolutionInSeconds();
        long smartRecordConfDurationStrategyInSeconds = smartRecordConf.getFixedDurationStrategy().toDuration().getSeconds();
        String message = String.format("partitionsResolutionInSeconds=%d must be multiplication of smartRecordConfDurationStrategyInSeconds=%d. fix retrieverConf=%s or smartConf=%s",
                partitionsResolutionInSeconds, smartRecordConfDurationStrategyInSeconds, dataRetrieverConf.getFactoryName(), smartRecordConf.getName());
        Assert.isTrue(partitionsResolutionInSeconds % smartRecordConfDurationStrategyInSeconds == 0, message);
        this.weightsModelName = dataRetrieverConf.getWeightsModelName();
        Assert.hasText(weightsModelName, String.format("weightsModelName must be defined for retriever name=%s", this.smartRecordConfName));

        this.smartWeightsScorerAlgorithm = smartWeightsScorerAlgorithm;
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime) {
        if(modelConfService == null) {
            modelConfService = DynamicModelConfServiceContainer.getModelConfService();
            this.weightsModelConf = this.modelConfService.getModelConf(weightsModelName);
            Assert.notNull(this.weightsModelConf, String.format("modelConf must be defined for retriever name=%s", this.smartRecordConfName));
        }
        Instant startTime = getStartTime(endTime).toInstant();
        Instant endTimeInstant = endTime.toInstant();
        TimeRange timeRange = new TimeRange(startTime,endTimeInstant);
        ModelDAO weightsModelDAO = getModelDAO(endTimeInstant);
        Instant weightsModelEndTime = weightsModelDAO.getEndTime();
        SmartWeightsModel smartWeightsModel = (SmartWeightsModel) weightsModelDAO.getModel();

        // If the retrieve is called for building a global model
        if (contextId == null) {
            return retrieveGlobalModelBuilderData(timeRange,smartWeightsModel,weightsModelEndTime);
        }
        List<AccumulatedSmartRecord> accumulatedSmartRecords = accumulationDataReader.findAccumulatedEventsByContextIdAndStartTimeRange(smartRecordConfName, contextId, startTime, endTimeInstant);
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainerList = flattenSmartRecordToSmartAggrData(accumulatedSmartRecords);
        GenericHistogram reductionHistogram = new GenericHistogram();
        final boolean[] noDataInDatabase = {true};

        smartAggregatedRecordDataContainerList.forEach(recordsDataContainer -> {
            noDataInDatabase[0] = false;
            double smartValue = calculateSmartValue(recordsDataContainer,smartWeightsModel);
            // TODO: Retriever functions should be iterated and executed here.
            reductionHistogram.add(smartValue, 1d);
        });
        if (reductionHistogram.getN() == 0) {
            if (noDataInDatabase[0]) {
                return new ModelBuilderData(NO_DATA_IN_DATABASE);
            } else {
                return new ModelBuilderData(ModelBuilderData.NoDataReason.ALL_DATA_FILTERED);
            }
        } else {
            long numOfPartitions = calcNumOfPartitions(accumulatedSmartRecords).size();
            reductionHistogram.setNumberOfPartitions(numOfPartitions);
            SmartValueData smartValueData = new SmartValueData(reductionHistogram,weightsModelEndTime);
            return new ModelBuilderData(smartValueData);
        }
    }

    private double calculateSmartValue(SmartAggregatedRecordDataContainer recordsDataContainer, SmartWeightsModel smartWeightsModel) {
        List<ClusterConf> clusterConfs = smartWeightsModel.getClusterConfs();
        List<SmartAggregatedRecordData> aggregatedRecordsData = recordsDataContainer.getSmartAggregatedRecordsData();
        return smartWeightsScorerAlgorithm.calculateScore(aggregatedRecordsData, clusterConfs).getScore();
    }

    private ModelDAO getModelDAO(Instant endTimeInstant) {
        Instant oldestAllowedModelTime = endTimeInstant.minus(oldestAllowedModelDurationDiff);
        return modelStore.getLatestBeforeEventTimeAfterOldestAllowedModelDaoSortedByEndTimeDesc(weightsModelConf, null, endTimeInstant, oldestAllowedModelTime, 1).get(0);
    }



    private ModelBuilderData retrieveGlobalModelBuilderData(TimeRange timeRange, SmartWeightsModel smartWeightsModel, Instant weightsModelEndTime) {
        AccumulatedSmartContextSelectorConf conf = new AccumulatedSmartContextSelectorConf(smartRecordConfName);
        IContextSelector contextSelector = contextSelectorFactoryService.getProduct(conf);
        Set<String> contextIds = contextSelector.getContexts(timeRange);
        logger.info("Number of contextIds: " + contextIds.size());
        GenericHistogram reductionHistogram = new GenericHistogram();
        Instant startTime = timeRange.getStart();
        Instant endTime = timeRange.getEnd();
        if (contextIds.isEmpty()) {
            return new ModelBuilderData(NO_DATA_IN_DATABASE);
        }
        Set<Long> distinctPartitionIds = new HashSet<>();
        for (String contextId : contextIds) {
            List<AccumulatedSmartRecord> accumulatedSmartRecords = accumulationDataReader.findAccumulatedEventsByContextIdAndStartTimeRange(smartRecordConfName, contextId, startTime, endTime);
            List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers = flattenSmartRecordToSmartAggrData(accumulatedSmartRecords);

            distinctPartitionIds.addAll(calcNumOfPartitions(accumulatedSmartRecords));
            smartAggregatedRecordDataContainers.stream()
                    .mapToDouble(smartData -> calculateSmartValue(smartData, smartWeightsModel))
                    .max()
                    .ifPresent(maxSmartValue -> {
                        // TODO: Retriever functions should be iterated and executed here.
                        reductionHistogram.add(maxSmartValue, 1d);
                    });
        }

        if (reductionHistogram.getN() == 0) {
            return new ModelBuilderData(ModelBuilderData.NoDataReason.ALL_DATA_FILTERED);
        } else {
            reductionHistogram.setNumberOfPartitions(distinctPartitionIds.size());
            SmartValueData smartValueData = new SmartValueData(reductionHistogram,weightsModelEndTime);
            return new ModelBuilderData(smartValueData);
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

    Set<Long> calcNumOfPartitions(List<AccumulatedSmartRecord> data) {
        return data.stream()
                .map(x -> (x.getStartInstant().getEpochSecond() / partitionsResolutionInSeconds) * partitionsResolutionInSeconds)
                .distinct()
                .collect(Collectors.toSet());
    }
}
