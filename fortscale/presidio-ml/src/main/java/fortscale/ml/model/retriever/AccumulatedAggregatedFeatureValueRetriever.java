package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;

import java.time.Duration;
import java.time.Instant;
import java.util.*;


public class AccumulatedAggregatedFeatureValueRetriever extends AbstractAggregatedFeatureValueRetriever {

    private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;


    public AccumulatedAggregatedFeatureValueRetriever(AccumulatedAggregatedFeatureValueRetrieverConf config, AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader, AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService) {
        super(config, aggregatedFeatureEventsConfService, true);
        this.aggregationEventsAccumulationDataReader = aggregationEventsAccumulationDataReader;
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

        TreeMap<Instant, Double> startInstantToValue = new TreeMap<>();

        accumulatedAggregationFeatureRecords.forEach(accumulatedRecord -> {
                    accumulatedRecord.getAggregatedFeatureValues().entrySet().stream().
                            forEach(entry -> startInstantToValue.put(accumulatedRecord.getStartInstant().plus(Duration.ofHours(entry.getKey())), entry.getValue()));
                }
        );

        return startInstantToValue;
    }


}
