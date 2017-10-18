package fortscale.ml.model.retriever;

import fortscale.common.feature.Feature;
import fortscale.ml.model.ModelBuilderData;
import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordDataContainer;
import fortscale.ml.model.retriever.smart_data.SmartWeightsModelBuilderData;
import fortscale.ml.model.selector.AccumulatedSmartContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.time.TimeRange;
import org.springframework.util.Assert;
import presidio.ade.domain.record.accumulator.AccumulatedSmartRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataReader;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    public AccumulatedSmartDataRetriever(AccumulatedSmartDataRetrieverConf dataRetrieverConf, FactoryService<IContextSelector> contextSelectorFactoryService, SmartAccumulationDataReader dataReader) {
        super(dataRetrieverConf);
        this.contextSelectorFactoryService = contextSelectorFactoryService;
        this.dataReader = dataReader;
        this.smartRecordConfName = dataRetrieverConf.getSmartRecordConfName();
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime) {
        Assert.isNull(contextId,"context must be not null");
        Instant startTime = getStartTime(endTime).toInstant();
        Instant endTimeInstant = endTime.toInstant();
        TimeRange timeRange = new TimeRange(startTime, endTimeInstant);

        IContextSelector contextSelector = contextSelectorFactoryService.getProduct(
                new AccumulatedSmartContextSelectorConf(smartRecordConfName));

        Set<String> contextIds = contextSelector.getContexts(timeRange);
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers = contextIds.stream()
                .flatMap(context -> readSmartAggregatedRecordData(smartRecordConfName, context, startTime, endTimeInstant).stream())
                .collect(Collectors.toList());
        int amountOfContextIds = contextIds.size();
        long distinctDays = calcNumOfDistinctDays(smartAggregatedRecordDataContainers);
        return new ModelBuilderData(new SmartWeightsModelBuilderData(amountOfContextIds,smartAggregatedRecordDataContainers,distinctDays));
    }

    private List<SmartAggregatedRecordDataContainer> readSmartAggregatedRecordData(
            String smartConfName, String contextId, Instant startTime, Instant endTime) {

        List<AccumulatedSmartRecord> accumulatedSmartRecords = dataReader.findAccumulatedEventsByContextIdAndStartTimeRange(
                smartConfName, contextId, startTime, endTime);
        return flattenSmartRecordToSmartAggrData(startTime,accumulatedSmartRecords);
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime, Feature feature) {
        throw new UnsupportedOperationException(String.format(
                "%s does not support retrieval of a single feature",
                getClass().getSimpleName()));
    }

    protected long calcNumOfDistinctDays(List<SmartAggregatedRecordDataContainer> data) {
        return data.stream().map(x->x.getStartTime().truncatedTo(ChronoUnit.DAYS)).distinct().count();
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
