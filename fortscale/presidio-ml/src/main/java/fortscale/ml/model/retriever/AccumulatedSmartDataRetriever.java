package fortscale.ml.model.retriever;

import fortscale.common.feature.Feature;
import fortscale.ml.model.ModelBuilderData;
import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordDataContainer;
import fortscale.ml.model.retriever.smart_data.SmartWeightsModelBuilderData;
import fortscale.ml.model.selector.AccumulatedSmartContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.time.TimeRange;
import org.springframework.util.Assert;
import presidio.ade.domain.record.accumulator.AccumulatedSmartRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataReader;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static fortscale.ml.model.retriever.smart_data.SmartAccumulationFlattener.flattenSmartRecordToSmartAggrData;

/**
 * Created by barak_schuster on 30/08/2017.
 */
public class AccumulatedSmartDataRetriever extends AbstractDataRetriever {

    private final FactoryService<IContextSelector> contextSelectorFactoryService;
    private final SmartAccumulationDataReader dataReader;
    private final String smartRecordConfName;
    private final long partitionsResolutionInSeconds;

    public AccumulatedSmartDataRetriever(AccumulatedSmartDataRetrieverConf dataRetrieverConf, FactoryService<IContextSelector> contextSelectorFactoryService, SmartAccumulationDataReader dataReader, SmartRecordConfService smartRecordConfService) {
        super(dataRetrieverConf);
        this.contextSelectorFactoryService = contextSelectorFactoryService;
        this.dataReader = dataReader;
        this.smartRecordConfName = dataRetrieverConf.getSmartRecordConfName();
        this.partitionsResolutionInSeconds = dataRetrieverConf.getPartitionsResolutionInSeconds();
        SmartRecordConf smartRecordConf = smartRecordConfService.getSmartRecordConf(smartRecordConfName);
        long smartRecordConfDurationStrategyInSeconds = smartRecordConf.getFixedDurationStrategy().toDuration().getSeconds();
        String message = String.format("partitionsResolutionInSeconds=%d must be multiplication of smartRecordConfDurationStrategyInSeconds=%d. fix retrieverConf=%s or smartConf=%s",
                partitionsResolutionInSeconds, smartRecordConfDurationStrategyInSeconds, dataRetrieverConf.getFactoryName(), smartRecordConfName);
        Assert.isTrue(partitionsResolutionInSeconds % smartRecordConfDurationStrategyInSeconds == 0, message);
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime) {
        Assert.isNull(contextId,"context must be null");
        Instant startTime = getStartTime(endTime).toInstant();
        Instant endTimeInstant = endTime.toInstant();
        TimeRange timeRange = new TimeRange(startTime, endTimeInstant);

        IContextSelector contextSelector = contextSelectorFactoryService.getProduct(
                new AccumulatedSmartContextSelectorConf(smartRecordConfName));

        Set<String> contextIds = contextSelector.getContexts(timeRange);
        List<SmartAggregatedRecordDataContainer> finalSmartAggregatedRecordDataContainers = new ArrayList<>();

        Set<Long> timePartitions = new HashSet<>();

        for (String context : contextIds) {
            List<AccumulatedSmartRecord> accumulatedSmartRecords = getAccumulatedSmartRecords(smartRecordConfName, context, startTime, endTimeInstant);

            List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers = flattenSmartRecordToSmartAggrData(accumulatedSmartRecords);
            timePartitions.addAll(calcPartitions(accumulatedSmartRecords));
            for (SmartAggregatedRecordDataContainer smartAggregatedRecordDataContainer : smartAggregatedRecordDataContainers) {
                finalSmartAggregatedRecordDataContainers.add(smartAggregatedRecordDataContainer);
            }
        }

        int amountOfContextIds = contextIds.size();
        long numOfPartitions = timePartitions.size();
        return new ModelBuilderData(new SmartWeightsModelBuilderData(amountOfContextIds,finalSmartAggregatedRecordDataContainers,numOfPartitions));
    }



    private List<AccumulatedSmartRecord> getAccumulatedSmartRecords(String smartConfName, String contextId, Instant startTime, Instant endTime) {
        return dataReader.findAccumulatedEventsByContextIdAndStartTimeRange(
                    smartConfName, contextId, startTime, endTime);
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime, Feature feature) {
        throw new UnsupportedOperationException(String.format(
                "%s does not support retrieval of a single feature",
                getClass().getSimpleName()));
    }

    protected Set<Long> calcPartitions(List<AccumulatedSmartRecord> data) {
        return data.stream().map(x -> (x.getStartInstant().getEpochSecond() / partitionsResolutionInSeconds) * partitionsResolutionInSeconds).collect(Collectors.toSet());
    }

    @Override
    public String getContextId(Map<String, String> context) {
        return null;
    }

    @Override
    public Set<String> getEventFeatureNames() {
        return Collections.singleton(SmartRecord.AGGREGATION_RECORDS_FIELD);
    }

    @Override
    public List<String> getContextFieldNames() {
        return Collections.emptyList();
    }
}
