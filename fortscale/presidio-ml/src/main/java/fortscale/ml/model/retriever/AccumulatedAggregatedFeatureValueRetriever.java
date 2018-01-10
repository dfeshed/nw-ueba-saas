package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.model.metrics.MaxContinuousModelRetrieverMetricsContainer;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;

import java.time.Duration;
import java.time.Instant;
import java.util.*;


public class AccumulatedAggregatedFeatureValueRetriever extends AbstractAggregatedFeatureValueRetriever {

    private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;
    private MaxContinuousModelRetrieverMetricsContainer maxContinuousModelRetrieverMetricsContainer;


    public AccumulatedAggregatedFeatureValueRetriever(AccumulatedAggregatedFeatureValueRetrieverConf config,
                                                      AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader,
                                                      AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
                                                      MaxContinuousModelRetrieverMetricsContainer maxContinuousModelRetrieverMetricsContainer) {
        super(config, aggregatedFeatureEventsConfService, true);
        this.aggregationEventsAccumulationDataReader = aggregationEventsAccumulationDataReader;
        this.maxContinuousModelRetrieverMetricsContainer = maxContinuousModelRetrieverMetricsContainer;
    }

    @Override
    protected TreeMap<Instant, Double> readAggregatedFeatureValues(AggregatedFeatureEventConf aggregatedFeatureEventConf,
                                                                   String contextId,
                                                                   Date startTime,
                                                                   Date endTime) {

        List<AccumulatedAggregationFeatureRecord> accumulatedAggregationFeatureRecords = aggregationEventsAccumulationDataReader.findAccumulatedEventsByContextIdAndStartTimeRange(
                aggregatedFeatureEventConf.getName(),
                contextId,
                getStartTime(endTime).toInstant(),
                endTime.toInstant()
        );
        maxContinuousModelRetrieverMetricsContainer.updateReadMetric(accumulatedAggregationFeatureRecords.size());

        TreeMap<Instant, Double> startInstantToValue = new TreeMap<>();

        accumulatedAggregationFeatureRecords.forEach(accumulatedRecord -> {
                    accumulatedRecord.getAggregatedFeatureValues().entrySet().stream().
                            forEach(entry -> startInstantToValue.put(accumulatedRecord.getStartInstant().plus(Duration.ofHours(entry.getKey())), entry.getValue()));
                }
        );

        return startInstantToValue;
    }


}
