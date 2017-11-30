package fortscale.ml.model.retriever;

import fortscale.common.feature.Feature;
import fortscale.ml.model.*;
import fortscale.ml.model.retriever.smart_data.ContextSmartValueData;
import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordData;
import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordDataContainer;
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

import static fortscale.ml.model.retriever.smart_data.SmartAccumulationFlattener.flattenSmartRecordToSmartAggrData;


public class AccumulatedContextSmartValueRetriever extends AbstractDataRetriever {

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

    public AccumulatedContextSmartValueRetriever(AccumulatedContextSmartValueRetrieverConf dataRetrieverConf, SmartAccumulationDataReader accumulationDataReader, SmartRecordConfService smartRecordConfService, FactoryService<IContextSelector> contextSelectorFactoryService, ModelStore modelStore, Duration oldestAllowedModelDurationDiff, SmartWeightsScorerAlgorithm smartWeightsScorerAlgorithm) {
        super(dataRetrieverConf);
        this.smartRecordConfName = dataRetrieverConf.getSmartRecordConfName();
        this.modelStore = modelStore;
        this.oldestAllowedModelDurationDiff = oldestAllowedModelDurationDiff;
        Assert.hasText(this.smartRecordConfName, "smart record conf name must be defined for retriever");
        this.accumulationDataReader = accumulationDataReader;
        this.contextSelectorFactoryService = contextSelectorFactoryService;
        this.smartRecordConf = smartRecordConfService.getSmartRecordConf(this.smartRecordConfName);
        this.weightsModelName = dataRetrieverConf.getWeightsModelName();
        Assert.hasText(weightsModelName, String.format("weightsModelName must be defined for retriever name=%s", this.smartRecordConfName));

        this.smartWeightsScorerAlgorithm = smartWeightsScorerAlgorithm;
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime) {
        if (modelConfService == null) {
            modelConfService = DynamicModelConfServiceContainer.getModelConfService();
            this.weightsModelConf = this.modelConfService.getModelConf(weightsModelName);
            Assert.notNull(this.weightsModelConf, String.format("modelConf must be defined for retriever name=%s", this.smartRecordConfName));
        }
        Instant startTime = getStartTime(endTime).toInstant();
        Instant endTimeInstant = endTime.toInstant();
        ModelDAO weightsModelDAO = getWeightsModelDao(endTimeInstant);
        Instant weightsModelEndTime = weightsModelDAO.getEndTime();
        SmartWeightsModel smartWeightsModel = (SmartWeightsModel) weightsModelDAO.getModel();

        List<AccumulatedSmartRecord> accumulatedSmartRecords = accumulationDataReader.findAccumulatedEventsByContextIdAndStartTimeRange(smartRecordConfName, contextId, startTime, endTimeInstant);
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainerList = flattenSmartRecordToSmartAggrData(accumulatedSmartRecords);

        Map<Instant, Double> startInstantToSmartValue = new HashMap<>();
        smartAggregatedRecordDataContainerList.forEach(recordsDataContainer -> {
            double smartValue = calculateSmartValue(recordsDataContainer, smartWeightsModel);
            startInstantToSmartValue.put(recordsDataContainer.getStartTime(), smartValue);
        });

        ContextSmartValueData contextSmartValueData = new ContextSmartValueData(startInstantToSmartValue, weightsModelEndTime);
        return new ModelBuilderData(contextSmartValueData);
    }

    private double calculateSmartValue(SmartAggregatedRecordDataContainer recordsDataContainer, SmartWeightsModel smartWeightsModel) {
        List<ClusterConf> clusterConfs = smartWeightsModel.getClusterConfs();
        List<SmartAggregatedRecordData> aggregatedRecordsData = recordsDataContainer.getSmartAggregatedRecordsData();
        return smartWeightsScorerAlgorithm.calculateScore(aggregatedRecordsData, clusterConfs);
    }

    private ModelDAO getWeightsModelDao(Instant endTimeInstant) {
        Instant oldestAllowedModelTime = endTimeInstant.minus(oldestAllowedModelDurationDiff);
        ModelDAO latestBeforeEventTimeAfterOldestAllowedModelDao = modelStore.getLatestBeforeEventTimeAfterOldestAllowedModelDaoSortedByEndTimeDesc(weightsModelConf, null, endTimeInstant, oldestAllowedModelTime, 1).get(0);
        return latestBeforeEventTimeAfterOldestAllowedModelDao;
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
