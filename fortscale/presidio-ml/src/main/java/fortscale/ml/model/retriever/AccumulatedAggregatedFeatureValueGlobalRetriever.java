package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.model.metrics.AccumulatedAggregatedFeatureValueGlobalRetrieverMetricsContainer;
import org.apache.commons.lang3.Validate;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class AccumulatedAggregatedFeatureValueGlobalRetriever extends AbstractAggregatedFeatureValueRetriever {

    private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;
    private AccumulatedAggregatedFeatureValueGlobalRetrieverMetricsContainer metricsContainer;


    public AccumulatedAggregatedFeatureValueGlobalRetriever(AccumulatedAggregatedFeatureValueGlobalRetrieverConf config,
                                                            AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader,
                                                            AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
                                                            AccumulatedAggregatedFeatureValueGlobalRetrieverMetricsContainer metricsContainer) {
        super(config, aggregatedFeatureEventsConfService, true);
        this.aggregationEventsAccumulationDataReader = aggregationEventsAccumulationDataReader;
        this.metricsContainer = metricsContainer;
    }

    @Override
    protected TreeMap<Instant, Double> readAggregatedFeatureValues(AggregatedFeatureEventConf aggregatedFeatureEventConf,
                                                                   String contextId,
                                                                   Date startTime,
                                                                   Date endTime) {
        Validate.isTrue(contextId == null, "contextId is expected to be null.");
        List<AccumulatedAggregationFeatureRecord> accumulatedAggregationFeatureRecords = aggregationEventsAccumulationDataReader.findAccumulatedEventsByStartTimeRange(
                aggregatedFeatureEventConf.getName(),
                getStartTime(endTime).toInstant(),
                endTime.toInstant()
        );
        metricsContainer.updateReadMetric(accumulatedAggregationFeatureRecords.size());

        TreeMap<Instant, Double> startInstantToValue = new TreeMap<>();

        //ret.compute((long)occurrence, (k,v) -> v == null ? 1 : v+1);
        accumulatedAggregationFeatureRecords.forEach(accumulatedRecord -> {
                    accumulatedRecord.getAggregatedFeatureValues().
                            forEach((key,value) ->
                                    startInstantToValue.compute(
                                            accumulatedRecord.getStartInstant().plus(Duration.ofHours(key)),
                                            (k, v) -> v == null ? value : Math.max(v, value)));
                }
        );

        return startInstantToValue;
    }

    @Override
    public List<String> getContextFieldNames() {
        return Collections.emptyList();
    }

    @Override
    public String getContextId(Map<String, String> context) {
        return null;
    }


}
