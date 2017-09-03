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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static fortscale.ml.model.ModelBuilderData.NoDataReason.NO_DATA_IN_DATABASE;
import static fortscale.ml.model.retriever.smart_data.SmartAccumulationFlattener.flattenSmartRecordToSmartAggrData;

/**
 * Created by barak_schuster on 24/08/2017.
 */
public class AccumulatedSmartValueRetriever extends AbstractDataRetriever {

    private final SmartRecordConf smartRecordConf;
    private final String smartRecordConfName;
    private final SmartAccumulationDataReader accumulationDataReader;
    private final FactoryService<IContextSelector> contextSelectorFactoryService;
    private ModelConf weightsModelConf;
    private final String weightsModelName;
    private ModelConfService modelConfService;
    private final ModelStore modelStore;
    private final SmartWeightsScorerAlgorithm smartWeightsScorerAlgorithm;
    private final Duration oldestAllowedModelDurationDiff;

    public AccumulatedSmartValueRetriever(AccumulatedSmartValueRetrieverConf dataRetrieverConf, SmartAccumulationDataReader accumulationDataReader, SmartRecordConfService smartRecordConfService, FactoryService<IContextSelector> contextSelectorFactoryService, ModelStore modelStore, Duration oldestAllowedModelDurationDiff) {
        super(dataRetrieverConf);
        this.smartRecordConfName = dataRetrieverConf.getSmartRecordConfName();
        this.modelStore = modelStore;
        this.oldestAllowedModelDurationDiff = oldestAllowedModelDurationDiff;
        Assert.hasText(this.smartRecordConfName,"smart record conf name must be defined for retriever");
        this.accumulationDataReader = accumulationDataReader;
        this.contextSelectorFactoryService = contextSelectorFactoryService;
        this.smartRecordConf = smartRecordConfService.getSmartRecordConf(this.smartRecordConfName);
        this.weightsModelName = dataRetrieverConf.getWeightsModelName();
        Assert.hasText(weightsModelName ,String.format("weightsModelName must be defined for retriever name=%s",this.smartRecordConfName));

        this.smartWeightsScorerAlgorithm = new SmartWeightsScorerAlgorithm();
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime) {
        if(modelConfService == null) {
            modelConfService = DynamicModelConfServiceContainer.getModelConfService();
            this.weightsModelConf = this.modelConfService.getModelConf(weightsModelName);
            Assert.notNull(this.weightsModelConf ,String.format("modelConf must be defined for retriever name=%s",this.smartRecordConfName));
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
            double smartValue = calculateSmartValue(endTimeInstant, recordsDataContainer);
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
            return new ModelBuilderData(reductionHistogram);
        }
    }

    private double calculateSmartValue(Instant endTimeInstant, SmartAggregatedRecordDataContainer recordsDataContainer) {
        SmartWeightsModel smartWeightsModel = getModel(endTimeInstant);
        List<ClusterConf> clusterConfs = smartWeightsModel.getClusterConfs();
        List<SmartAggregatedRecordData> aggregatedRecordsData = recordsDataContainer.getSmartAggregatedRecordsData();
        return smartWeightsScorerAlgorithm.calculateScore(aggregatedRecordsData,clusterConfs);
    }

    private SmartWeightsModel getModel(Instant endTimeInstant) {
        Instant oldestAllowedModelTime = endTimeInstant.minus(oldestAllowedModelDurationDiff);
        ModelDAO latestBeforeEventTimeAfterOldestAllowedModelDao = modelStore.getLatestBeforeEventTimeAfterOldestAllowedModelDao(weightsModelConf, null, endTimeInstant, oldestAllowedModelTime);
        return (SmartWeightsModel) latestBeforeEventTimeAfterOldestAllowedModelDao.getModel();
    }

    private Stream<SmartAggregatedRecordDataContainer> readSmartAggregatedRecordData(String contextId, Instant startTime, Instant endTime) {
        List<AccumulatedSmartRecord> accumulatedSmartRecords = accumulationDataReader.findAccumulatedEventsByContextIdAndStartTimeRange(smartRecordConfName, contextId, startTime, endTime);
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainerList = flattenSmartRecordToSmartAggrData(startTime, accumulatedSmartRecords);
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
                    mapToDouble(smartData -> calculateSmartValue(timeRange.getEnd(), smartData))
                    .max()
                    .ifPresent(maxSmartValue -> {
                        // TODO: Retriever functions should be iterated and executed here.
                        reductionHistogram.add(maxSmartValue, 1d);
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
